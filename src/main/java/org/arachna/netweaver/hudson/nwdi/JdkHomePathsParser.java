/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import java.util.Set;
import java.util.TreeSet;

import org.arachna.netweaver.dc.types.JdkHomeAlias;
import org.arachna.netweaver.dc.types.JdkHomePaths;

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
        this.pathSpec = pathSpec == null ? "" : pathSpec;
    }

    /**
     * Parse the path specifications and return the alias -- path mapping.
     * 
     * @return the alias -- path mapping.
     */
    JdkHomePaths parse() {
        final JdkHomePaths paths = new JdkHomePaths();
        final String[] pathDefs = pathSpec.split(",|;");

        for (final String pathDef : pathDefs) {
            final String[] parts = pathDef.split("=");

            final JdkHomeAlias alias = JdkHomeAlias.fromString(parts[0].trim());

            if (alias != null) {
                paths.add(alias, parts[1].trim());
            }
            else {
                invalidJdkHomeNames.add(parts[0]);
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
        return !invalidJdkHomeNames.isEmpty();
    }

    /**
     * Returns the invalid home names formatted as a comma separated list.
     * 
     * @return the invalid home names formatted as a comma separated list.
     */
    String getInvalidJdkHomeNames() {
        final StringBuffer invalidHomes = new StringBuffer();

        for (final String invalidHome : invalidJdkHomeNames) {
            invalidHomes.append(invalidHome).append(", ");
        }

        invalidHomes.setLength(invalidHomes.length() - 2);

        return invalidHomes.toString();
    }
}
