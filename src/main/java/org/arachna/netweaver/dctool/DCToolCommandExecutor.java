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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.arachna.netweaver.dc.types.DevelopmentConfiguration;

/**
 * Execute a DC Tool.
 * 
 * @author Dirk Weigenand
 */
public final class DCToolCommandExecutor {
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
     * Execute dctool with the given {@link DCToolCommandBuilder}.
     * 
     * @param commandBuilder
     *            builder for dc tool commands
     * @return content of logfile created by the executed dctool.
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
     * Create an <code>InputStream</code> containing the given dc tool commands.
     * 
     * @param commands
     *            list of dc tool commands
     * @return <code>InputStream</code> containing the given dc tool commands.
     */
    private ByteArrayInputStream createCommandInputStream(final List<String> commands) {
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
        final String fullyQualifiedDcToolCommand =
            String.format("%s%c%s", this.getDcToolPath(), pathSeparator, command);
        return fullyQualifiedDcToolCommand;
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
        environment.put(NWDITOOLLIB, this.dcToolDescriptor.getNwdiToolLibrary());
        environment.put(JAVA_HOME, this.getJdkHomePath());

        final JdkHomeAlias alias = this.getJdkPropertyName();

        if (alias != null) {
            environment.put(JDK_PROPERTY_NAME, alias.toString());
        }

        return environment;
    }

    /**
     * Get the name of the JDKs property name ({@see #JDK1_3_1_HOME}, etc.
     * 
     * @return the property name for the JDK to use or an empty string.
     */
    protected JdkHomeAlias getJdkPropertyName() {
        String jdkPropertyName = "";
        final String pathKey =
            this.developmentConfiguration.getBuildVariant().getBuildOption(COM_SAP_JDK_HOME_PATH_KEY);

        if (pathKey != null) {
            jdkPropertyName = pathKey;
        }

        return JdkHomeAlias.fromString(jdkPropertyName);
    }

    /**
     * Get JDK home path. Try to find a JDK location from the current build
     * variant. Returns the location configured in the JdkHomePaths, the value
     * of the java.home property when no value could be found.
     * 
     * @return a JDK location for executing the dctool with.
     */
    private String getJdkHomePath() {
        final JdkHomeAlias alias = getJdkPropertyName();

        return this.dcToolDescriptor.getPaths().get(alias);
    }
}
