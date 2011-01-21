/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.scm.ChangeLogParser;
import hudson.scm.PollingResult;
import hudson.scm.PollingResult.Change;
import hudson.scm.SCMDescriptor;
import hudson.scm.SCMRevisionState;
import hudson.scm.SCM;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.json.JSONObject;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dctool.DCToolCommandExecutor;
import org.arachna.netweaver.hudson.dtr.browser.Activity;
import org.arachna.netweaver.hudson.dtr.browser.DtrBrowser;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Interface to NetWeaver Developer Infrastructure.
 * 
 * @author Dirk Weigenand
 */
public class NWDIScm extends SCM {
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(NWDIScm.class.getName());

    /**
     * Get a clean copy of all development components from NWDI.
     */
    private final boolean cleanCopy;

    /**
     * Create an instance of a <code>NWDIScm</code>.
     * 
     * @param cleanCopy
     *            indicate whether only changed development components should be
     *            loaded from the NWDI or all that are contained in the
     *            indicated CBS workspace
     */
    @DataBoundConstructor
    public NWDIScm(final boolean cleanCopy) {
        super();
        this.cleanCopy = cleanCopy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hudson.scm.SCM#getDescriptor()
     */
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    /*
     * (non-Javadoc)
     * 
     * @see hudson.scm.SCM#createChangeLogParser()
     */
    @Override
    public ChangeLogParser createChangeLogParser() {
        return new DtrChangeLogParser();
    }

    /*
     * (non-Javadoc)
     * 
     * @see hudson.scm.SCM#requiresWorkspaceForPolling()
     */
    @Override
    public boolean requiresWorkspaceForPolling() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hudson.scm.SCM#checkout(hudson.model.AbstractBuild, hudson.Launcher,
     * hudson.FilePath, hudson.model.BuildListener, java.io.File)
     */
    @Override
    public boolean checkout(final AbstractBuild<?, ?> build, final Launcher launcher, final FilePath workspace,
        final BuildListener listener, final File changelogFile) throws IOException, InterruptedException {
        final NWDIBuild currentBuild = (NWDIBuild)build;
        final List<Activity> activities = this.getActivities(currentBuild);

        this.writeChangeLog(build, changelogFile, activities);
        build.addAction(new NWDIRevisionState(activities));

        final boolean rebuildNeeded = this.cleanCopy || !activities.isEmpty();

        if (rebuildNeeded) {
            final DCToolCommandExecutor dcToolExecutor = currentBuild.getDCToolExecutor(launcher);
            this.listDevelopmentComponents(currentBuild, dcToolExecutor);
            this.syncDevelopmentComponents(currentBuild.getDevelopmentConfiguration(), dcToolExecutor);
        }

        return rebuildNeeded;
    }

    @Override
    public SCMRevisionState calcRevisionsFromBuild(final AbstractBuild<?, ?> build, final Launcher launcher,
        final TaskListener listener) throws IOException, InterruptedException {
        return SCMRevisionState.NONE;
    }

    @Override
    protected PollingResult compareRemoteRevisionWith(final AbstractProject<?, ?> project, final Launcher launcher,
        final FilePath path, final TaskListener listener, final SCMRevisionState revisionState) throws IOException,
        InterruptedException {
        return new PollingResult(SCMRevisionState.NONE, project.getAction(NWDIRevisionState.class), Change.SIGNIFICANT);
    }

    /**
     * Lists the development components contained in this projects development
     * configuration and inserts them into the {@see #developmentConfiguration}
     * object (under the respective compartment).
     * 
     * @param currentBuild
     *            currently running build
     * @throws IOException
     *             when writing the 'listdcs' command file failed
     * @throws InterruptedException
     *             when the operation was canceled by the user
     */
    private void listDevelopmentComponents(final NWDIBuild currentBuild, final DCToolCommandExecutor executor)
        throws IOException, InterruptedException {
        final String output = executor.execute(new ListDcCommandBuilder(currentBuild.getDevelopmentConfiguration()));
        final DevelopmentComponentsReader developmentComponentsReader =
            new DevelopmentComponentsReader(new StringReader(output), currentBuild.getDevelopmentComponentFactory(),
                currentBuild.getDevelopmentConfiguration());
        developmentComponentsReader.read();
    }

    /**
     * Synchronize the development components. The DCs will be synchronized
     * depending on the projects parameter <code>cleanCopy</code>. When it is
     * <code>true</code> all DCs will be synchronized, only those involved in
     * activities since the last build otherwise.
     * 
     * @param developmentConfiguration
     *            development configuration to use when computing the 'syncdc'
     *            commands.
     * @param executor
     * @throws IOException
     *             when writing the 'syncdcs' command file failed
     * @throws InterruptedException
     *             when the operation was canceled by the user
     */
    private void syncDevelopmentComponents(final DevelopmentConfiguration developmentConfiguration,
        final DCToolCommandExecutor executor) throws IOException, InterruptedException {
        final SyncDevelopmentComponentsCommandBuilder commandBuilder =
            new SyncDevelopmentComponentsCommandBuilder(developmentConfiguration, this.cleanCopy);
        executor.execute(commandBuilder);
    }

    /**
     * Write the change log using the given list of activities.
     * 
     * @param build
     *            the {@link AbstractBuild} to use writing the change log.
     * @param changelogFile
     *            file to write the change log to.
     * @param activities
     *            list of activities to write to change log.
     * @throws IOException
     */
    private void writeChangeLog(final AbstractBuild<?, ?> build, final File changelogFile,
        final List<Activity> activities) throws IOException {
        final DtrChangeLogWriter dtrChangeLogWriter =
            new DtrChangeLogWriter(new DtrChangeLogSet(build, activities), new FileWriter(changelogFile));
        dtrChangeLogWriter.write();
    }

    @Extension
    public static class DescriptorImpl extends SCMDescriptor<NWDIScm> {
        /**
         * Create an instance of {@link NWDIScm}s descriptor.
         */
        public DescriptorImpl() {
            super(NWDIScm.class, null);
            load();
        }

        @Override
        public String getDisplayName() {
            return "NetWeaver Development Infrastructure";
        }

        @Override
        public SCM newInstance(final StaplerRequest req, final JSONObject formData)
            throws hudson.model.Descriptor.FormException {
            return super.newInstance(req, formData);
        }
    }

    /**
     * Get list of activities since last run. If <code>lastRun</code> is
     * <code>null</code> all activities will be calculated.
     * 
     * @param build
     *            the build currently running
     * @return a list of {@link Activity} objects that were checked in since the
     *         last run or all activities.
     */
    private List<Activity> getActivities(final NWDIBuild build) {
        final NWDIProject.DescriptorImpl descriptor = build.getParent().getDescriptor();
        final DtrBrowser browser =
            new DtrBrowser(build.getDevelopmentConfiguration(), build.getDevelopmentComponentFactory(),
                descriptor.getUser(), descriptor.getPassword());

        final List<Activity> activities = new ArrayList<Activity>();
        long start = System.currentTimeMillis();

        if (build.getPreviousBuild() == null) {
            activities.addAll(browser.getActivities());
        }
        else {
            activities.addAll(browser.getActivities(build.getPreviousBuild().getTime()));
        }

        this.duration(start, "getActivities");
        start = System.currentTimeMillis();
        // update activities with their respective resources
        // FIXME: add methods to DtrBrowser that get activities with their
        // respective resources!
        for (final DevelopmentComponent component : browser.getDevelopmentComponents(activities)) {
            component.setNeedsRebuild(true);
        }

        this.duration(start, "getDevelopmentComponents");

        return activities;
    }

    private void duration(final long start, final String message) {
        final long duration = System.currentTimeMillis() - start;

        LOGGER.log(Level.INFO, String.format("%s took %d.%d sec.\n", message, (duration / 1000), (duration % 1000)));
    }
}
