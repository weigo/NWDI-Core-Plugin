/**
 *
 */
package org.arachna.netweaver.tools.cbs;

import hudson.FilePath;
import hudson.Launcher;
import hudson.Launcher.ProcStarter;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.tools.AbstractDIToolExecutor;
import org.arachna.netweaver.tools.DIToolCommandExecutionResult;
import org.arachna.netweaver.tools.DIToolDescriptor;

/**
 * Execute a CBS Tool.
 * 
 * @author Dirk Weigenand
 */
public final class CBSToolCommandExecutor extends AbstractDIToolExecutor {
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
    public CBSToolCommandExecutor(final Launcher launcher, final FilePath workspace,
        final DIToolDescriptor diToolDescriptor, final DevelopmentConfiguration developmentConfiguration) {
        super(launcher, workspace, diToolDescriptor, developmentConfiguration);
    }

    /**
     * List development components in the development configuration.
     * 
     * @param dcFactory
     *            registry for development components to update with DCs listed from CBS.
     * @return the result of the listdc-command operation.
     * @throws IOException
     *             might be thrown be the {@link ProcStarter} used to execute the DC tool commands.
     * @throws InterruptedException
     *             when the user canceled the action.
     */
    public DIToolCommandExecutionResult listDevelopmentComponents(final DevelopmentComponentFactory dcFactory)
        throws IOException, InterruptedException {
        final long startListDcs = System.currentTimeMillis();
        final DevelopmentConfiguration config = getDevelopmentConfiguration();

        log(String.format("Reading development components for %s from NWDI.\n", config.getName()));
        final DIToolCommandExecutionResult result =
            execute(new DCLister(config.getCmsUrl(), config.getName(), getDiToolDescriptor()));
        new DCListReader(config, dcFactory).execute(new StringReader(result.getOutput()));
        duration(startListDcs, String.format("Read %s development components from NWDI", dcFactory.getAll().size()));

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
        return isUnix ? "cbstool.sh" : "cbstool.bat";
    }

    /**
     * Generate platform dependent path to dc tool.
     * 
     * @return platform dependent path to dc tool.
     */
    @Override
    protected File getToolPath() {
        return new File(new File(getNwdiToolLibrary()), "cbstool");
    }
}
