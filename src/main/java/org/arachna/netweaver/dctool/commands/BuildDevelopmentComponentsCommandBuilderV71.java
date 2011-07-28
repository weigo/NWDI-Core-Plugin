/**
 *
 */
package org.arachna.netweaver.dctool.commands;

import java.util.Collection;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;

/**
 * Builder for 'builddc' commands for DC tool.
 *
 * @author Dirk Weigenand
 */
final class BuildDevelopmentComponentsCommandBuilderV71 extends BuildDevelopmentComponentsCommandBuilder {
    /**
     * command for building a DC.
     */
    private static final String BUILD_DC_COMMAND = "builddc -c %s -n %s -v %s -o";

    /**
     * Creates a <code>DevelopmentComponentBuilder</code> instance for the given
     * development components.
     *
     * @param config
     *            development configuration to use for executing dc tool
     *            commands.
     * @param components
     *            development components to create build dc commands for.
     */
    public BuildDevelopmentComponentsCommandBuilderV71(final DevelopmentConfiguration config,
        final Collection<DevelopmentComponent> components) {
        super(config, components);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String createBuildDcCommand(DevelopmentComponent component) {
        return String.format(BUILD_DC_COMMAND, component.getCompartment().getName(), component.getName(),
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
