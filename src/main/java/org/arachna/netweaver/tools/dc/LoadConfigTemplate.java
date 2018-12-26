/**
 *
 */
package org.arachna.netweaver.tools.dc;

import org.arachna.netweaver.dc.types.JdkHomeAlias;

/**
 * Template for generating <code>loadconfig</code> and <code>timing</code> commands.
 *
 * @author Dirk Weigenand
 */
enum LoadConfigTemplate {
    /**
     * template for connecting and disconnecting a dctool to/from the NWDI (NetWeaver 7.0).
     */
    V70("loadconfig -u %s -p %s -c \"%s\" -r \"%s\";\n", "exectime -m on;", "spool spool.txt;", "tracefile tracefile.txt;", "exit;"),
    /**
     * template for connecting and disconnecting a dctool to/from the NWDI (NetWeaver 7.1 and onwards).
     */
    V71("loadconfig -u %s -p %s -v \"%s\" -l \"%s\"", "timing on", "spool spool.txt", "tracefile tracefile.txt", "exit");

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
     * command to enable writing into a spool file.
     */
    private final String spoolCommand;

    /**
     * command to enable writing into a trace file.
     */
    private final String tracefileCommand;

    /**
     * Create a <code>LoadConfigTemplate</code> with the given template strings for <code>loadconfig</code> and <code>timing</code>
     * commands.
     *
     * @param loadConfigCommand template string for generation of a <code>loadconfig</code> command.
     * @param timingCommand     template string for generation of a <code>timing</code> command.
     * @param spoolCommand      command to enable writing into a spool file.
     * @param tracefileCommand  command to enable writing into a trace file.
     * @param exitCommand       exit command.
     */
    LoadConfigTemplate(final String loadConfigCommand, final String timingCommand, final String spoolCommand,
                       final String tracefileCommand, final String exitCommand) {
        this.loadConfigCommand = loadConfigCommand;
        this.timingCommand = timingCommand;
        this.spoolCommand = spoolCommand;
        this.tracefileCommand = tracefileCommand;
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
     * @return the spoolCommand
     */
    public String getSpoolCommand() {
        return spoolCommand;
    }

    /**
     * @return the tracefileCommand
     */
    public String getTracefileCommand() {
        return tracefileCommand;
    }

    /**
     * Create a template from the given {@link JdkHomeAlias}.
     *
     * @param alias JDK home from which to map to template.
     * @return template for given alias.
     */
    static LoadConfigTemplate fromJdkHomeAlias(final JdkHomeAlias alias) {
        LoadConfigTemplate template = null;

        switch (alias) {
            case Jdk131Home:
            case Jdk142Home:
                template = V70;
                break;

            case Jdk150Home:
            case Jdk160Home:
            case Jdk180Home:
                template = V71;
                break;

            default:
                throw new IllegalStateException(String.format("Could not map JDK home alias %s to template!", alias));
        }

        return template;
    }
}
