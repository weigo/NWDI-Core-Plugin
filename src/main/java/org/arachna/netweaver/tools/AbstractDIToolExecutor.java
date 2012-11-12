/**
 * 
 */
package org.arachna.netweaver.tools;

import hudson.FilePath;
import hudson.Launcher;
import hudson.Launcher.ProcStarter;
import hudson.util.ArgumentListBuilder;
import hudson.util.ForkOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dc.types.JdkHomeAlias;

/**
 * Base class for executors of NWDI tools (cbstool, dctool, etc.).
 * 
 * @author Dirk Weigenand
 */
public abstract class AbstractDIToolExecutor {
    /**
     * 
     */
    private static final String JAVA_HOME = "JAVA_HOME";

    /**
     * 1000 milliseconds.
     */
    private static final float A_THOUSAND_MSECS = 1000f;

    /**
     * {@link DevelopmentConfiguration} the development configuration to use.
     */
    private final DevelopmentConfiguration developmentConfiguration;

    /**
     * the launcher to use executing the DC tool.
     */
    private final Launcher launcher;

    /**
     * the workspace where the DC tool should be executed.
     */
    private final FilePath workspace;

    /**
     * descriptor for various parameters needed for DC tool execution.
     */
    private final DIToolDescriptor diToolDescriptor;

    /**
     * Logger.
     */
    private final PrintStream logger;

    /**
     * create DC tool executor with the given command line generator and given
     * command build.
     * 
     * @param launcher
     *            the launcher to use executing the DC tool.
     * @param workspace
     *            the workspace where the DC tool should be executed.
     * @param diToolDescriptor
     *            descriptor for various parameters needed for DC tool
     *            execution.
     * @param developmentConfiguration
     *            {@link DevelopmentConfiguration} to use executing the DC tool.
     */
    public AbstractDIToolExecutor(final Launcher launcher, final FilePath workspace,
        final DIToolDescriptor diToolDescriptor, final DevelopmentConfiguration developmentConfiguration) {
        this.launcher = launcher;
        this.workspace = workspace;
        this.diToolDescriptor = diToolDescriptor;
        this.developmentConfiguration = developmentConfiguration;
        logger = launcher.getListener().getLogger();
    }

    /**
     * Execute dc tool with the given {@link DIToolCommandBuilder}.
     * 
     * @param commandBuilder
     *            builder for dc tool commands
     * @return content of log file created by the executed dc tool.
     * @throws IOException
     *             might be thrown be the {@link ProcStarter} used to execute
     *             the DC tool commands.
     * @throws InterruptedException
     *             when the user canceled the action.
     */
    public DIToolCommandExecutionResult execute(final DIToolCommandBuilder commandBuilder) throws IOException,
        InterruptedException {
        final ProcStarter starter = launcher.launch();
        starter.pwd(workspace);
        starter.envs(createEnvironment());
        final ArgumentListBuilder toolCommand = createToolCommand(launcher.isUnix());
        starter.cmds(toolCommand);
        final List<String> commands = commandBuilder.execute();
        starter.stdin(createCommandInputStream(commands, launcher.isUnix()));

        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        final ForkOutputStream tee = new ForkOutputStream(launcher.getListener().getLogger(), result);
        starter.stdout(tee);
        final ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        starter.stderr(stderr);

        final int exitCode = starter.join();
        System.err.println(stderr.toString());

        final DIToolCommandExecutionResult executionResult =
            new DIToolCommandExecutionResult(result.toString(), exitCode);

        if (!executionResult.isExitCodeOk()) {
            Logger.getLogger("NWDI-Core-Plugin").fine(
                String.format("Executing the following commands using %s failed!\n%s\n", toolCommand.toString(),
                    commands.toString()));
        }

        return executionResult;
    }

    /**
     * Create an <code>InputStream</code> containing the given NWDI tool
     * commands.
     * 
     * @param commands
     *            list of NWDI tool commands
     * @param isUnix
     *            indicates whether we run on a Unix OS (<code>true</code>) or
     *            not (<code>false</code>).
     * @return <code>InputStream</code> containing the given NWDI tool commands.
     */
    private InputStream createCommandInputStream(final List<String> commands, final boolean isUnix) {
        final StringBuilder cmds = new StringBuilder();
        final String separator = isUnix ? "\n" : "\r\n";

        for (final String cmd : commands) {
            cmds.append(cmd).append(separator);
        }

        return new ByteArrayInputStream(cmds.toString().getBytes());
    }

    /**
     * Creates the command line for tool execution.
     * 
     * @param isUnix
     *            indicate whether to run on a unixoid OS or Windows.
     * @return the created command line.
     */
    private ArgumentListBuilder createToolCommand(final boolean isUnix) {
        final ArgumentListBuilder args = new ArgumentListBuilder();

        args.add(getFullyQualifiedToolCommand(isUnix));

        if (!isUnix) {
            args.prepend("cmd.exe", "/C");
            args.add("&&", "exit", "%%ERRORLEVEL%%");
        }

        return args;
    }

    /**
     * Generate the fully qualified command to be used to execute the dc tool.
     * 
     * @param isUnix
     *            indicate whether the platform to run on is Unix(oid) or
     *            Windows.
     * @return fully qualified command to be used to execute the dc tool.
     */
    private String getFullyQualifiedToolCommand(final boolean isUnix) {
        return new File(getToolPath(), getCommandName(isUnix)).getAbsolutePath();
    }

    /**
     * Determine the name of the command to be executed. I.e. the name of the
     * shell script or batch file.
     * 
     * @param isUnix
     *            <code>true</code> if command is executed on a unix system,
     *            <code>false</code> otherwise.
     * @return the name of the shell script or batch file to be executed.
     */
    protected abstract String getCommandName(boolean isUnix);

    /**
     * Get the path to the tool to be executed.
     * 
     * @return path under NWDITOOLLIB folder containing the tool.
     */
    protected abstract File getToolPath();

    /**
     * Prepare the environment variables for the launcher.
     * 
     * @return the map containing the environment variable name mapping to their
     *         corresponding values.
     */
    private Map<String, String> createEnvironment() {
        final Map<String, String> environment = new HashMap<String, String>();
        environment.put("NWDITOOLLIB", getDiToolDescriptor().getNwdiToolLibrary() + File.separatorChar + "lib");

        final JdkHomeAlias alias = developmentConfiguration.getJdkHomeAlias();

        if (alias != null) {
            environment.put(JAVA_HOME, getDiToolDescriptor().getJavaHome(alias));
            environment.put("JDK_PROPERTY_NAME", alias.toString());
        }
        else {
            environment.put(JAVA_HOME, System.getProperty("java.home"));

        }

        return environment;
    }

    /**
     * Determine the time in seconds passed since the given start time and log
     * it using the message given.
     * 
     * @param start
     *            begin of action whose duration should be logged.
     * @param message
     *            message to log.
     */
    protected final void duration(final long start, final String message) {
        final long duration = System.currentTimeMillis() - start;

        log(String.format("%s (%f sec).\n", message, duration / A_THOUSAND_MSECS));
    }

    /**
     * @return the developmentConfiguration
     */
    protected final DevelopmentConfiguration getDevelopmentConfiguration() {
        return developmentConfiguration;
    }

    /**
     * Log the given message.
     * 
     * @param message
     *            the message to log.
     */
    protected final void log(final String message) {
        logger.append(message);
    }

    protected final String getNwdiToolLibrary() {
        return getDiToolDescriptor().getNwdiToolLibrary();
    }

    /**
     * @return the diToolDescriptor
     */
    protected final DIToolDescriptor getDiToolDescriptor() {
        return diToolDescriptor;
    }
}
