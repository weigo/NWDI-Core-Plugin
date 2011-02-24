package org.arachna.netweaver.hudson.nwdi;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentComponentType;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;

/**
 * Reader for dctool 'listdcs' output of a compartment. The reader will read the
 * generated file and update the compartments with the found development
 * components (and register them).
 * 
 * @author Dirk Weigenand
 */
public final class DevelopmentComponentsReader extends AbstractDcToolOutputReader {
    /**
     * regexp for a compartment.
     */
    private final Pattern compartmentPattern = Pattern.compile("Compartment (.*?)\\s+\\((.*? state)\\).*?");

    /**
     * regexp for a development component name.
     */
    private final Pattern dcNamePattern = Pattern.compile("^\\d+\\)\\s+DC name:\\s+(.*?)");

    /**
     * regexp for a development components vendor.
     */
    private final Pattern dcVendorPattern = Pattern.compile("^\\s+DC vendor:\\s+(.*?)");

    /**
     * regexp for a development components type.
     */
    private final Pattern dcTypePattern = Pattern.compile("^\\s+DC type:\\s+(.*?)");

    /**
     * registry for development components.
     */
    private final DevelopmentComponentFactory dcFactory;

    /**
     * development configuration to use for updating the compartments with
     * development components.
     */
    private final DevelopmentConfiguration developmentConfiguration;

    /**
     * Creates a new reader for development components and their respective
     * compartments from the output of DC tool.
     * 
     * @param input
     *            <code>Reader</code> for reading DC tool output.
     * @param dcFactory
     *            registry for development components.
     * @param developmentConfiguration
     *            development configuration to use and update.
     */
    public DevelopmentComponentsReader(final Reader input, final DevelopmentComponentFactory dcFactory,
        final DevelopmentConfiguration developmentConfiguration) {
        super(input);
        this.dcFactory = dcFactory;
        this.developmentConfiguration = developmentConfiguration;
    }

    /**
     * Reads the DC tool generated output and updates the found compartments
     * with their associated development components.
     * 
     * @throws IOException
     *             wenn beim Einlesen der Daten ein Fehler auftritt
     */
    public void read() throws IOException {
        try {
            Compartment compartment = readCompartment();

            if (compartment != null) {

                while (true) {
                    final String name = this.readLine(this.dcNamePattern);
                    final String vendor = this.readLine(this.dcVendorPattern);

                    if (isEmpty(name) || isEmpty(vendor)) {
                        compartment = readCompartment();
                        continue;
                    }

                    if (compartment.isArchiveState()) {
                        compartment.add(this.dcFactory.create(vendor, name));
                    }
                    else {
                        this.readLine();
                        final String type = this.readLine(this.dcTypePattern);

                        compartment.add(this.dcFactory.create(vendor, name,
                            DevelopmentComponentType.fromString(type, "")));
                    }

                    this.readLine();
                }
            }
        }
        catch (final EOFException e) {
            // at EOF simply execute the finally clause...
        }
        finally {
            this.close();
        }
    }

    /**
     * determines whether given supplied is <code>null</code> or white space
     * only.
     * 
     * @param name
     *            string to test
     * @return <code>true</code> iff given string is null or contains only white
     *         space.
     */
    private boolean isEmpty(final String name) {
        return name == null || name.trim().length() == 0;
    }

    /**
     * Liest solange die Ausgabedatei ein, bis die Zeile die
     * Compartment-Eigenschaften enthält.
     * 
     * @return a compartment when one could be read a name for or
     *         <code>null</code>
     * @throws IOException
     *             wenn beim Lesen der Eingabe ein Fehler auftritt.
     */
    private Compartment readCompartment() throws IOException {
        Compartment compartment = null;
        String line;
        Matcher compartmentMatcher;

        while ((line = this.readLine()) != null) {
            compartmentMatcher = compartmentPattern.matcher(line);

            if (compartmentMatcher.matches()) {
                compartment = this.developmentConfiguration.getCompartment(compartmentMatcher.group(1));
                break;
            }
        }

        return compartment;
    }
}
