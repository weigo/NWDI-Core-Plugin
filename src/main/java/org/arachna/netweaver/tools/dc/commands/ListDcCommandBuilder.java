/**
 *
 */
package org.arachna.netweaver.tools.dc.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;

/**
 * A 'listdc' command generator for a development configuration.
 * 
 * @author Dirk Weigenand
 */
abstract class ListDcCommandBuilder extends AbstractDCToolCommandBuilder {

    /**
     * Create an instance of 'listdc' command generator for the given development configuration.
     * 
     * @param developmentConfiguration
     *            development configuration to generate 'listdc' commands for.
     */
    ListDcCommandBuilder(final DevelopmentConfiguration developmentConfiguration) {
        super(developmentConfiguration);
    }

    /**
     * Generate 'listdc' commands for all compartments contained in the development configuration.
     * 
     * {@inheritDoc}
     */
    @Override
    protected List<String> executeInternal() {
        final Collection<Compartment> compartments = this.getDevelopmentConfiguration().getCompartments();
        final List<String> commands = new ArrayList<String>(compartments.size());

        for (final Compartment compartment : compartments) {
            commands.add(this.getListDcCommand(compartment));
        }

        return commands;
    }

    /**
     * Create a listdc command.
     * 
     * @param compartment
     *            the compartment to list DCs from.
     * 
     * @return the create listdc command
     */
    abstract String getListDcCommand(Compartment compartment);
}
