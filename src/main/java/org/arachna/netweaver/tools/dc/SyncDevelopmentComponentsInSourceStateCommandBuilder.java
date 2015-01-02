/**
 *
 */
package org.arachna.netweaver.tools.dc;

import java.util.LinkedList;
import java.util.List;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;

/**
 * DC tool command builder for synchronizing development components in source state.
 *
 * @author Dirk Weigenand
 */
public class SyncDevelopmentComponentsInSourceStateCommandBuilder extends AbstractDCToolCommandBuilder {
    /**
     * Flag to indicate whether development components should be synchronized regardless of its {@see DevelopmentComponent#isNeedsRebuild()}
     * state.
     */
    private final boolean cleanCopy;

    /**
     * provider of templates for (un-)sync DC commands.
     */
    private final SyncDcCommandTemplate templateProvider;

    /**
     * create a builder for development component listing and syncing commands.
     *
     * @param developmentConfiguration
     *            development configuration to synchronize development components for.
     * @param cleanCopy
     *            indicate whether a clean copy of the workspace is needed.
     */
    SyncDevelopmentComponentsInSourceStateCommandBuilder(final DevelopmentConfiguration developmentConfiguration, final boolean cleanCopy) {
        super(developmentConfiguration);
        this.cleanCopy = cleanCopy;
        templateProvider = SyncDcCommandTemplate.create(developmentConfiguration.getJdkHomeAlias());
    }

    @Override
    protected List<String> executeInternal() {
        return synchronizeDCsNeedingRebuild();
    }

    /**
     * Create commands for synchronizing DCs that need to be rebuilt.
     *
     * @return collection of commands for synchronizing DCs that need to be rebuilt.
     */
    private List<String> synchronizeDCsNeedingRebuild() {
        final DevelopmentConfiguration developmentConfiguration = getDevelopmentConfiguration();
        final List<String> commands = new LinkedList<String>();

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
     * Create command for synchronizing DCs in source state.
     *
     * @param compartment
     *            the compartment whose components should be synchronized in source state.
     * @return command for synchronizing DCs in source state.
     */
    private String createSyncDcsInInActiveModeCommand(final Compartment compartment) {
        return String.format(templateProvider.getSyncAllDcsInInactiveModeTemplate(), compartment.getName());
    }

    /**
     * Create command to unsynchronize the given DC.
     *
     * @param component
     *            DC to unsynchronize.
     * @return dctool command to unsynchronize the given DC
     */
    private String createUnsyncDCCommand(final DevelopmentComponent component) {
        return String.format(templateProvider.getUnsyncDcTemplate(), component.getCompartment().getName(), component.getName(),
            component.getVendor());
    }

    /**
     * Create command for synchronizing the given DC in inactive state.
     *
     * @param component
     *            DC to synchronize.
     * @return dctool command to synchronize the given DC
     */
    private String createSyncInactiveDCCommand(final DevelopmentComponent component) {
        return String.format(templateProvider.getSyncInactiveDcTemplate(), component.getCompartment().getName(), component.getName(),
            component.getVendor());
    }
}
