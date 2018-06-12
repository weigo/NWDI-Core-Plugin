/**
 *
 */
package org.arachna.netweaver.tools.dc;

import java.util.Collection;

import org.arachna.ant.AntHelper;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.tools.DIToolCommandBuilder;

/**
 * Factory for creation of DC tool commands.
 *
 * Encapsulates knowledge which command to create depending on the build variant used in a development configuration. This influences the DC
 * tool version to be used.
 *
 * @author Dirk Weigenand
 */
public final class CommandFactory {
    /**
     * {@link DevelopmentConfiguration} to use when creating dc tool commands.
     */
    private final DevelopmentConfiguration developmentConfiguration;

    /**
     * Create new CommandFactory instance with the given {@link DevelopmentConfiguration}.
     *
     * @param developmentConfiguration
     *            {@link DevelopmentConfiguration} to use when creating dc tool commands.
     */
    public CommandFactory(final DevelopmentConfiguration developmentConfiguration) {
        this.developmentConfiguration = developmentConfiguration;
    }

    /**
     * Creates a DC tool command builder for building synchronizing development components commands (for components in archive state).
     *
     * @param dcFactory factory for development components
     * @param antHelper helper for building paths
     * @param components
     *            development components to be used calculating the DCs to be synchronized
     * @return a command builder for creating 'syncdc' commands.
     */
    public DIToolCommandBuilder createSyncDevelopmentComponentsInArchiveStateCommandBuilder(final DevelopmentComponentFactory dcFactory,
        final AntHelper antHelper, final Collection<DevelopmentComponent> components) {
        return new SyncDevelopmentComponentsInArchiveStateCommandBuilder(developmentConfiguration, dcFactory, antHelper, components);
    }

    /**
     * Creates a DC tool command builder for building synchronizing development components commands (for components in source state).
     *
     * @param cleanCopy
     *            indicate whether sources should be synchronized unconditionally.
     *
     * @return a command builder for creating 'syncdc' commands.
     */
    public DIToolCommandBuilder createSyncDevelopmentComponentsInSourceStateCommandBuilder(final boolean cleanCopy) {
        return new SyncDevelopmentComponentsInSourceStateCommandBuilder(developmentConfiguration, cleanCopy);
    }

    /**
     * Creates a DC tool command builder for building build development components commands.
     *
     * @param affectedComponents
     *            collections of development components to build.
     * @return a command builder for creating 'builddc' commands.
     */
    public DIToolCommandBuilder createBuildDevelopmentComponentsCommandBuilder(final Collection<DevelopmentComponent> affectedComponents) {
        return new BuildDevelopmentComponentsCommandBuilder(developmentConfiguration, affectedComponents);
    }
}
