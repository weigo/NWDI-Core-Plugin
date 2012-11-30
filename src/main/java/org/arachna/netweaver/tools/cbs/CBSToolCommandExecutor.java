/**
 *
 */
package org.arachna.netweaver.tools.cbs;

import hudson.FilePath;
import hudson.Launcher;
import hudson.Launcher.ProcStarter;
import hudson.Util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;

import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.hudson.nwdi.Messages;
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
    public CBSToolCommandExecutor(final Launcher launcher, final FilePath workspace,
        final DIToolDescriptor diToolDescriptor, final DevelopmentConfiguration developmentConfiguration) {
        super(launcher, workspace, diToolDescriptor, developmentConfiguration);
    }

    /**
     * List development components in the development configuration.
     * 
     * @param dcFactory
     *            registry for development components to update with DCs listed
     *            from CBS.
     * @return the result of the listdc-command operation.
     * @throws IOException
     *             might be thrown be the {@link ProcStarter} used to execute
     *             the DC tool commands.
     * @throws InterruptedException
     *             when the user canceled the action.
     */
    public DIToolCommandExecutionResult listDevelopmentComponents(final DevelopmentComponentFactory dcFactory)
        throws IOException, InterruptedException {
        final long startListDcs = System.currentTimeMillis();
        final DevelopmentConfiguration config = getDevelopmentConfiguration();

        log(Messages.CBSToolCommandExecutor_listing_development_components(config.getName()));

        DIToolCommandExecutionResult result = null;

        switch (getCbsToolVersion()) {
        case CE:
            result = execute(new DCLister(config, getDiToolDescriptor()));
            new DCListReader(config, dcFactory).execute(new StringReader(result.getOutput()));
            break;

        case PRE_CE:
            result = execute(new ListCompartments(config.getCmsUrl(), config.getName(), getDiToolDescriptor()));

            if (result.isExitCodeOk()) {
                result = execute(new PreCeDCLister(config, getDiToolDescriptor()));
                new PreCeDCListReader(config, dcFactory).execute(new StringReader(result.getOutput()));
            }

            break;
        }

        duration(startListDcs, Messages.CBSToolCommandExecutor_report_count_of_dcs_read(dcFactory.getAll().size()));

        return result;
    }

    /**
     * Determine the version of the CBS tool to use.
     * 
     * @return the {@link CbsToolVersion} to use.
     */
    protected CbsToolVersion getCbsToolVersion() {
        final StringWriter scriptContent = new StringWriter();

        try {
            Util.copyStream(new FileReader(getToolCommand()), scriptContent);
        }
        catch (final IOException e) {
            throw new IllegalStateException(e);
        }

        return CbsToolVersion.fromString(scriptContent.toString());
    }

    /**
     * Get a list of build spaces from CBS.
     * 
     * @return list of build spaces from CBS.
     * @throws IOException
     *             re-thrown from executing the CBS tool via the launcher.
     * @throws InterruptedException
     *             when the command execution was interrupted.
     */
    public Collection<String> getBuildSpaceNames() throws IOException, InterruptedException {
        final DevelopmentConfiguration config = getDevelopmentConfiguration();
        final DIToolCommandExecutionResult result =
            execute(new ListBuildSpaces(config.getCmsUrl(), getDiToolDescriptor()));

        return new BuildSpaceParser(result.getOutput()).parse();
    }

    /**
     * Download the development configuration for the given build space to the
     * given path.
     * 
     * @param buildSpace
     *            name of build space whose development configuration should be
     *            download.
     * @param path
     *            path to store development configuration at.
     * @return result object containing result state and output of the executed
     *         command.
     * @throws IOException
     *             re-thrown from executing the CBS tool via the launcher.
     * @throws InterruptedException
     *             when the command execution was interrupted.
     */
    public DIToolCommandExecutionResult updateDevelopmentConfiguration(final String buildSpace, final String path)
        throws IOException, InterruptedException {
        final DevelopmentConfiguration config = getDevelopmentConfiguration();
        return execute(new DownloadConfig(config.getCmsUrl(), getDiToolDescriptor(), buildSpace, path));
    }

    /**
     * Generate the fully qualified command to be used to execute the cbstool.
     * 
     * @return fully qualified command to be used to execute the cbstool.
     */
    @Override
    protected String getCommandName() {
        return isUnix() ? "cbstool.sh" : "cbstool.bat";
    }

    /**
     * Generate platform dependent path to cbstool.
     * 
     * @return platform dependent path to cbstool.
     */
    @Override
    protected File getToolPath() {
        return new File(new File(getNwdiToolLibrary()), "cbstool");
    }
}
