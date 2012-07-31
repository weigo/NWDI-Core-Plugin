/**
 *
 */
package org.arachna.netweaver.tools.dc.commands;

import java.util.Collection;

import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.tools.DIToolCommandBuilder;

/**
 * Factory for creation of DC tool commands.
 * 
 * Encapsulates knowledge which command to create depending on the build variant
 * used in a development configuration. This influences the DC tool version to
 * be used.
 * 
 * @author Dirk Weigenand
 */
public final class CommandFactory {
    /**
     * {@link DevelopmentConfiguration} to use when creating dc tool commands.
     */
    private final DevelopmentConfiguration developmentConfiguration;

    /**
     * Create new CommandFactory instance with the given
     * {@link DevelopmentConfiguration}.
     * 
     * @param developmentConfiguration
     *            {@link DevelopmentConfiguration} to use when creating dc tool
     *            commands.
     */
    public CommandFactory(final DevelopmentConfiguration developmentConfiguration) {
        this.developmentConfiguration = developmentConfiguration;
    }

    /**
     * Creates a DC tool command builder for building synchronizing development
     * components commands.
     * 
     * @param configuration
     *            development configuration to synchronize.
     * @param dcFactory
     *            registry for development components
     * @param syncSources
     *            synchronize DCs only in {@link CompartmentState.Source} when
     *            <code>true</code>, in {@link CompartmentState.Archive} when
     *            <code>false</code>
     * @param cleanCopy
     *            indicate that the current build should operate on a fresh copy
     *            from the DTR.
     * @return a command builder for creating 'syncdc' commands.
     */
    public DIToolCommandBuilder createSyncDevelopmentComponentsCommandBuilder(
        final DevelopmentComponentFactory dcFactory, final boolean syncSources, final boolean cleanCopy) {
        return new SyncDevelopmentComponentsCommandBuilder(developmentConfiguration, dcFactory, syncSources, cleanCopy);
    }

    /**
     * Creates a DC tool command builder for building build development
     * components commands.
     * 
     * @param affectedComponents
     *            collections of development components to build.
     * @return a command builder for creating 'builddc' commands.
     */
    public DIToolCommandBuilder createBuildDevelopmentComponentsCommandBuilder(
        final Collection<DevelopmentComponent> affectedComponents) {
        return new BuildDevelopmentComponentsCommandBuilder(developmentConfiguration, affectedComponents);
    }
}
