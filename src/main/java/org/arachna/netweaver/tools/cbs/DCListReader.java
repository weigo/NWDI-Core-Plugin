/**
 * 
 */
package org.arachna.netweaver.tools.cbs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;

/**
 * Parse output of a 'listdcs' command from a SAP NW CE CBS tool.
 * 
 * @author Dirk Weigenand
 */
class DCListReader extends AbstractDCListReader {
    /**
     * group index of regexp for DC name.
     */
    private static final int DC_INDEX = 2;

    /**
     * group index of regexp for vendor.
     */
    private static final int VENDOR_INDEX = 3;

    /**
     * regular expression for parsing a line listing a DC and it's enclosing
     * compartment.
     */
    private final Pattern regexp = Pattern.compile("^\\d+\\s+(.*?)\\s+(.*?)\\s+(.*?)\\s+.*?$");

    /**
     * Create a new instance of a DCListReader using the given development
     * configuration and development component registry.
     * 
     * @param config
     *            development configuration to add read compartments to.
     * @param dcFactory
     *            registry for read development components.
     */
    DCListReader(final DevelopmentConfiguration config, final DevelopmentComponentFactory dcFactory) {
        super(config, dcFactory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void process(final String line) {
        final Matcher matcher = regexp.matcher(line);

        if (matcher.matches()) {
            final Compartment compartment = getCompartment(matcher.group(1));
            compartment.add(createDC(matcher.group(VENDOR_INDEX), matcher.group(DC_INDEX)));
        }
    }
}