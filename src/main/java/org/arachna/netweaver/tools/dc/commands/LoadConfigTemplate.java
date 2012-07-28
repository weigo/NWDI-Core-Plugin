/**
 * 
 */
package org.arachna.netweaver.tools.dc.commands;

/**
 * Template for generating <code>loadconfig</code> and <code>timing</code> commands.
 * 
 * @author Dirk Weigenand
 */
enum LoadConfigTemplate {
    V70("loadconfig -u %s -p %s -c \"%s\" -r \"%s\";\n", "exectime -m on;"), V71(
        "loadconfig -u %s -p %s -v \"%s\" -l \"%s\"",
        "timing on\ntracefile dctool.trace\nspool dctool.spool");

    /**
     * template string for generation of a <code>loadconfig</code> command.
     */
    private final String loadConfigCommand;

    /**
     * template string for generation of a <code>timing</code> command.
     */
    private final String timingCommand;

    /**
     * Create a <code>LoadConfigTemplate</code> with the given template strings for <code>loadconfig</code> and <code>timing</code>
     * commands.
     * 
     * @param loadConfigCommand
     *            template string for generation of a <code>loadconfig</code> command.
     * @param timingCommand
     *            template string for generation of a <code>timing</code> command.
     */
    LoadConfigTemplate(final String loadConfigCommand, final String timingCommand) {
        this.loadConfigCommand = loadConfigCommand;
        this.timingCommand = timingCommand;

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
}
