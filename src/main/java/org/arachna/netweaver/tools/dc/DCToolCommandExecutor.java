/**
 *
 */
package org.arachna.netweaver.tools.dc;

import hudson.FilePath;
import hudson.Launcher;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.arachna.ant.AntHelper;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.tools.AbstractDIToolExecutor;
import org.arachna.netweaver.tools.DIToolCommandBuilder;
import org.arachna.netweaver.tools.DIToolCommandExecutionResult;
import org.arachna.netweaver.tools.DIToolDescriptor;

/**
 * Execute a DC Tool.
 *
 * @author Dirk Weigenand
 */
public final class DCToolCommandExecutor extends AbstractDIToolExecutor {
    /**
     * Factory for creating DC tool commands.
     */
    private final CommandFactory commandFactory;

    /**
     * builder for connecting/disconnecting to/from the NDWI.
     */
    private final LoadConfigCommandBuilder loadConfigCommandBuilder;

    /**
     * create DC tool executor with the given command line generator and given command build.
     *
     * @param launcher
     *            the launcher to use executing the DC tool.
     * @param workspace
     *            the workspace where the DC tool should be executed.
     * @param diToolDescriptor
     *            descriptor for various parameters needed for DC tool execution.
     * @param developmentConfiguration
     *            {@link DevelopmentConfiguration} to use executing the DC tool.
     */
    public DCToolCommandExecutor(final Launcher launcher, final FilePath workspace, final DIToolDescriptor diToolDescriptor,
        final DevelopmentConfiguration developmentConfiguration) {
        super(launcher, workspace, diToolDescriptor, developmentConfiguration);
        commandFactory = new CommandFactory(developmentConfiguration);
        loadConfigCommandBuilder =
            new LoadConfigCommandBuilder(diToolDescriptor, LoadConfigTemplate.fromJdkHomeAlias(developmentConfiguration.getJdkHomeAlias()));
    }

    /**
     * Synchronize development components in archive mode in the development configuration.
     *
     * @param dcFactory
     *            registry for development components.
     * @param antHelper
     *            helper to determine location of development components in workspace.
     * @param components List of development components to synchronize (in Archive State).
     * @return the result of the syncdc-command operation.
     * @throws IOException
     *             re-thrown from dctool execution
     * @throws InterruptedException
     *             re-thrown from dctool execution
     */
    public DIToolCommandExecutionResult synchronizeDevelopmentComponentsInArchiveState(final DevelopmentComponentFactory dcFactory,
        final AntHelper antHelper, final Collection<DevelopmentComponent> components) throws IOException, InterruptedException {
        final long startSyncDCs = System.currentTimeMillis();
        log(Messages.DCToolCommandExecutor_synchronizing_development_components());
        final DIToolCommandExecutionResult result =
            wrapAndExecute(commandFactory.createSyncDevelopmentComponentsInArchiveStateCommandBuilder(dcFactory, antHelper, components));
        duration(startSyncDCs, Messages.DCToolCommandExecutor_done_synchronizing_development_components());

        return result;
    }

    /**
     * Synchronize development components in archive mode in the development configuration.
     *
     * @param cleanCopy
     *            indicate whether the sources are to be synchronized unconditionally.
     * @return the result of the syncdc-command operation.
     * @throws IOException
     *             re-thrown from dctool execution
     * @throws InterruptedException
     *             re-thrown from dctool execution
     */
    public DIToolCommandExecutionResult synchronizeDevelopmentComponentsInSourceState(final boolean cleanCopy) throws IOException,
    InterruptedException {
        final long startSyncDCs = System.currentTimeMillis();
        log(Messages.DCToolCommandExecutor_synchronizing_development_components());
        final DIToolCommandExecutionResult result =
            wrapAndExecute(commandFactory.createSyncDevelopmentComponentsInSourceStateCommandBuilder(cleanCopy));
        duration(startSyncDCs, Messages.DCToolCommandExecutor_done_synchronizing_development_components());

        return result;
    }

    /**
     * Build the given development components.
     *
     * @param affectedComponents
     *            development components to build.
     * @return the result of the builddc operation.
     * @throws IOException
     *             re-thrown from dctool execution
     * @throws InterruptedException
     *             re-thrown from dctool execution
     */
    public DIToolCommandExecutionResult buildDevelopmentComponents(final Collection<DevelopmentComponent> affectedComponents)
        throws IOException, InterruptedException {
        final long start = System.currentTimeMillis();
        final DIToolCommandExecutionResult result =
            wrapAndExecute(commandFactory.createBuildDevelopmentComponentsCommandBuilder(affectedComponents));
        duration(start, Messages.DCToolCommandExecutor_done_building_development_components());

        return result;
    }

    /**
     * Wrap the given builder with a {@link DCToolCommandBuilderWrapper} to supply 'loadconfig' and 'exit' commands and execute the
     * resulting command list with the dctool.
     *
     * @param builder
     *            builder for dctool commands.
     * @return result object with return code and output of dctool commands.
     * @throws IOException
     *             re-thrown from dctool execution
     * @throws InterruptedException
     *             re-thrown from dctool execution
     */
    private DIToolCommandExecutionResult wrapAndExecute(final DIToolCommandBuilder builder) throws IOException, InterruptedException {
        return execute(new DCToolCommandBuilderWrapper(loadConfigCommandBuilder, builder));
    }

    /**
     * Generate the fully qualified command to be used to execute the dc tool.
     *
     * @return fully qualified command to be used to execute the dc tool.
     */
    @Override
    protected String getCommandName() {
        return isUnix() ? "dctool.sh" : "dctool.bat";
    }

    /**
     * Generate platform dependent path to dc tool.
     *
     * @return platform dependent path to dc tool.
     */
    @Override
    protected File getToolPath() {
        return new File(new File(getNwdiToolLibrary()), "dc");
    }

    /**
     * Wrap a given {@link DIToolCommandBuilder} in order to prepend 'loadconfig' and timing commands and append an 'exit' command.
     *
     * @author Dirk Weigenand
     *
     */
    private static class DCToolCommandBuilderWrapper implements DIToolCommandBuilder {
        /**
         * builder for connecting/disconnecting to/from the NDWI.
         */
        private final LoadConfigCommandBuilder loadConfigCommandBuilder;

        /**
         * builder to be wrapped.
         */
        private final DIToolCommandBuilder wrappedBuilder;

        /**
         * Create a new builder wrapper using the given {@link LoadConfigCommandBuilder} and builder to wrap.
         *
         * @param loadConfigCommandBuilder
         *            builder for connecting/disconnecting to/from the NDWI.
         * @param wrappedBuilder
         *            builder to wrap.
         */
        DCToolCommandBuilderWrapper(final LoadConfigCommandBuilder loadConfigCommandBuilder, final DIToolCommandBuilder wrappedBuilder) {
            this.loadConfigCommandBuilder = loadConfigCommandBuilder;
            this.wrappedBuilder = wrappedBuilder;
        }

        /**
         * Wrap commands in load- and exit-commands when the list of commands produced by the wrapped command build is not empty.
         *
         * This enables a performance optimisation when no commands are to be executed. In this case the expensive loading of the
         * <code>dctool</code> can be skipped. This will shave several 10 seconds off the build.
         *
         * @return list of commands to be executed. This list is empty, when the list of commands produced by the wrapped builder is empty.
         */
        @Override
        public List<String> execute() {
            final List<String> commands = new LinkedList<>();
            final List<String> wrappedCommands = wrappedBuilder.execute();

            if (!wrappedCommands.isEmpty()) {
                commands.addAll(loadConfigCommandBuilder.execute());
                commands.addAll(wrappedCommands);

                commands.add(loadConfigCommandBuilder.getExitCommand());
            }

            return commands;
        }
    }
}
