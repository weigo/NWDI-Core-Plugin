/**
 *
 */
package org.arachna.netweaver.dctool;

import java.io.PrintStream;
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
final class BuildDevelopmentComponentsCommandBuilder extends AbstractDCToolCommandBuilder {
    /**
     * command for building a DC.
     */
    private static final String BUILD_DC_COMMAND = "builddc -s %s -n %s -v %s -o;";

    /**
     * list of development components to create build commands for.
     */
    private final List<DevelopmentComponent> components = new ArrayList<DevelopmentComponent>();

    /**
     * Logger to use for log messages.
     */
    private final PrintStream logger;

    /**
     * Creates a <code>DevelopmentComponentBuilder</code> instance for the given
     * development components.
     *
     * @param config
     *            development configuration to use for executing dc tool
     *            commands.
     * @param components
     *            development components to create build dc commands for.
     * @param logger
     *            Logger to use for log messages.
     *
     */
    public BuildDevelopmentComponentsCommandBuilder(final DevelopmentConfiguration config,
        final Collection<DevelopmentComponent> components, PrintStream logger) {
        super(config);
        this.logger = logger;
        this.components.addAll(components);
    }

    /**
     * Erzeugt Befehle für das Bauen der Entwicklungskomponenten und führt
     * dann das DC-Tool aus.
     *
     * @return Erzeugte 'builddc' Kommandos.
     */
    @Override
    protected List<String> executeInternal() {
        final List<String> commands = new ArrayList<String>();

        for (final DevelopmentComponent component : components) {
            // FIXME: guard against NPE when compartment is not set!!!
            if (component.getCompartment() != null) {
                commands.add(String.format(BUILD_DC_COMMAND, component.getCompartment().getName(), component.getName(),
                    component.getVendor()));
            }
            else {
                logger
                    .append(String.format("%s/%s has no compartment set!", component.getVendor(), component.getName()));
            }
        }

        return commands;
    }
}
