/**
 *
 */
package org.arachna.netweaver.dctool;

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
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dctool.commands.CommandFactory;
import org.arachna.netweaver.dctool.commands.DCToolCommandBuilder;

/**
 * Execute a DC Tool.
 * 
 * @author Dirk Weigenand
 */
public final class DCToolCommandExecutor {
    /**
     * constant for environment variable 'JDK_PROPERTY_NAME'.
     */
    static final String JDK_PROPERTY_NAME = "JDK_PROPERTY_NAME";

    /**
     * constant for environment variable 'JAVA_HOME'.
     */
    static final String JAVA_HOME = "JAVA_HOME";

    /**
     * constant for environment variable 'NWDITOOLLIB'.
     */
    static final String NWDITOOLLIB = "NWDITOOLLIB";

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
    private final DCToolDescriptor dcToolDescriptor;

    /**
     * Logger.
     */
    private final PrintStream logger;

    /**
     * Factory for creating DC tool commands.
     */
    private final CommandFactory commandFactory = new CommandFactory();

    /**
     * create DC tool executor with the given command line generator and given command build.
     * 
     * @param launcher
     *            the launcher to use executing the DC tool.
     * @param workspace
     *            the workspace where the DC tool should be executed.
     * @param dcToolDescriptor
     *            descriptor for various parameters needed for DC tool execution.
     * @param developmentConfiguration
     *            {@link DevelopmentConfiguration} to use executing the DC tool.
     */
    public DCToolCommandExecutor(final Launcher launcher, final FilePath workspace,
        final DCToolDescriptor dcToolDescriptor, final DevelopmentConfiguration developmentConfiguration) {
        this.launcher = launcher;
        this.workspace = workspace;
        this.dcToolDescriptor = dcToolDescriptor;
        this.developmentConfiguration = developmentConfiguration;
        logger = launcher.getListener().getLogger();
    }

    /**
     * Execute dc tool with the given {@link DCToolCommandBuilder}.
     * 
     * @param commandBuilder
     *            builder for dc tool commands
     * @return content of log file created by the executed dc tool.
     * @throws IOException
     *             might be thrown be the {@link ProcStarter} used to execute the DC tool commands.
     * @throws InterruptedException
     *             when the user canceled the action.
     */
    public DcToolCommandExecutionResult execute(final DCToolCommandBuilder commandBuilder) throws IOException,
        InterruptedException {
        final List<String> commands = commandBuilder.execute();
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        int exitCode = 0;

        if (commands.size() > 0) {
            final ProcStarter starter = launcher.launch();
            starter.pwd(workspace);
            starter.envs(createEnvironment());
            starter.cmds(createDcToolCommand(launcher.isUnix(), null));

            commands.addAll(0, commandFactory
                .createLoadConfigCommandBuilder(developmentConfiguration, dcToolDescriptor)
                .execute());

            starter.stdin(createCommandInputStream(commands));
            final ForkOutputStream tee = new ForkOutputStream(launcher.getListener().getLogger(), result);
            starter.stdout(tee);
            exitCode = starter.join();
        }

        return new DcToolCommandExecutionResult(result.toString(), exitCode);
    }

    /**
     * List development components in the development configuration.
     * 
     * @return the result of the listdc-command operation.
     * @throws IOException
     *             might be thrown be the {@link ProcStarter} used to execute the DC tool commands.
     * @throws InterruptedException
     *             when the user canceled the action.
     */
    public DcToolCommandExecutionResult listDevelopmentComponents(final DevelopmentComponentFactory dcFactory)
        throws IOException, InterruptedException {
        final long startListDcs = System.currentTimeMillis();
        logger.append(String.format("Reading development components for %s from NWDI.\n",
            developmentConfiguration.getName()));
        final DcToolCommandExecutionResult result =
            execute(commandFactory.createListDevelopmentComponentsCommandBuilder(developmentConfiguration));
        commandFactory.getListDcCommandResultReader(new StringReader(result.getOutput()), dcFactory,
            developmentConfiguration).read();
        duration(startListDcs, String.format("Read %s development components from NWDI", dcFactory.getAll().size()));

        return result;
    }

    /**
     * Synchronize development configurations in the development configuration.
     * 
     * @param dcFactory
     *            registry for development components.
     * @param cleanCopy
     *            indicate whether to synchronize all DCs or only DCs marked as needing a rebuild.
     * @return the result of the syncdc-command operation.
     * @throws IOException
     *             might be thrown be the {@link ProcStarter} used to execute the DC tool commands.
     * @throws InterruptedException
     *             when the user canceled the action.
     */
    public DcToolCommandExecutionResult synchronizeDevelopmentComponents(final DevelopmentComponentFactory dcFactory,
        final boolean cleanCopy, final boolean syncSources) throws IOException,
        InterruptedException {
        final long startSyncDCs = System.currentTimeMillis();
        logger.append("Synchronizing development components from NWDI.\n");
        final DcToolCommandExecutionResult result =
            execute(commandFactory.createSyncDevelopmentComponentsCommandBuilder(developmentConfiguration, dcFactory, syncSources,
                cleanCopy));
        duration(startSyncDCs, "Done synchronizing development components from NWDI.\n");

        return result;
    }

    /**
     * Build the given development components.
     * 
     * @param affectedComponents
     *            development components to build.
     * @return the result of the builddc operation.
     * @throws IOException
     *             might be thrown be the {@link ProcStarter} used to execute the DC tool commands.
     * @throws InterruptedException
     *             when the user canceled the action.
     */
    public DcToolCommandExecutionResult buildDevelopmentComponents(
        final Collection<DevelopmentComponent> affectedComponents) throws IOException, InterruptedException {
        final long start = System.currentTimeMillis();
        final DcToolCommandExecutionResult result =
            execute(commandFactory.createBuildDevelopmentComponentsCommandBuilder(developmentConfiguration,
                affectedComponents));
        duration(start, "Done building development components");

        return result;
    }

    /**
     * Create an <code>InputStream</code> containing the given dc tool commands.
     * 
     * @param commands
     *            list of dc tool commands
     * @return <code>InputStream</code> containing the given dc tool commands.
     */
    private InputStream createCommandInputStream(final List<String> commands) {
        final StringBuilder cmds = new StringBuilder();

        for (final String cmd : commands) {
            cmds.append(cmd).append('\n');
        }

        return new ByteArrayInputStream(cmds.toString().getBytes());
    }

    /**
     * Creates the command line for dctool execution.
     * 
     * @param isUnix
     *            indicate whether to run on a unixoid OS or Windows.
     * @param commandFile
     *            <code>FilePath</code> where DC tool commands should be written to.
     * @return the created command line.
     */
    protected ArgumentListBuilder createDcToolCommand(final boolean isUnix, final FilePath commandFile) {
        final ArgumentListBuilder args = new ArgumentListBuilder();

        args.add(getFullyQualifiedDcToolCommand(isUnix));

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
     *            indicate whether the platform to run on is Unix(oid) or Windows.
     * @return fully qualified command to be used to execute the dc tool.
     */
    private String getFullyQualifiedDcToolCommand(final boolean isUnix) {
        final String command = isUnix ? "dctool.sh" : "dctool.bat";
        final Character pathSeparator = isUnix ? '/' : '\\';

        return String.format("%s%c%s", getDcToolPath(), pathSeparator, command);
    }

    /**
     * Generate platform dependent path to dc tool.
     * 
     * @return platform dependent path to dc tool.
     */
    protected String getDcToolPath() {
        final File libraryFolder = new File(dcToolDescriptor.getNwdiToolLibrary());
        // final File parent = libraryFolder.getParentFile();
        return String.format("%s%cdc", libraryFolder.getAbsolutePath(), File.separatorChar);
    }

    /**
     * Prepare the environment variables for the launcher.
     * 
     * @return the map containing the environment variable name mapping to their corresponding values.
     */
    protected Map<String, String> createEnvironment() {
        final Map<String, String> environment = new HashMap<String, String>();
        environment.put(NWDITOOLLIB, dcToolDescriptor.getNwdiToolLibrary() + File.separatorChar + "lib");

        final JdkHomeAlias alias = developmentConfiguration.getJdkHomeAlias();

        if (alias != null) {
            environment.put(JAVA_HOME, dcToolDescriptor.getPaths().get(alias));
            environment.put(JDK_PROPERTY_NAME, alias.toString());
        }

        environment.put("JAVA_OPTS", dcToolDescriptor.getJdkOpts());

        return environment;
    }

    /**
     * Determine the time in seconds passed since the given start time and log it using the message given.
     * 
     * @param start
     *            begin of action whose duration should be logged.
     * @param message
     *            message to log.
     */
    private void duration(final long start, final String message) {
        final long duration = System.currentTimeMillis() - start;

        launcher.getListener().getLogger()
            .append(String.format("%s (%f sec).\n", message, duration / A_THOUSAND_MSECS));
    }
}
