/**
 *
 */
package org.arachna.netweaver.dctool.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;

/**
 * Builder for DCTool synchronize commands for a development configurations
 * development components.
 * 
 * @author Dirk Weigenand
 */
final class SyncDevelopmentComponentsCommandBuilder extends AbstractDCToolCommandBuilder {
    /**
     * Provides templates for the various sync/unsync dc commands.
     */
    private final SyncDcCommandTemplate templateProvider;

    /**
     * Indicate that a clean copy of all development components is requested.
     */
    private final boolean cleanCopy;

    /**
     * create a builder for development component listing and syncing commands.
     * 
     * @param developmentConfiguration
     *            development configuration to synchronize development
     *            components for.
     * @param templateProvider
     *            provider of templates for dc tool command generation
     * @param cleanCopy
     *            indicate whether a clean copy of the workspace is needed.
     */
    SyncDevelopmentComponentsCommandBuilder(final DevelopmentConfiguration developmentConfiguration,
        final SyncDcCommandTemplate templateProvider, final boolean cleanCopy) {
        super(developmentConfiguration);
        this.templateProvider = templateProvider;
        this.cleanCopy = cleanCopy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.arachna.netweaver.dc.analyzer.dctool.DCToolCommandBuilder#execute ()
     */
    @Override
    protected List<String> executeInternal() {
        final List<String> commands = new ArrayList<String>();

        commands.addAll(synchronizeDCsNeedingRebuild());
        commands.addAll(synchronizeCompartmentsInArchiveMode());
        commands.add(getExitCommand());

        return commands;
    }

    /**
     * Create commands for synchronizing DCs that need to be rebuilt.
     * 
     * @return collection of commands for synchronizing DCs that need to be
     *         rebuilt.
     */
    protected Collection<String> synchronizeDCsNeedingRebuild() {
        final DevelopmentConfiguration developmentConfiguration = getDevelopmentConfiguration();
        final Collection<String> commands = new LinkedList<String>();

        for (final Compartment compartment : developmentConfiguration.getCompartments(CompartmentState.Source)) {
            for (final DevelopmentComponent component : compartment.getDevelopmentComponents()) {
                if (component.isNeedsRebuild()) {
                    commands.add(createUnsyncDCCommand(component));
                    commands.add(createSyncInactiveDCCommand(component));
                }
            }
        }

        return commands;
    }

    /**
     * Create commands for synchronizing DCs in archive mode.
     * 
     * When the property {@see #cleanCopy} is <code>false</code> only
     * compartments that do not match the regular expression of compartments to
     * ignore are synchronized.
     * 
     * @return collection of commands for synchronizing DCs in archive mode.
     */
    protected Collection<String> synchronizeCompartmentsInArchiveMode() {
        final DevelopmentConfiguration developmentConfiguration = getDevelopmentConfiguration();
        final Collection<Compartment> compartments = developmentConfiguration.getCompartments(CompartmentState.Archive);
        final Collection<String> commands = new ArrayList<String>(compartments.size());

        for (final Compartment compartment : compartments) {
            if (cleanCopy) {
                commands.add(createSyncDcsInArchiveModeCommand(compartment));
            }
            else if (!templateProvider.shouldCompartmentBeExcludedFromSynchronization(compartment)) {
                commands.add(createSyncDcsInArchiveModeCommand(compartment));
            }
        }

        return commands;
    }

    /**
     * Create a syncalldcs command for the given compartment.
     * 
     * @param compartment
     *            compartment to create syncalldcs command for
     * @return the created syncalldcs command
     */
    protected String createSyncDcsInArchiveModeCommand(final Compartment compartment) {
        return String.format(templateProvider.getSyncAllDcsInArchiveModeTemplate(), compartment.getName());
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

    /**
     * Return the 'exit' command to use.
     */
    @Override
    protected String getExitCommand() {
        return templateProvider.getExitTemplate();
    }
}
