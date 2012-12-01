/**
 * 
 */
package org.arachna.netweaver.tools.cbs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;

/**
 * Abstract base class for parsers of output of a cbstool's 'listdcs' command.
 * 
 * @author Dirk Weigenand
 */
abstract class AbstractDCListReader {
    /**
     * development configuration to add read compartments to.
     */
    protected final DevelopmentConfiguration config;

    /**
     * registry for read development components.
     */
    protected final DevelopmentComponentFactory dcFactory;

    /**
     * Create a new instance of a DCListReader using the given development
     * configuration and development component registry.
     * 
     * @param config
     *            development configuration to add read compartments to.
     * @param dcFactory
     *            registry for read development components.
     */
    AbstractDCListReader(final DevelopmentConfiguration config, final DevelopmentComponentFactory dcFactory) {
        this.config = config;
        this.dcFactory = dcFactory;
    }

    /**
     * Read the output of the CBS tool 'listdcs' command and parse the
     * compartments and development components and add them to the development
     * configuration/development component factory.
     * 
     * @param reader
     *            output of the CBS tool 'listdcs' command.
     */
    void execute(final Reader reader) {
        final BufferedReader buffer = new BufferedReader(reader);
        String line;

        try {
            while ((line = buffer.readLine()) != null) {
                process(line);
            }
        }
        catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Process a line of output from the CBS tool 'listdcs' command.
     * 
     * @param line
     *            line of output from the CBS tool 'listdcs' command.
     */
    protected abstract void process(final String line);
}
