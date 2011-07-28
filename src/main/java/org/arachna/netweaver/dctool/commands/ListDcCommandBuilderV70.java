/**
 *
 */
package org.arachna.netweaver.dctool.commands;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;

/**
 * A 'listdc' command generator for a development configuration.
 *
 * @author Dirk Weigenand
 */
final class ListDcCommandBuilderV70 extends ListDcCommandBuilder {

    /**
     * Create an instance of 'listdc' command generator for the given
     * development configuration.
     *
     * @param developmentConfiguration
     *            development configuration to generate 'listdc' commands for.
     */
    ListDcCommandBuilderV70(final DevelopmentConfiguration developmentConfiguration) {
        super(developmentConfiguration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    String getListDcCommand(Compartment compartment) {
        return String.format("listdcs -s %s;", compartment.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getExitCommand() {
        return "exit;";
    }
}
