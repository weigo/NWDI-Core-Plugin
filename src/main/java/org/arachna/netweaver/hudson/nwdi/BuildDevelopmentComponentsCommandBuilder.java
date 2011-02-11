/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dctool.AbstractDCToolCommandBuilder;

/**
 * Builder for 'builddc' commands for DC tool.
 * 
 * @author Dirk Weigenand
 */
public final class BuildDevelopmentComponentsCommandBuilder extends AbstractDCToolCommandBuilder {
    /**
     * command for building a DC.
     */
    private static final String BUILD_DC_COMMAND = "builddc -s %s -n %s -v %s -o;";

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
     * 
     */
    public BuildDevelopmentComponentsCommandBuilder(final DevelopmentConfiguration config, final Collection<DevelopmentComponent> components) {
        super(config);
        this.components.addAll(components);
    }

    /**
     * Erzeugt Befehle für das Bauen der Entwicklungskomponenten und führt dann das DC-Tool aus.
     * 
     * @return Erzeugte 'builddc' Kommandos.
     */
    @Override
    protected List<String> executeInternal() {
        final List<String> commands = new ArrayList<String>();

        for (final DevelopmentComponent component : components) {
            commands.add(String.format(BUILD_DC_COMMAND, component.getCompartment().getName(), component.getName(), component.getVendor()));
        }

        return commands;
    }
}
