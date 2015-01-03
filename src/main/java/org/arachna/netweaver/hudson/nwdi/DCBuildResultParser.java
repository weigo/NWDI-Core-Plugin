/**
 * 
 */
package org.arachna.netweaver.hudson.nwdi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;

/**
 * Parser for output of the DC tool 'builddc' command.
 * 
 * @author Dirk Weigenand
 */
public class DCBuildResultParser {
    /**
     * Regex matching failed builds.
     */
    private final Pattern failedBuildResultRegex = Pattern
        .compile("0 build\\(s\\) succeeded \\(or yield warnings\\), 1 build\\(s\\) failed\\.$");

    /**
     * regex matching DC name to be built and its containing compartment.
     */
    private final Pattern dcNameAndCompartmentPattern = Pattern
        .compile("\"Build of DC \"(.*?)\" from  compartment \"(.*?)\" based on sync mode \"SYNCHED_AS_INACTIVE_SOURCE\"\\s+$");

    /**
     * development configuration to determine development components by their
     * name and containing compartment.
     */
    private final DevelopmentConfiguration developmentConfiguration;

    /**
     * Create a parser for output of a sequence of DC tools 'builddc' commands.
     * 
     * @param developmentConfiguration
     *            a development configuration to determine DCs parsed from the
     *            output.
     */
    public DCBuildResultParser(final DevelopmentConfiguration developmentConfiguration) {
        this.developmentConfiguration = developmentConfiguration;
    }

    /**
     * Parse the given build log and determine whether there were any failed
     * builds.
     * 
     * @param buildLog
     *            The log of a build.
     * 
     * @return a container of build results.
     */
    public BuildResults parse(final Reader buildLog) {
        final BuildResults results = new BuildResults();

        final BufferedReader reader = new BufferedReader(buildLog);
        String line;
        String compartmentName = null;
        String dcName = null;

        try {
            while (null != (line = reader.readLine())) {
                final Matcher m = dcNameAndCompartmentPattern.matcher(line);

                if (m.matches()) {
                    dcName = m.group(1);
                    compartmentName = m.group(2);
                }

                if (dcName != null && compartmentName != null && failedBuildResultRegex.matcher(line).matches()) {
                    final Compartment compartment = developmentConfiguration.getCompartment(compartmentName);
                    final DevelopmentComponent component = compartment.getDevelopmentComponent(dcName);

                    if (component == null) {
                        throw new IllegalStateException(String.format("Compartment %s does not contain development component '%s'!",
                            compartmentName, dcName));
                    }

                    results.addFailedBuildFor(component);
                    // reset
                    compartmentName = null;
                    dcName = null;
                }
            }
        }
        catch (final IOException e) {
            throw new IllegalStateException(e);
        }
        finally {
            try {
                reader.close();
            }
            catch (final IOException e) {
                throw new IllegalStateException(e);
            }
        }

        return results;
    }

    /**
     * Collection of build results.
     * 
     * @author Dirk Weigenand
     */
    public class BuildResults {
        /**
         * collection of DCs that could not be built successfully.
         */
        private final List<DevelopmentComponent> dcsWithBuildErrors = new ArrayList<DevelopmentComponent>();

        /**
         * returns whether there were build errors or not.
         * 
         * @return <code>true</code>, when there were build errors,
         *         <code>false</code> otherwise.
         */
        public boolean hasBuildErrors() {
            return !getDcsWithBuildErrors().isEmpty();
        }

        /**
         * Add a development component to the list of DCs whose build failed.
         * 
         * @param component
         *            development component whose build failed.
         */
        void addFailedBuildFor(final DevelopmentComponent component) {
            this.getDcsWithBuildErrors().add(component);
        }

        /**
         * Get list of DCs whose build failed.
         * 
         * @return the dcsWithBuildErrors
         */
        public List<DevelopmentComponent> getDcsWithBuildErrors() {
            return dcsWithBuildErrors;
        }
    }
}
