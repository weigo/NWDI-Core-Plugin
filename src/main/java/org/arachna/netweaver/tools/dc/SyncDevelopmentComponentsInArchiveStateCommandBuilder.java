/**
 *
 */
package org.arachna.netweaver.tools.dc;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;

/**
 * Builder for DCTool synchronize commands for a development configurations development components.
 *
 * @author Dirk Weigenand
 */
final class SyncDevelopmentComponentsInArchiveStateCommandBuilder extends AbstractDCToolCommandBuilder {
    /**
     * Provides templates for the various sync/unsync dc commands.
     */
    private final SyncDcCommandTemplate templateProvider;

    /**
     *
     */
    private final Collection<DevelopmentComponent> components;

    /**
     * create a builder for development component listing and syncing commands.
     *
     * @param developmentConfiguration
     *            development configuration to synchronize development components for.
     * @param components
     *            collection of development components to generate DC tool synchronization statements for.
     */
    SyncDevelopmentComponentsInArchiveStateCommandBuilder(final DevelopmentConfiguration developmentConfiguration,
        final Collection<DevelopmentComponent> components) {
        super(developmentConfiguration);
        this.components = components;
        templateProvider = SyncDcCommandTemplate.create(developmentConfiguration.getJdkHomeAlias());
    }

    /**
     * Create dctool commands for synchronizing development components.
     *
     * @return list of generated commands.
     */
    @Override
    protected List<String> executeInternal() {
        return synchronizeCompartmentsInArchiveMode();
    }

    /**
     * Create commands for synchronizing DCs in archive mode.
     *
     * @return collection of commands for synchronizing DCs in archive mode.
     */
    private List<String> synchronizeCompartmentsInArchiveMode() {
        final List<String> commands = new LinkedList<String>();

        for (final DevelopmentComponent component : components) {
            commands.add(createSyncArchiveDCCommand(component));
        }

        Collections.sort(commands);

        return commands;
    }

    /**
     * Create command for synchronizing the given DC in inactive state.
     *
     * @param component
     *            DC to synchronize.
     * @return dctool command to synchronize the given DC
     */
    private String createSyncArchiveDCCommand(final DevelopmentComponent component) {
        return String.format(templateProvider.getSyncArchiveDcTemplate(), component.getCompartment().getName(), component.getName(),
            component.getVendor());
    }
}
