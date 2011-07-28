/**
 *
 */
package org.arachna.netweaver.dctool.commands;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;

/**
 * Builder for DCTool synchronize commands for a development configurations
 * development components.
 *
 * @author Dirk Weigenand
 */
final class SyncDevelopmentComponentsCommandBuilderV70 extends SyncDevelopmentComponentsCommandBuilder {
    /**
     * command for syncing an inactive DC.
     */
    private static final String SYNC_INACTIVE_DC_COMMAND = "syncdc -s %s -n %s -v %s -m inactive -y;";

    /**
     * command for syncing all DCs in archive mode.
     */
    private static final String SYNC_DCS_IN_ARCHIVE_MODE_COMMAND = "syncalldcs -s %s -m archive;";

    /**
     * unsync one development component of a given compartment and vendor.
     */
    private static final String UNSYNC_DC_COMMAND = "unsyncdc -s %s -n %s -v %s;";

    /**
     * create a builder for development component listing and syncing commands.
     *
     * @param developmentConfiguration
     *            development configuration to synchronize development
     *            components for.
     * @param cleanCopy
     *            indicate whether a clean copy of the workspace is needed.
     */
    SyncDevelopmentComponentsCommandBuilderV70(final DevelopmentConfiguration developmentConfiguration) {
        super(developmentConfiguration);
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected String createSyncDcsInArchiveModeCommand(Compartment compartment) {
        return String.format(SYNC_DCS_IN_ARCHIVE_MODE_COMMAND, compartment.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getExitCommand() {
        return "exit;";
    }
}
