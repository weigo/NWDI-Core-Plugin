/**
 *
 */
package org.arachna.netweaver.dctool.commands;

import java.util.ArrayList;
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
abstract class SyncDevelopmentComponentsCommandBuilder extends AbstractDCToolCommandBuilder {
    /**
     * create a builder for development component listing and syncing commands.
     *
     * @param developmentConfiguration
     *            development configuration to synchronize development
     *            components for.
     * @param cleanCopy
     *            indicate whether a clean copy of the workspace is needed.
     */
    SyncDevelopmentComponentsCommandBuilder(final DevelopmentConfiguration developmentConfiguration) {
        super(developmentConfiguration);
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

        synchronizeCompartmentsInArchiveMode(commands);
        synchronizeDCsNeedingRebuild(commands);

        commands.add(getExitCommand());

        return commands;
    }

    /**
     * @param commands
     */
    private void synchronizeDCsNeedingRebuild(final List<String> commands) {
        final DevelopmentConfiguration developmentConfiguration = getDevelopmentConfiguration();

        for (final Compartment compartment : developmentConfiguration.getCompartments(CompartmentState.Source)) {
            for (final DevelopmentComponent component : compartment.getDevelopmentComponents()) {
                if (component.isNeedsRebuild()) {
                    commands.add(createUnsyncDCCommand(component));
                    commands.add(createSyncInactiveDCCommand(component));
                }
            }
        }
    }

    /**
     * @param commands
     */
    private void synchronizeCompartmentsInArchiveMode(final List<String> commands) {
        final DevelopmentConfiguration developmentConfiguration = getDevelopmentConfiguration();

        for (final Compartment compartment : developmentConfiguration.getCompartments(CompartmentState.Archive)) {
            commands.add(createSyncDcsInArchiveModeCommand(compartment));
        }
    }

    /**
     * Create a syncalldcs command for the given compartment.
     *
     * @param compartment
     *            compartment to create syncalldcs command for
     * @return the created syncalldcs command
     */
    protected abstract String createSyncDcsInArchiveModeCommand(final Compartment compartment);

    /**
     * Create command for synchronizing the given DC in inactive state.
     *
     * @param component
     *            DC to synchronize.
     * @return dctool command to synchronize the given DC
     */
    protected abstract String createSyncInactiveDCCommand(final DevelopmentComponent component);

    /**
     * Create command to unsynchronize the given DC.
     *
     * @param component
     *            DC to unsynchronize.
     * @return dctool command to unsynchronize the given DC
     */
    protected abstract String createUnsyncDCCommand(final DevelopmentComponent component);
}
