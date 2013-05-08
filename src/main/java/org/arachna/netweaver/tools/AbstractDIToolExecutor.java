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
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     */
    public DIToolCommandExecutionResult execute(final DIToolCommandBuilder commandBuilder) throws IOException {
        final ProcStarter starter = launcher.launch();
        starter.pwd(workspace);
        starter.envs(createEnvironment());
        final ArgumentListBuilder toolCommand = createToolCommand();
        starter.cmds(toolCommand);
        final List<String> commands = commandBuilder.execute();
        starter.stdin(createCommandInputStream(commands));

        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        final ForkOutputStream tee = new ForkOutputStream(launcher.getListener().getLogger(), result);
        starter.stdout(tee);

        int exitCode = -1;

        try {
            exitCode = starter.join();
        }
        catch (final InterruptedException e) {
            result.write("\nOperation has been interrupted!".getBytes(Charset.defaultCharset()));
        }

        return new DIToolCommandExecutionResult(result.toString(Charset.defaultCharset().name()), exitCode);
    }

    /**
     * Create an <code>InputStream</code> containing the given NWDI tool
     * commands.
     * 
     * @param commands
     *            list of NWDI tool commands
     * @return <code>InputStream</code> containing the given NWDI tool commands.
     */
    private InputStream createCommandInputStream(final List<String> commands) {
        final StringBuilder cmds = new StringBuilder();
        final String separator = isUnix() ? "\n" : "\r\n";

        for (final String cmd : commands) {
            cmds.append(cmd).append(separator);
        }

        return new ByteArrayInputStream(cmds.toString().getBytes(Charset.defaultCharset()));
    }

    /**
     * Creates the command line for tool execution.
     * 
     * @return the created command line.
     */
    private ArgumentListBuilder createToolCommand() {
        final ArgumentListBuilder args = new ArgumentListBuilder();

        args.add(getFullyQualifiedToolCommand());

        if (!isUnix()) {
            args.prepend("cmd.exe", "/C");
            args.add("&&", "exit", "%%ERRORLEVEL%%");
        }

        return args;
    }

    /**
     * Determines whether this executor runs on Unix or not.
     * 
     * @return <code>true</code> iff this launcher runs on Unix,
     *         <code>false</code> otherwise.
     */
    protected boolean isUnix() {
        return launcher.isUnix();
    }

    /**
     * Generate the fully qualified command to be used to execute the dc tool.
     * 
     * @return fully qualified command to be used to execute the dc tool.
     */
    private String getFullyQualifiedToolCommand() {
        return getToolCommand().getAbsolutePath();
    }

    /**
     * Get a file instance for the tool command to use.
     * 
     * @return a file instance pointing to the tool command to use.
     */
    protected final File getToolCommand() {
        return new File(getToolPath(), getCommandName());
    }

    /**
     * Determine the name of the command to be executed. I.e. the name of the
     * shell script or batch file.
     * 
     * @return the name of the shell script or batch file to be executed.
     */
    protected abstract String getCommandName();

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
            environment.put("JDK_PROPERTY_NAME", alias.toString());
        }

        environment.put(JAVA_HOME, getDiToolDescriptor().getJavaHome(alias));

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
        log(Messages.duration_template(message,
            String.format("%f", (System.currentTimeMillis() - start) / A_THOUSAND_MSECS)));
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
        logger.println(message);
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
