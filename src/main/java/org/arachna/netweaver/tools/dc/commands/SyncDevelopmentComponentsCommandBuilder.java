/**
 *
 */
package org.arachna.netweaver.tools.dc.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dc.types.PublicPartReference;

/**
 * Builder for DCTool synchronize commands for a development configurations development components.
 * 
 * @author Dirk Weigenand
 */
final class SyncDevelopmentComponentsCommandBuilder extends AbstractDCToolCommandBuilder {
    /**
     * names of build infrastructure compartments.
     */
    private static final Set<String> BUILD_INFRASTRUCTURE_COMPARTMENTS = new HashSet<String>(Arrays.asList(
        "sap.com_SAP_BUILDT_1", "sap.com_EP_BUILDT_1"));

    /**
     * Provides templates for the various sync/unsync dc commands.
     */
    private final SyncDcCommandTemplate templateProvider;

    /**
     * Indicate that a clean copy of all development components is requested.
     */
    private final boolean cleanCopy;

    /**
     * registry for development components.
     */
    private final DevelopmentComponentFactory dcFactory;

    /**
     * synchronize in inactive mode or in archive mode.
     */
    private final boolean syncSources;

    /**
     * create a builder for development component listing and syncing commands.
     * 
     * @param developmentConfiguration
     *            development configuration to synchronize development components for.
     * @param dcFactory
     *            registry for development components
     * @param syncSources
     *            synchronize in inactive mode or in archive mode
     * @param cleanCopy
     *            indicate whether a clean copy of the workspace is needed.
     */
    SyncDevelopmentComponentsCommandBuilder(final DevelopmentConfiguration developmentConfiguration,
        final DevelopmentComponentFactory dcFactory, final boolean syncSources, final boolean cleanCopy) {
        super(developmentConfiguration);
        this.dcFactory = dcFactory;
        templateProvider = SyncDcCommandTemplate.create(developmentConfiguration.getJdkHomeAlias());
        this.syncSources = syncSources;
        this.cleanCopy = cleanCopy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.arachna.netweaver.dc.analyzer.dctool.DCToolCommandBuilder#execute ()
     */
    @Override
    protected List<String> executeInternal() {
        final List<String> commands = new ArrayList<String>();

        if (syncSources) {
            commands.addAll(synchronizeDCsNeedingRebuild());
        }
        else {
            commands.addAll(synchronizeCompartmentsInArchiveMode());
        }

        return commands;
    }

    /**
     * Create commands for synchronizing DCs that need to be rebuilt.
     * 
     * @return collection of commands for synchronizing DCs that need to be rebuilt.
     */
    protected Collection<String> synchronizeDCsNeedingRebuild() {
        final DevelopmentConfiguration developmentConfiguration = getDevelopmentConfiguration();
        final Collection<String> commands = new LinkedList<String>();

        for (final Compartment compartment : developmentConfiguration.getCompartments(CompartmentState.Source)) {
            if (cleanCopy) {
                commands.add(createSyncDcsInInActiveModeCommand(compartment));
            }
            else {
                for (final DevelopmentComponent component : compartment.getDevelopmentComponents()) {
                    if (component.isNeedsRebuild()) {
                        commands.add(createUnsyncDCCommand(component));
                        commands.add(createSyncInactiveDCCommand(component));
                    }
                }
            }
        }

        return commands;
    }

    /**
     * Create command for synchronizing DCs in archive mode.
     * 
     * @param compartment
     *            the compartment whose components should be synchronized in archive mode
     * @return command for synchronizing DCs in inactive mode.
     */
    protected String createSyncDcsInInActiveModeCommand(final Compartment compartment) {
        return String.format(templateProvider.getSyncAllDcsInInactiveModeTemplate(), compartment.getName());
    }

    /**
     * Create commands for synchronizing DCs in archive mode.
     * 
     * When the property {@see #cleanCopy} is <code>false</code> only compartments that do not match the regular
     * expression of compartments to ignore are synchronized.
     * 
     * @return collection of commands for synchronizing DCs in archive mode.
     */
    protected Collection<String> synchronizeCompartmentsInArchiveMode() {
        final DevelopmentConfiguration developmentConfiguration = getDevelopmentConfiguration();
        final Collection<String> commands = new LinkedList<String>();

        final Set<DevelopmentComponent> usedDCs = new HashSet<DevelopmentComponent>();

        for (final Compartment compartment : developmentConfiguration.getCompartments(CompartmentState.Source)) {
            for (final DevelopmentComponent component : compartment.getDevelopmentComponents()) {
                collectUsedDCs(usedDCs, component);
            }
        }

        for (final DevelopmentComponent component : usedDCs) {
            commands.add(createSyncArchiveDCCommand(component));
        }

        if (cleanCopy) {
            for (final String compartmentName : BUILD_INFRASTRUCTURE_COMPARTMENTS) {
                final Compartment compartment = developmentConfiguration.getCompartment(compartmentName);

                if (compartment != null) {
                    commands.add(createSyncDcsInArchiveModeCommand(compartment));
                }
            }
        }

        return commands;
    }

    /**
     * @param usedDCs
     *            collect used DCs that should be synchronized here.
     * @param component
     *            look at used DCs of this component to determine which should be synchronized.
     */
    protected void collectUsedDCs(final Set<DevelopmentComponent> usedDCs, final DevelopmentComponent component) {
        DevelopmentComponent usedDC;
        for (final PublicPartReference reference : component.getUsedDevelopmentComponents()) {
            usedDC = dcFactory.get(reference);

            if (isUsedDCinArchiveStateAndNoBuildPlugin(usedDC)) {
                usedDCs.add(usedDC);
            }
        }
    }

    /**
     * Decide whether the given used DC should be synchronized.
     * 
     * <ul>
     * <li>it should be in archive state and</li>
     * <li>and no build plugin (i.e. not in a build infrastructure compartment</li>
     * .
     * </ul>
     * 
     * @param usedDC
     *            used DC to be inspected.
     * @return whether the DC should be included in DCs to be synchronized.
     */
    protected boolean isUsedDCinArchiveStateAndNoBuildPlugin(final DevelopmentComponent usedDC) {
        if (usedDC == null) {
            return false;
        }

        Compartment compartment = usedDC.getCompartment();

        if (compartment == null) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                String.format("%s has no compartment!", usedDC.toString()));
            return false;
        }

        return CompartmentState.Archive.equals(compartment.getState())
            && !BUILD_INFRASTRUCTURE_COMPARTMENTS.contains(compartment.getName());
    }

    /**
     * Create a syncalldcs command for the given compartment.
     * 
     * @param compartment
     *            compartment to create syncalldcs command for
     * @return the created syncalldcs command
     */
    protected String createSyncDcsInArchiveModeCommand(final Compartment compartment) {
        return String.format(templateProvider.getSyncAllDcsForGivenCompartmentInArchiveModeTemplate(),
            compartment.getName());
    }

    /**
     * Create command for synchronizing the given DC in inactive state.
     * 
     * @param component
     *            DC to synchronize.
     * @return dctool command to synchronize the given DC
     */
    protected String createSyncInactiveDCCommand(final DevelopmentComponent component) {
        return String.format(templateProvider.getSyncInactiveDcTemplate(), component.getCompartment().getName(),
            component.getName(), component.getVendor());
    }

    /**
     * Create command for synchronizing the given DC in inactive state.
     * 
     * @param component
     *            DC to synchronize.
     * @return dctool command to synchronize the given DC
     */
    protected String createSyncArchiveDCCommand(final DevelopmentComponent component) {
        return String.format(templateProvider.getSyncArchiveDcTemplate(), component.getCompartment().getName(),
            component.getName(), component.getVendor());
    }

    /**
     * Create command to unsynchronize the given DC.
     * 
     * @param component
     *            DC to unsynchronize.
     * @return dctool command to unsynchronize the given DC
     */
    protected String createUnsyncDCCommand(final DevelopmentComponent component) {
        return String.format(templateProvider.getUnsyncDcTemplate(), component.getCompartment().getName(),
            component.getName(), component.getVendor());
    }
}
