package org.arachna.netweaver.tools.dc.commands;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;

/**
 * Reader for dctool 'listdcs' output of a compartment. The reader will read the generated file and update the compartments with the found
 * development components (and register them).
 * 
 * @author Dirk Weigenand
 */
public final class DevelopmentComponentsReader71 extends AbstractDcToolOutputReader {
    /**
     * regexp for a development component name, vendor.
     */
    private final Pattern dcNamePattern = Pattern.compile("^\\d+\\s+(.*?)\\s+(.*?)\\s+(.*?)$");

    /**
     * registry for development components.
     */
    private final DevelopmentComponentFactory dcFactory;

    /**
     * development configuration to use for updating the compartments with development components.
     */
    private final DevelopmentConfiguration developmentConfiguration;

    /**
     * Creates a new reader for development components and their respective compartments from the output of DC tool.
     * 
     * @param input
     *            <code>Reader</code> for reading DC tool output.
     * @param dcFactory
     *            registry for development components.
     * @param developmentConfiguration
     *            development configuration to use and update.
     */
    public DevelopmentComponentsReader71(final Reader input, final DevelopmentComponentFactory dcFactory,
        final DevelopmentConfiguration developmentConfiguration) {
        super(input);
        this.dcFactory = dcFactory;
        this.developmentConfiguration = developmentConfiguration;
    }

    /**
     * Reads the DC tool generated output and updates the found compartments with their associated development components.
     * 
     * @throws IOException
     *             when an i/o error occurs reading the DC listing
     */
    @Override
    public void read() throws IOException {
        try {
            String line = null;
            Matcher matcher = null;

            while ((line = this.readLine()) != null) {
                matcher = this.dcNamePattern.matcher(line);

                if (!matcher.matches()) {
                    continue;
                }

                Compartment compartment = this.developmentConfiguration.getCompartment(matcher.group(3));
                compartment.add(this.dcFactory.create(matcher.group(2), matcher.group(1)));
            }
        }
        catch (final IOException e) {
            throw e;
        }
        finally {
            this.close();
        }
    }
}
