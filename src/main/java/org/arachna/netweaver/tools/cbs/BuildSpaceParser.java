/**
 *
 */
package org.arachna.netweaver.tools.cbs;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for output of cbstool 'listbuildspaces' command.
 * 
 * @author Dirk Weigenand
 */
final class BuildSpaceParser {

    /**
     * Regular expression to match output of the 'listbuildspaces' cbstool command.
     */
    private final Pattern buildSpaceRegex = Pattern.compile("^\\d*\\s*([a-zA-Z0-9]{3}_[a-zA-Z0-9\\-]+_D)$");

    /**
     * List of build spaces as obtained from the 'listbuildspaces' cbstool command.
     */
    private final String buildSpaceList;

    /**
     * Create a new <code>BuildSpaceParser</code> instance with the given output of the 'listbuildspaces' cbstool command.
     * 
     * @param buildSpaceList
     *            output of the 'listbuildspaces' cbstool command.
     */
    BuildSpaceParser(final String buildSpaceList) {
        this.buildSpaceList = buildSpaceList;

    }

    /**
     * Parses the output of the 'listbuildspaces' cbstool command and extracts names of development build spaces.
     * 
     * @return a collection of recognized (development) build space names (those ending with _D).
     */
    Collection<String> parse() {
        final List<String> buildSpaces = new LinkedList<String>();

        for (final String output : buildSpaceList.split("\n")) {
            final Matcher matcher = buildSpaceRegex.matcher(output.trim());

            if (matcher.matches()) {
                buildSpaces.add(matcher.group(1));
            }
        }

        return buildSpaces;
    }
}
