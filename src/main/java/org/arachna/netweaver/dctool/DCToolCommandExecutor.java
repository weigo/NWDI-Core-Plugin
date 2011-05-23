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
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.hudson.nwdi.DevelopmentComponentsReader;

/**
 * Execute a DC Tool.
 *
 * @author Dirk Weigenand
 */
public final class DCToolCommandExecutor {
    /**
     * 1000 milliseconds.
     */
    private static final float A_THOUSAND_MSECS = 1000f;

    /**
     * constant for environment variable 'JDK_PROPERTY_NAME'.
     */
    protected static final String JDK_PROPERTY_NAME = "JDK_PROPERTY_NAME";

    /**
     * constant for environment variable 'JAVA_HOME'.
     */
    protected static final String JAVA_HOME = "JAVA_HOME";

    /**
     * constant for environment variable 'NWDITOOLLIB'.
     */
    protected static final String NWDITOOLLIB = "NWDITOOLLIB";

    /**
     * constant for JDK to be used to build DCs.
     */
    protected static final String COM_SAP_JDK_HOME_PATH_KEY = "com.sap.jdk.home_path_key";

    /**
     * constant for 'loadconfig' command.
     */
    private static final String LOAD_CONFIG_COMMAND = "loadconfig -u %s -p %s -c \"%s\" -r \"%s\";\n";

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
     * create DC tool executor with the given command line generator and given
     * command build.
     *
     * @param launcher
     *            the launcher to use executing the DC tool.
     * @param workspace
     *            the workspace where the DC tool should be executed.
     * @param dcToolDescriptor
     *            descriptor for various parameters needed for DC tool
     *            execution.
     * @param developmentConfiguration
     *            {@link DevelopmentConfiguration} to use executing the DC tool.
     */
    public DCToolCommandExecutor(final Launcher launcher, final FilePath workspace,
        final DCToolDescriptor dcToolDescriptor, final DevelopmentConfiguration developmentConfiguration) {
        this.launcher = launcher;
        this.workspace = workspace;
        this.dcToolDescriptor = dcToolDescriptor;
        this.developmentConfiguration = developmentConfiguration;
    }

    /**
     * Execute dc tool with the given {@link DCToolCommandBuilder}.
     *
     * @param commandBuilder
     *            builder for dc tool commands
     * @return content of log file created by the executed dc tool.
     * @throws IOException
     *             when an error occurred writing the the command file.
     * @throws InterruptedException
     *             when the user canceled the action.
     */
    public DcToolCommandExecutionResult execute(final DCToolCommandBuilder commandBuilder) throws IOException,
        InterruptedException {
        final List<String> commands = commandBuilder.execute();
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        int exitCode = 0;

        if (commands.size() > 0) {
            final ProcStarter starter = this.launcher.launch();
            starter.pwd(this.workspace);
            starter.envs(this.createEnvironment());
            starter.cmds(this.createDcToolCommand(launcher.isUnix(), null));
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
     * @throws InterruptedException
     */
    public DcToolCommandExecutionResult listDevelopmentComponents(DevelopmentComponentFactory dcFactory)
        throws IOException, InterruptedException {
        long startListDcs = System.currentTimeMillis();
        this.launcher
            .getListener()
            .getLogger()
            .append(
                String.format("Reading development components for %s from NWDI.\n",
                    this.developmentConfiguration.getName()));
        DcToolCommandExecutionResult result = this.execute(new ListDcCommandBuilder(this.developmentConfiguration));
        new DevelopmentComponentsReader(new StringReader(result.getOutput()), dcFactory, this.developmentConfiguration)
            .read();
        duration(startListDcs, String.format("Read %s development components from NWDI", dcFactory.getAll().size()));

        return result;
    }

    /**
     * Synchronize development configurations in the development configuration.
     *
     * @param cleanCopy
     *            indicate whether to synchronize all DCs or only DCs marked as
     *            needing a rebuild.
     * @return the result of the syncdc-command operation.
     * @throws IOException
     * @throws InterruptedException
     */
    public DcToolCommandExecutionResult synchronizeDevelopmentComponents(boolean cleanCopy) throws IOException,
        InterruptedException {
        final long startSyncDCs = System.currentTimeMillis();
        this.launcher.getListener().getLogger().append("Synchronizing development components from NWDI.\n");
        DcToolCommandExecutionResult result =
            this.execute(new SyncDevelopmentComponentsCommandBuilder(this.developmentConfiguration, cleanCopy));
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
     * @throws InterruptedException
     */
    public DcToolCommandExecutionResult buildDevelopmentComponents(Collection<DevelopmentComponent> affectedComponents)
        throws IOException, InterruptedException {
        final long start = System.currentTimeMillis();
        final DcToolCommandExecutionResult result =
            this.execute(new BuildDevelopmentComponentsCommandBuilder(developmentConfiguration, affectedComponents,
                this.launcher.getListener().getLogger()));
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
        cmds.append("exectime -m on;\n");
        cmds.append(String.format(LOAD_CONFIG_COMMAND, this.dcToolDescriptor.getUser(),
            this.dcToolDescriptor.getPassword(), this.dcToolDescriptor.getDtrDirectory(), ".dtc"));

        for (final String cmd : commands) {
            cmds.append(cmd).append('\n');
        }

        cmds.append("exit;\n");

        return new ByteArrayInputStream(cmds.toString().getBytes());
    }

    /**
     * Creates the command line for dctool execution.
     *
     * @param isUnix
     *            indicate whether to run on a unixoid OS or Windows.
     * @param commandFile
     *            <code>FilePath</code> where DC tool commands should be written
     *            to.
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
     *            indicate whether the platform to run on is Unix(oid) or
     *            Windows.
     * @return fully qualified command to be used to execute the dc tool.
     */
    private String getFullyQualifiedDcToolCommand(final boolean isUnix) {
        final String command = isUnix ? "dctool.sh" : "dctool.bat";
        final Character pathSeparator = isUnix ? '/' : '\\';

        return String.format("%s%c%s", this.getDcToolPath(), pathSeparator, command);
    }

    /**
     * Generate platform dependent path to dc tool.
     *
     * @return platform dependent path to dc tool.
     */
    protected String getDcToolPath() {
        final File libraryFolder = new File(this.dcToolDescriptor.getNwdiToolLibrary());
        final File parent = libraryFolder.getParentFile();

        return String.format("%s%cdc", parent.getAbsolutePath(), File.separatorChar);
    }

    /**
     * Prepare the environment variables for the launcher.
     *
     * @return the map containing the environment variable name mapping to their
     *         corresponding values.
     */
    protected Map<String, String> createEnvironment() {
        final Map<String, String> environment = new HashMap<String, String>();
        // FIXME: get NWDITOOLLIB depending on JdkHomeAlias (1.3.1, 1.4.2 use
        // old DC tool, 1.5.0, 1.6.0, ... use new DC tool)
        environment.put(NWDITOOLLIB, this.dcToolDescriptor.getNwdiToolLibrary());

        final JdkHomeAlias alias = this.developmentConfiguration.getJdkHomeAlias();

        if (alias != null) {
            environment.put(JAVA_HOME, this.dcToolDescriptor.getPaths().get(alias));
            environment.put(JDK_PROPERTY_NAME, alias.toString());
        }

        return environment;
    }

    /**
     * Determine the time in seconds passed since the given start time and log
     * it using the message given.
     *
     * @param logger
     *            the logger to use.
     * @param start
     *            begin of action whose duration should be logged.
     * @param message
     *            message to log.
     */
    private void duration(final long start, final String message) {
        final long duration = System.currentTimeMillis() - start;

        this.launcher.getListener().getLogger()
            .append(String.format("%s (%f sec).\n", message, duration / A_THOUSAND_MSECS));
    }
}
