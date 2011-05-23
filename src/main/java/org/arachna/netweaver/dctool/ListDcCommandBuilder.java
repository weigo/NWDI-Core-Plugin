/**
 *
 */
package org.arachna.netweaver.dctool;

import java.util.ArrayList;
import java.util.List;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;

/**
 * A 'listdc' command generator for a development configuration.
 *
 * @author Dirk Weigenand
 */
final class ListDcCommandBuilder extends AbstractDCToolCommandBuilder {

    /**
     * Create an instance of 'listdc' command generator for the given
     * development configuration.
     *
     * @param developmentConfiguration
     *            development configuration to generate 'listdc' commands for.
     */
    ListDcCommandBuilder(final DevelopmentConfiguration developmentConfiguration) {
        super(developmentConfiguration);
    }

    /**
     * Generate 'listdc' commands for all compartments contained in the
     * development configuration.
     *
     * {@inheritDoc}
     */
    @Override
    protected List<String> executeInternal() {
        final List<String> commands = new ArrayList<String>();


        for (final Compartment compartment : this.getDevelopmentConfiguration().getCompartments()) {
            commands.add(String.format("listdcs -s %s;", compartment.getName()));
        }

        return commands;
    }
}
