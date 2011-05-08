/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import java.util.ArrayList;
import java.util.List;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dctool.AbstractDCToolCommandBuilder;

/**
 * Builder for DCTool synchronize commands for a development configurations
 * development components.
 *
 * @author Dirk Weigenand
 */
final class SyncDevelopmentComponentsCommandBuilder extends AbstractDCToolCommandBuilder {
    /**
     * command for syncing an inactive DC.
     */
    protected static final String SYNC_INACTIVE_DC_COMMAND = "syncdc -s %s -n %s -v %s -m inactive -y;";

    /**
     * command for syncing all DCs in archive mode.
     */
    protected static final String SYNC_DCS_IN_ARCHIVE_MODE_COMMAND = "syncalldcs -s %s -m archive;";

    /**
     * command for syncing all DCs in source state.
     */
    protected static final String SYNC_ALL_DCS_COMMAND = "syncalldcs -s %s -m inactive -y;";

    /**
     * unsync one development component of a given compartment and vendor.
     */
    protected static final String UNSYNC_DC_COMMAND = "unsyncdc -s %s -n %s -v %s;";

    /**
     * unsync all development components of a given compartment.
     */
    protected static final String UNSYNC_ALL_DCS_COMMAND = "unsyncalldcs -s %s;";

    /**
     * Indicates whether DCs in source state should be unsynced before sync.
     */
    private final boolean cleanCopy;

    /**
     * create a builder for development component listing and syncing commands.
     *
     * @param developmentConfiguration
     *            development configuration to synchronize development
     *            components for.
     * @param cleanCopy
     *            indicate whether a clean copy of the workspace is needed.
     */
    SyncDevelopmentComponentsCommandBuilder(final DevelopmentConfiguration developmentConfiguration,
        final boolean cleanCopy) {
        super(developmentConfiguration);
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

        synchronizeCompartmentsInArchiveMode(commands);

        if (this.cleanCopy) {
            synchronizeDCsWithClean(commands);
        }
        else {
            synchronizeDCsNeedingRebuild(commands);
        }

        return commands;
    }

    /**
     * @param commands
     */
    private void synchronizeDCsNeedingRebuild(final List<String> commands) {
        DevelopmentConfiguration developmentConfiguration = this.getDevelopmentConfiguration();

        for (final Compartment compartment : developmentConfiguration.getCompartments(CompartmentState.Source)) {
            for (final DevelopmentComponent component : compartment.getDevelopmentComponents()) {
                if (component.isNeedsRebuild()) {
//                    commands.add(createUnsyncDCCommand(component));
                    commands.add(createSyncInactiveDCCommand(component));
                }
            }
        }
    }

    /**
     * @param commands
     */
    private void synchronizeDCsWithClean(final List<String> commands) {
        DevelopmentConfiguration developmentConfiguration = this.getDevelopmentConfiguration();

        for (final Compartment compartment : developmentConfiguration.getCompartments(CompartmentState.Source)) {
            commands.add(String.format(UNSYNC_ALL_DCS_COMMAND, compartment.getName()));
            commands.add(String.format(SYNC_ALL_DCS_COMMAND, compartment.getName()));
        }
    }

    /**
     * @param commands
     */
    private void synchronizeCompartmentsInArchiveMode(final List<String> commands) {
        DevelopmentConfiguration developmentConfiguration = this.getDevelopmentConfiguration();

        for (final Compartment compartment : developmentConfiguration.getCompartments(CompartmentState.Archive)) {
            commands.add(String.format(SYNC_DCS_IN_ARCHIVE_MODE_COMMAND, compartment.getName()));
        }
    }

    /**
     * Create command for synchronizing the given DC in inactive state.
     *
     * @param component
     *            DC to synchronize.
     * @return dctool command to synchronize the given DC
     */
    protected String createSyncInactiveDCCommand(final DevelopmentComponent component) {
        return String.format(SYNC_INACTIVE_DC_COMMAND, component.getCompartment().getName(), component.getName(),
            component.getVendor());
    }

    /**
     * Create command to unsynchronize the given DC.
     *
     * @param component
     *            DC to unsynchronize.
     * @return dctool command to unsynchronize the given DC
     */
    protected String createUnsyncDCCommand(final DevelopmentComponent component) {
        return String.format(UNSYNC_DC_COMMAND, component.getCompartment().getName(), component.getName(),
            component.getVendor());
    }
}
