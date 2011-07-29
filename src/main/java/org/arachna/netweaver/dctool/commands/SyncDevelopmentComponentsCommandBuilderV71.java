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
final class SyncDevelopmentComponentsCommandBuilderV71 extends AbstractDCToolCommandBuilder {
    /**
     * command for syncing an inactive DC.
     */
    private static final String SYNC_INACTIVE_DC_COMMAND = "syncdc -c %s -n %s -v %s -m inactive -f";

    /**
     * command for syncing all DCs in archive mode.
     */
    private static final String SYNC_DCS_IN_ARCHIVE_MODE_COMMAND = "syncalldcs -c %s -m archive";

    /**
     * command for syncing all DCs in incative mode.
     */
    private static final String SYNC_DCS_IN_INACTIVE_MODE_COMMAND = "syncalldcs -c %s -m inactive";

    /**
     * unsync one development component of a given compartment and vendor.
     */
    private static final String UNSYNC_DC_COMMAND = "unsyncdc -c %s -n %s -v %s";

    /**
     * create a builder for development component listing and syncing commands.
     * 
     * @param developmentConfiguration
     *            development configuration to synchronize development
     *            components for.
     * @param cleanCopy
     *            indicate whether a clean copy of the workspace is needed.
     */
    SyncDevelopmentComponentsCommandBuilderV71(final DevelopmentConfiguration developmentConfiguration) {
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

        commands.add("tracefile synchronization.trace");
        synchronizeDCsNeedingRebuild(commands);
        synchronizeCompartmentsInArchiveMode(commands);

        return commands;
    }

    /**
     * @param commands
     */
    private void synchronizeDCsNeedingRebuild(final List<String> commands) {
        final DevelopmentConfiguration developmentConfiguration = getDevelopmentConfiguration();

        for (final Compartment compartment : developmentConfiguration.getCompartments(CompartmentState.Source)) {
            int cnt = 0;
            List<String> cmds = new LinkedList<String>();
            Collection<DevelopmentComponent> components = compartment.getDevelopmentComponents();

            for (final DevelopmentComponent component : components) {
                cnt++;

                if (component.isNeedsRebuild()) {
                    cmds.add(createUnsyncDCCommand(component));
                    cmds.add(createSyncInactiveDCCommand(component));
                }
            }

            if (components.size() == cnt) {
                commands.add(createSyncInactiveDCsCommand(compartment));
            }
            else {
                commands.addAll(cmds);
            }
        }
    }

    /**
     * @param commands
     */
    private void synchronizeCompartmentsInArchiveMode(final List<String> commands) {
        final DevelopmentConfiguration developmentConfiguration = getDevelopmentConfiguration();

        for (final Compartment compartment : developmentConfiguration.getCompartments(CompartmentState.Archive)) {
            commands.add(String.format(SYNC_DCS_IN_ARCHIVE_MODE_COMMAND, compartment.getName()));
        }
    }

    private String createSyncInactiveDCsCommand(Compartment compartment) {
        return String.format(SYNC_DCS_IN_INACTIVE_MODE_COMMAND, compartment.getName());
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
    protected String getExitCommand() {
        return "exit";
    }
}
