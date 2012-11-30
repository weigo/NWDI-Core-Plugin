/**
 * 
 */
package org.arachna.netweaver.tools.cbs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;

/**
 * Parse output of a 'listdcs' command from a SAP NW CBS tool (pre CE version).
 * 
 * @author Dirk Weigenand
 */
final class PreCeDCListReader extends AbstractDCListReader {
    /**
     * template for regexp to recognize compartments for a given build space.
     */
    private static final String COMPARTMENT_RX_TEMPLATE = "^.*?compartment '(.*?)' of build space '%s'$";

    /**
     * regular expression for parsing a line listing a DC and it's enclosing
     * compartment.
     */
    private final Pattern dcRegexp = Pattern.compile("^[0-9]+\\s+DC name:\\s+(.*?)\\s+DC vendor:\\s+(.*?)\\s*");

    /**
     * regexp to recognize compartments for a given build space.
     */
    private final Pattern compartmentRegex;

    /**
     * Create a new instance of a DCListReader using the given development
     * configuration and development component registry.
     * 
     * @param config
     *            development configuration to add read compartments to.
     * @param dcFactory
     *            registry for read development components.
     */
    PreCeDCListReader(final DevelopmentConfiguration config, final DevelopmentComponentFactory dcFactory) {
        super(config, dcFactory);
        compartmentRegex = Pattern.compile(String.format(COMPARTMENT_RX_TEMPLATE, config.getName()));
    }

    /**
     * Read the output of the CBS tool 'listdcs' command and parse the
     * compartments and development components and add them to the development
     * configuration/development component factory.
     * 
     * @param reader
     *            output of the CBS tool 'listdcs' command.
     */
    @Override
    void execute(final Reader reader) {
        final BufferedReader buffer = new BufferedReader(reader);
        String line;
        Compartment compartment = null;

        try {
            while ((line = buffer.readLine()) != null) {
                Matcher matcher = compartmentRegex.matcher(line);

                if (matcher.matches()) {
                    compartment = config.getCompartment(matcher.group(1));
                    continue;
                }

                matcher = dcRegexp.matcher(line);

                if (matcher.matches()) {
                    compartment.add(dcFactory.create(matcher.group(2), matcher.group(1)));
                }
            }
        }
        catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
