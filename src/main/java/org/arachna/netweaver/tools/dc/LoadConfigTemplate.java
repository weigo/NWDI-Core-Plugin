/**
 * 
 */
package org.arachna.netweaver.tools.dc;

import java.util.HashMap;
import java.util.Map;

import org.arachna.netweaver.dc.types.JdkHomeAlias;

/**
 * Template for generating <code>loadconfig</code> and <code>timing</code>
 * commands.
 * 
 * @author Dirk Weigenand
 */
enum LoadConfigTemplate {
    /**
     * template for connecting and disconnecting a dctool to/from the NWDI.
     */
    V70("loadconfig -u %s -p %s -c \"%s\" -r \"%s\";\n", "exectime -m on;", "exit;"), V71(
        "loadconfig -u %s -p %s -v \"%s\" -l \"%s\"", "timing on", "exit");

    /**
     * mapping of {@link JdkHomeAlias}es to template matching the respective
     * NetWeaver version.
     */
    private static final Map<JdkHomeAlias, LoadConfigTemplate> VALUES =
        new HashMap<JdkHomeAlias, LoadConfigTemplate>() {
            {
                put(JdkHomeAlias.Jdk131Home, V70);
                put(JdkHomeAlias.Jdk142Home, V70);
                put(JdkHomeAlias.Jdk150Home, V71);
                put(JdkHomeAlias.Jdk160Home, V71);
            }
        };

    /**
     * template string for generation of a <code>loadconfig</code> command.
     */
    private final String loadConfigCommand;

    /**
     * template string for generation of a <code>timing</code> command.
     */
    private final String timingCommand;

    /**
     * exit command.
     */
    private final String exitCommand;

    /**
     * Create a <code>LoadConfigTemplate</code> with the given template strings
     * for <code>loadconfig</code> and <code>timing</code> commands.
     * 
     * @param loadConfigCommand
     *            template string for generation of a <code>loadconfig</code>
     *            command.
     * @param timingCommand
     *            template string for generation of a <code>timing</code>
     *            command.
     * @param exitCommand
     *            exit command.
     */
    LoadConfigTemplate(final String loadConfigCommand, final String timingCommand, final String exitCommand) {
        this.loadConfigCommand = loadConfigCommand;
        this.timingCommand = timingCommand;
        this.exitCommand = exitCommand;
    }

    /**
     * @return the loadConfigCommand
     */
    final String getLoadConfigCommand() {
        return loadConfigCommand;
    }

    /**
     * @return the timingCommand
     */
    final String getTimingCommand() {
        return timingCommand;
    }

    /**
     * @return the exitCommand
     */
    String getExitCommand() {
        return exitCommand;
    }

    /**
     * Create a template from the given {@link JdkHomeAlias}.
     * 
     * @param alias
     *            JDK home from which to map to template.
     * @return template for given alias.
     */
    static LoadConfigTemplate fromJdkHomeAlias(final JdkHomeAlias alias) {
        final LoadConfigTemplate template = VALUES.get(alias);

        if (template == null) {
            throw new IllegalStateException(String.format("Could not map JDK home alias %s to template!", alias));
        }

        return template;
    }
}
