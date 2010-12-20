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
import hudson.model.Run;
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

import net.sf.json.JSONObject;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dctool.DCToolCommandExecutor;
import org.arachna.netweaver.dctool.DCToolDescriptor;
import org.arachna.netweaver.hudson.dtr.browser.Activity;
import org.arachna.netweaver.hudson.dtr.browser.DtrBrowser;
import org.arachna.netweaver.hudson.nwdi.dcupdater.DevelopmentComponentUpdater;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Interface to NetWeaver Developer Infrastructure.
 * 
 * @author Dirk Weigenand
 */
public class NWDIScm extends SCM {

    /**
     * Get a clean copy of all development components from NWDI.
     */
    private final boolean cleanCopy;

    /**
     * Executor for DC tool commands used throughout.
     */
    private transient DCToolCommandExecutor dcToolExecutor;

    /**
     * registry for development components.
     */
    private transient DevelopmentComponentFactory dcFactory;

    /**
     * the development configuration of this track.
     */
    private transient DevelopmentConfiguration developmentConfiguration;

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
     * @see hudson.scm.SCM#getDescriptor()
     */
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    /*
     * (non-Javadoc)
     * @see hudson.scm.SCM#createChangeLogParser()
     */
    @Override
    public ChangeLogParser createChangeLogParser() {
        return new DtrChangeLogParser();
    }

    /*
     * (non-Javadoc)
     * @see hudson.scm.SCM#requiresWorkspaceForPolling()
     */
    @Override
    public boolean requiresWorkspaceForPolling() {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see hudson.scm.SCM#checkout(hudson.model.AbstractBuild, hudson.Launcher,
     * hudson.FilePath, hudson.model.BuildListener, java.io.File)
     */
    @Override
    public boolean checkout(final AbstractBuild<?, ?> build, final Launcher launcher, final FilePath workspace,
        final BuildListener listener, final File changelogFile) throws IOException, InterruptedException {
        final NWDIBuild currentBuild = (NWDIBuild)build;
        final NWDIProject project = (NWDIProject)build.getProject();
        final NWDIProject.DescriptorImpl descriptor = project.getDescriptor();
        this.dcFactory = new DevelopmentComponentFactory();
        this.developmentConfiguration = currentBuild.getDevelopmentConfiguration();
        final List<Activity> activities = this.getActivities(build.getPreviousBuild(), descriptor);
        final boolean rebuildNeeded = this.cleanCopy || !activities.isEmpty();

        if (rebuildNeeded) {
            this.writeChangeLog(build, changelogFile, activities);

            // final FilePath dtrDirectory =
            // this.createOrUpdateConfiguration(workspace);
            final DCToolDescriptor dcToolDescriptor =
                new DCToolDescriptor(descriptor.getUser(), descriptor.getPassword(), descriptor.getNwdiToolLibFolder(),
                    build.getWorkspace().child(".dtr").getName(), descriptor.getConfiguredJdkHomePaths());
            this.dcToolExecutor =
                new DCToolCommandExecutor(launcher, workspace, dcToolDescriptor,
                    currentBuild.getDevelopmentConfiguration());

            listener.getLogger().append(listDevelopmentComponents());
            listener.getLogger().append(syncDevelopmentComponents());

            final DevelopmentComponentUpdater updater =
                new DevelopmentComponentUpdater(workspace.absolutize().getName(), dcFactory);
            updater.execute();

            // TODO: add current development configuration to this build.
        }

        build.addAction(new NWDIRevisionState(activities));

        return rebuildNeeded;
    }

    private String listDevelopmentComponents() throws IOException, InterruptedException {

        final String output = this.dcToolExecutor.execute(new ListDcCommandBuilder(developmentConfiguration));

        final DevelopmentComponentsReader developmentComponentsReader =
            new DevelopmentComponentsReader(new StringReader(output), dcFactory, developmentConfiguration);
        developmentComponentsReader.read();

        for (final Compartment currentCompartment : developmentConfiguration.getCompartments()) {
            if (this.cleanCopy && CompartmentState.Source.equals(currentCompartment.getState())) {
                for (final DevelopmentComponent component : currentCompartment.getDevelopmentComponents()) {
                    component.setNeedsRebuild(true);
                }
            }
        }

        return output;
    }

    /**
     * @throws IOException
     * @throws InterruptedException
     */
    private String syncDevelopmentComponents() throws IOException, InterruptedException {
        final SyncDevelopmentComponentsCommandBuilder commandBuilder =
            new SyncDevelopmentComponentsCommandBuilder(this.developmentConfiguration, this.cleanCopy);
        return this.dcToolExecutor.execute(commandBuilder);
    }

    /**
     * @param build
     * @param changelogFile
     * @param activities
     * @throws IOException
     */
    private void writeChangeLog(final AbstractBuild<?, ?> build, final File changelogFile,
        final List<Activity> activities) throws IOException {
        final DtrChangeLogWriter dtrChangeLogWriter =
            new DtrChangeLogWriter(new DtrChangeLogSet(build, activities), new FileWriter(changelogFile));
        dtrChangeLogWriter.write();
    }

    /**
     * @return the cleanCopy
     */
    public boolean isCleanCopy() {
        return cleanCopy;
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
     * @param lastRun
     *            last run of a build or <code>null</code> if this run is the
     *            first.
     * @return a list of {@link Activity} objects that were checked in since the
     *         last run or all activities.
     */
    private List<Activity> getActivities(final Run<?, ?> lastRun, NWDIProject.DescriptorImpl descriptor) {
        final DtrBrowser browser =
            new DtrBrowser(this.developmentConfiguration, this.dcFactory, descriptor.getUser(),
                descriptor.getPassword());

        final List<Activity> activities = new ArrayList<Activity>();

        if (lastRun == null) {
            activities.addAll(browser.getActivities());
        }
        else {
            activities.addAll(browser.getActivities(lastRun.getTime()));
        }

        if (this.dcFactory != null) {
            // update activities with their respective resources
            // FIXME: add methods to DtrBrowser that get activities with their
            // respective resources!
            browser.getDevelopmentComponents(activities);
        }

        return activities;
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
}
