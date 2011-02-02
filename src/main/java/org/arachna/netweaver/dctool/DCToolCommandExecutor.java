/**
 *
 */
package org.arachna.netweaver.dctool;

import hudson.FilePath;
import hudson.Launcher;
import hudson.Launcher.ProcStarter;
import hudson.util.ArgumentListBuilder;

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
     * 'loadconfig' command.
     */
    private static final String LOGFILE_COMMAND = "logfile -f %s -m overwrite;";

    /**
     * constant for 'loadconfig' command.
     */
    private static final String LOAD_CONFIG_COMMAND = "loadconfig -u %s -p %s -c \"%s\" -r \"%s\";";

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

        String dcToolLogoutput = "";
        int result = 0;

        if (commands.size() > 0) {
            final FilePath commandFile = this.workspace.child("dctool.in");
            final String logFileName = "dctool.out";

            commands.add(0, "exectime -m on;");
            commands.add(1, String.format(LOGFILE_COMMAND, logFileName));
            commands.add(2, String.format(LOAD_CONFIG_COMMAND, this.dcToolDescriptor.getUser(),
                this.dcToolDescriptor.getPassword(), this.dcToolDescriptor.getDtrDirectory(), ".dtc"));
            this.createCommandFile(commandFile, commands);

            final ProcStarter starter = this.launcher.launch();
            starter.pwd(this.workspace);
            starter.envs(this.createEnvironment());
            starter.cmds(this.createDcToolCommand(launcher.isUnix(), commandFile));

            result = starter.join();
            dcToolLogoutput = this.workspace.child(logFileName).readToString();
            this.launcher.getListener().getLogger().append(dcToolLogoutput);
        }

        return new DcToolCommandExecutionResult(dcToolLogoutput, result);
    }

    /**
     * Create the command file for the dc tool in the given workspace using the
     * given list of commands.
     * 
     * @param commandFile
     *            file path to write commands into.
     * @param commands
     *            list of commands to be executed by the dc tool.
     * @throws IOException
     *             when an error occured writing the the command file.
     * @throws InterruptedException
     *             when the user cancelled the action.
     */
    private void createCommandFile(final FilePath commandFile, final List<String> commands) throws IOException,
        InterruptedException {
        final StringBuilder commandBuilder = new StringBuilder();

        for (final String command : commands) {
            commandBuilder.append(command).append('\n');
        }

        commandFile.write(commandBuilder.toString(), "UTF-8");
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
        args.add("@" + commandFile.getName());

        if (!isUnix) {
            args.prepend("cmd.exe", "/C");
            args.add("&&", "exit", "%%ERRORLEVEL%%");
        }

        return args;
    }

    /**
     * @param isUnix
     * @return
     */
    private String getFullyQualifiedDcToolCommand(final boolean isUnix) {
        final String command = isUnix ? "dctool.sh" : "dctool.bat";
        final Character pathSeparator = isUnix ? '/' : '\\';
        final String fullyQualifiedDcToolCommand =
            String.format("%s%c%s", this.getDcToolPath(), pathSeparator, command);
        return fullyQualifiedDcToolCommand;
    }

    /**
     * @return
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
