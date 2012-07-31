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

class DCListReader {
    private final DevelopmentConfiguration config;
    private final DevelopmentComponentFactory dcFactory;
    private final Pattern regexp = Pattern.compile("^\\d+\\s+\\(.*?\\)\\s+\\(.*?\\)\\s+\\(.*?\\).*?$");

    DCListReader(final DevelopmentConfiguration config, final DevelopmentComponentFactory dcFactory) {
        this.config = config;
        this.dcFactory = dcFactory;
    }

    void execute(final Reader reader) {
        final BufferedReader buffer = new BufferedReader(reader);
        String line;

        try {
            while ((line = buffer.readLine()) != null) {
                final Matcher matcher = regexp.matcher(line);

                if (matcher.matches()) {
                    final Compartment compartment = config.getCompartment(matcher.group(1));
                    compartment.add(dcFactory.create(matcher.group(3), matcher.group(2)));
                }
            }
        }
        catch (final IOException e) {
            // FIXME: exception handling!
            e.printStackTrace();
        }
    }
}