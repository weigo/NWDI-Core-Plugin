/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import hudson.Util;

import java.util.Set;
import java.util.TreeSet;

import org.arachna.netweaver.dctool.JdkHomeAlias;
import org.arachna.netweaver.dctool.JdkHomePaths;

/**
 * @author Dirk Weigenand
 * 
 */
final class JdkHomePathsParser {
    /**
     * path specification for JDK homes.
     */
    private final String pathSpec;

    /**
     * Collect invalid paths here.
     */
    private final Set<String> invalidJdkHomeNames = new TreeSet<String>();

    /**
     * Create an instance of {@link JdkHomePathsParser} with the given path
     * specifications.
     * 
     * @param pathSpec
     *            JDK home path specifications.
     */
    JdkHomePathsParser(final String pathSpec) {
        this.pathSpec = pathSpec;
    }

    /**
     * Parse the path specifications and return the alias -- path mapping.
     * 
     * @return the alias -- path mapping.
     */
    JdkHomePaths parse() {
        final JdkHomePaths paths = new JdkHomePaths();

        final String[] pathDefs = Util.fixNull(this.pathSpec).split(",|;");

        for (String pathDef : pathDefs) {
            final String[] parts = pathDef.split("=");

            final JdkHomeAlias alias = JdkHomeAlias.fromString(Util.fixEmptyAndTrim(Util.fixNull(parts[0])));

            if (alias != null) {
                paths.add(alias, Util.fixEmptyAndTrim(Util.fixNull(parts[1])));
            }
            else {
                this.invalidJdkHomeNames.add(parts[0]);
            }
        }

        return paths;
    }

    /**
     * Indicate that there were invalid JDK home aliases found during parsing.
     * 
     * @return <code>boolean</code> when there were invalid JDK home aliases
     *         found during parsing, <code>false</code> otherwise.
     */
    boolean hasInvalidJdkHomeNames() {
        return !this.invalidJdkHomeNames.isEmpty();
    }

    /**
     * Returns the invalid home names formatted as a comma separated list.
     * 
     * @return the invalid home names formatted as a comma separated list.
     */
    String getInvalidJdkHomeNames() {
        final StringBuffer invalidHomes = new StringBuffer();

        for (String invalidHome : this.invalidJdkHomeNames) {
            invalidHomes.append(invalidHome).append(", ");
        }

        invalidHomes.setLength(invalidHomes.length() - 2);

        return invalidHomes.toString();
    }
}
