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
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;

/**
 * Parse the output of a pre SAP NW CE CBS tool 'listcompartments' command.
 * 
 * @author Dirk Weigenand
 */
final class ListCompartmentsReader {
    /**
     * regexp group for compartment name.
     */
    private static final int COMPARTMENT_NAME = 1;

    /**
     * regexp group for compartment state.
     */
    private static final int COMPARTMENT_STATE = 2;

    /**
     * development component to register read compartments with.
     */
    private final DevelopmentConfiguration config;

    /**
     * regular expression to parse compartments from output of
     * 'listcompartments' command.
     */
    private final Pattern compartmentPattern = Pattern.compile("^(.*?)\\s+\\((.*?state)\\)$");

    /**
     * Create a new instance of a ListCompartmentsReader with the given
     * development configuration to register read compartments with.
     * 
     * @param config
     *            development configuration to register read compartments with.
     */
    ListCompartmentsReader(final DevelopmentConfiguration config) {
        this.config = config;
    }

    /**
     * Parse output of 'listcompartments' command and register found
     * compartments with the development configuration given to the constructor.
     * 
     * @param reader
     *            output of 'listcompartments' command.
     */
    void execute(final Reader reader) {
        final BufferedReader buffer = new BufferedReader(reader);
        String line;

        try {
            while ((line = buffer.readLine()) != null) {
                registerCompartmentIfLineMatches(line);
            }
        }
        catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Parse line of output of 'listcompartments' command and register a
     * compartment with the development configuration if the given line matches
     * the regular expression for compartments.
     * 
     * @param line
     *            line of output of 'listcompartments' command.
     */
    protected void registerCompartmentIfLineMatches(final String line) {
        final Matcher matcher = compartmentPattern.matcher(line);

        if (matcher.matches()) {
            config.add(Compartment.create(matcher.group(COMPARTMENT_NAME),
                CompartmentState.fromString(matcher.group(COMPARTMENT_STATE).replaceAll("\\s+", " "))));
        }
    }

}
