/**
 *
 */
package org.arachna.netweaver.tools.dc;

import hudson.FilePath;
import hudson.Launcher;
import hudson.Launcher.ProcStarter;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.tools.AbstractDIToolExecutor;
import org.arachna.netweaver.tools.DIToolCommandExecutionResult;
import org.arachna.netweaver.tools.DIToolDescriptor;
import org.arachna.netweaver.tools.dc.commands.CommandFactory;

/**
 * Execute a DC Tool.
 * 
 * @author Dirk Weigenand
 */
public final class DCToolCommandExecutor extends AbstractDIToolExecutor {
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
        final DIToolDescriptor dcToolDescriptor, final DevelopmentConfiguration developmentConfiguration) {
        super(launcher, workspace, dcToolDescriptor, developmentConfiguration);
    }

    /**
     * List development components in the development configuration.
     * 
     * @param dcFactory
     * @return the result of the listdc-command operation.
     * @throws IOException
     *             might be thrown be the {@link ProcStarter} used to execute the DC tool commands.
     * @throws InterruptedException
     *             when the user canceled the action.
     */
    public DIToolCommandExecutionResult listDevelopmentComponents(final DevelopmentComponentFactory dcFactory)
        throws IOException, InterruptedException {
        final long startListDcs = System.currentTimeMillis();
        DevelopmentConfiguration developmentConfiguration = getDevelopmentConfiguration();

        log(String.format("Reading development components for %s from NWDI.\n", developmentConfiguration.getName()));
        final DIToolCommandExecutionResult result =
            execute(commandFactory.createListDevelopmentComponentsCommandBuilder(developmentConfiguration));
        commandFactory.getListDcCommandResultReader(new StringReader(result.getOutput()), dcFactory,
            developmentConfiguration).read();
        duration(startListDcs, String.format("Read %s development components from NWDI", dcFactory.getAll().size()));

        return result;
    }

    /**
     * Synchronize development components in the development configuration.
     * 
     * @param dcFactory
     *            registry for development components.
     * @param cleanCopy
     *            indicate whether to synchronize all DCs or only DCs marked as needing a rebuild.
     * @param syncSources
     *            synchronize in inactive or archive mode
     * @return the result of the syncdc-command operation.
     * @throws IOException
     *             might be thrown be the {@link ProcStarter} used to execute the DC tool commands.
     * @throws InterruptedException
     *             when the user canceled the action.
     */
    public DIToolCommandExecutionResult synchronizeDevelopmentComponents(final DevelopmentComponentFactory dcFactory,
        final boolean cleanCopy, final boolean syncSources) throws IOException,
        InterruptedException {
        final long startSyncDCs = System.currentTimeMillis();
        log("Synchronizing development components from NWDI.\n");
        final DIToolCommandExecutionResult result =
            execute(commandFactory.createSyncDevelopmentComponentsCommandBuilder(getDevelopmentConfiguration(), dcFactory, syncSources,
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
    public DIToolCommandExecutionResult buildDevelopmentComponents(
        final Collection<DevelopmentComponent> affectedComponents) throws IOException, InterruptedException {
        final long start = System.currentTimeMillis();
        final DIToolCommandExecutionResult result =
            execute(commandFactory.createBuildDevelopmentComponentsCommandBuilder(getDevelopmentConfiguration(),
                affectedComponents));
        duration(start, "Done building development components");

        return result;
    }

    /**
     * Generate the fully qualified command to be used to execute the dc tool.
     * 
     * @param isUnix
     *            indicate whether the platform to run on is Unix(oid) or Windows.
     * @return fully qualified command to be used to execute the dc tool.
     */
    @Override
    protected String getCommandName(final boolean isUnix) {
        return isUnix ? "dctool.sh" : "dctool.bat";
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
}
