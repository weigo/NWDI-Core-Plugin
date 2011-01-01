/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import java.util.ArrayList;
import java.util.List;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dctool.AbstractDCToolCommandBuilder;

/**
 * Builder für 'builddc'-Kommando zum Bauen von Entwicklungskomponenten über das
 * DC-Tool.
 * 
 * @author Dirk Weigenand
 */
public final class BuildDevelopmentComponentsCommandBuilder extends AbstractDCToolCommandBuilder {
    /**
     * command for building a DC.
     */
    private static final String BUILD_DC_COMMAND = "builddc -s %s -n %s -v %s -o;";

    /**
     * registry for development components.
     */
    private final DevelopmentComponentFactory dcFactory;

    /**
     * Erzeugt einen <code>DevelopmentComponentBuilder</code>.
     * 
     * @param dcFactory
     *            registry for development components.
     * @param developmentConfiguration
     *            Entwicklungskonfiguration
     */
    public BuildDevelopmentComponentsCommandBuilder(final DevelopmentComponentFactory dcFactory,
        final DevelopmentConfiguration developmentConfiguration) {
        super(developmentConfiguration);
        this.dcFactory = dcFactory;
    }

    /**
     * Erzeugt Befehle für das Bauen der Entwicklungskomponenten und führt dann
     * das DC-Tool aus.
     * 
     * @return Erzeugte 'builddc' Kommandos.
     */
    @Override
    protected List<String> executeInternal() {
        final DependencySorter sorter = new DependencySorter(this.dcFactory, getDevelopmentComponentsNeedingARebuild());

        return this.generateBuildCommands(sorter.determineBuildSequence());
    }

    /**
     * Filter development components of this development configuration by their
     * <code>needsRebuild</code> property.
     * 
     * @return list of development components needing a rebuild.
     */
    private List<DevelopmentComponent> getDevelopmentComponentsNeedingARebuild() {
        final List<DevelopmentComponent> components = new ArrayList<DevelopmentComponent>();

        for (final Compartment compartment : this.getDevelopmentConfiguration().getCompartments()) {
            if (CompartmentState.Source.equals(compartment.getState())) {
                for (final DevelopmentComponent component : compartment.getDevelopmentComponents()) {
                    if (component.isNeedsRebuild()) {
                        components.add(component);
                    }
                }
            }
        }

        return components;
    }

    /**
     * Erzeugt builddc Befehle für die übergebenen Entwicklungskomponenten.
     * 
     * @param components
     *            Entwicklungskomponenten für die 'builddc'-Kommandos erzeugt
     *            werden sollen.
     * @return 'builddc'-Kommandos für die übergebenen Entwicklungskomponenten
     */
    private List<String> generateBuildCommands(final List<DevelopmentComponent> components) {
        final List<String> commands = new ArrayList<String>();

        for (final DevelopmentComponent component : components) {
            commands.add(String.format(BUILD_DC_COMMAND, component.getCompartment().getName(), component.getName(),
                component.getVendor()));
        }

        return commands;
    }
}
