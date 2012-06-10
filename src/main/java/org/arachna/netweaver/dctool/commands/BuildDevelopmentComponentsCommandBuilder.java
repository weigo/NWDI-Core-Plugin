/**
 *
 */
package org.arachna.netweaver.dctool.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;

/**
 * Builder for 'builddc' commands for DC tool.
 * 
 * @author Dirk Weigenand
 */
abstract class BuildDevelopmentComponentsCommandBuilder extends AbstractDCToolCommandBuilder {
    /**
     * list of development components to create build commands for.
     */
    private final List<DevelopmentComponent> components = new ArrayList<DevelopmentComponent>();

    /**
     * Creates a <code>DevelopmentComponentBuilder</code> instance for the given development components.
     * 
     * @param config
     *            development configuration to use for executing dc tool commands.
     * @param components
     *            development components to create build dc commands for.
     */
    public BuildDevelopmentComponentsCommandBuilder(final DevelopmentConfiguration config,
        final Collection<DevelopmentComponent> components) {
        super(config);
        this.components.addAll(components);
    }

    /**
     * Create collection of builddc commands.
     * 
     * @return created list of 'builddc' commands.
     */
    @Override
    protected final List<String> executeInternal() {
        final List<String> commands = new ArrayList<String>();

        for (final DevelopmentComponent component : components) {
            if (component.getCompartment() != null) {
                commands.add(this.createBuildDcCommand(component));
            }
            else {
                throw new RuntimeException(String.format("%s/%s has no compartment set!", component.getVendor(),
                    component.getName()));
            }
        }

        return commands;
    }

    /**
     * Create command for building a development component for the given DC.
     * 
     * @param component
     *            development component to create builddc command for.
     * @return the created builddc command
     */
    protected abstract String createBuildDcCommand(DevelopmentComponent component);
}