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
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.sf.json.JSONObject;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.hudson.dtr.browser.Activity;
import org.arachna.netweaver.hudson.dtr.browser.DtrBrowser;
import org.arachna.netweaver.hudson.nwdi.changelog.ChangeLogService;
import org.arachna.netweaver.hudson.nwdi.dcupdater.DevelopmentComponentUpdater;
import org.arachna.netweaver.hudson.util.FilePathHelper;
import org.arachna.netweaver.tools.DIToolCommandExecutionResult;
import org.arachna.netweaver.tools.dc.DCToolCommandExecutor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Interface to NetWeaver Developer Infrastructure.
 * 
 * @author Dirk Weigenand
 */
public class NWDIScm extends SCM {
    /**
     * 1000 milliseconds.
     */
    private static final float A_THOUSAND_MSECS = 1000f;

    /**
     * Get a clean copy of all development components from NWDI.
     */
    private final boolean cleanCopy;

    /**
     * user to authenticate against DTR.
     */
    private final transient String dtrUser;

    /**
     * password to use for authentication against the DTR.
     */
    private final transient String password;

    /**
     * Service for reading/writing DTR change logs.
     */
    private transient ChangeLogService changeLogService = new ChangeLogService();

    /**
     * Create an instance of a <code>NWDIScm</code>.
     * 
     * @param dtrUser
     *            user to authenticate with.
     * @param password
     *            password to use for authentication.
     * @param cleanCopy
     *            indicate whether only changed development components should be loaded from the NWDI or all that are contained in the
     *            indicated CBS workspace
     */
    public NWDIScm(final boolean cleanCopy, final String dtrUser, final String password) {
        super();
        this.cleanCopy = cleanCopy;
        this.dtrUser = dtrUser;
        this.password = password;
    }

    /**
     * Create parser for DTR change logs.
     * 
     * {@inheritDoc}
     */
    @Override
    public ChangeLogParser createChangeLogParser() {
        return changeLogService.createChangeLogParser();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requiresWorkspaceForPolling() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkout(final AbstractBuild<?, ?> build, final Launcher launcher, final FilePath workspace,
        final BuildListener listener, final File changelogFile) throws IOException, InterruptedException {
        final NWDIBuild currentBuild = (NWDIBuild)build;
        final NWDIProject project = currentBuild.getParent();
        final PrintStream logger = listener.getLogger();

        project.updateDevelopmentConfiguration(logger, currentBuild.getDtcFolder());
        final Collection<Activity> activities = new LinkedList<Activity>();
        final DCToolCommandExecutor executor = currentBuild.getDCToolExecutor(launcher);

        final DevelopmentComponentFactory dcFactory = currentBuild.getDevelopmentComponentFactory();

        DIToolCommandExecutionResult result = currentBuild.getCBSToolExecutor(launcher).listDevelopmentComponents(dcFactory);
        final DevelopmentComponentUpdater updater = new DevelopmentComponentUpdater(FilePathHelper.makeAbsolute(workspace), dcFactory);

        if (result.isExitCodeOk()) {
            final NWDIBuild lastSuccessfulBuild = project.getLastSuccessfulBuild();

            if (lastSuccessfulBuild != null) {
                logger.println(Messages.NWDIScm_get_activities_from_dtr(lastSuccessfulBuild.getNumber()));
            }
            else {
                logger.println(Messages.NWDIScm_get_all_activities_from_dtr());
            }

            final NWDIRevisionState state =
                lastSuccessfulBuild == null ? NWDIRevisionState.START_STATE : lastSuccessfulBuild.getAction(NWDIRevisionState.class);
            final DevelopmentConfiguration config = currentBuild.getDevelopmentConfiguration();
            activities.addAll(getActivities(logger, getDtrBrowser(config), dcFactory, state));

            final boolean cleanCopy = currentBuild.getPreviousBuild() == null || this.cleanCopy;

            setNeedsRebuildPropertyOnAllDevelopmentComponentsInSourceState(config, cleanCopy);

            if (cleanCopy || !activities.isEmpty()) {
                // synchronize sources
                result = executor.synchronizeDevelopmentComponents(dcFactory, cleanCopy, true);
                // update used DCs
                updater.execute();

                if (result.isExitCodeOk()) {
                    // synchronize used DCs (in archive state)
                    result = executor.synchronizeDevelopmentComponents(dcFactory, cleanCopy, false);
                }
            }
        }

        updater.execute();

        build.addAction(new NWDIRevisionState());
        writeChangeLog(build, changelogFile, activities);

        return result.isExitCodeOk();
    }

    /**
     * Set the needsRebuild property on all development components in source state if a clean build was requested.
     * 
     * @param config
     *            the development configuration containing the DCs
     * @param cleanCopy
     *            <code>true</code> when a clean build was requested, <code>false</code> otherwise.
     */
    private void setNeedsRebuildPropertyOnAllDevelopmentComponentsInSourceState(final DevelopmentConfiguration config,
        final boolean cleanCopy) {
        if (cleanCopy) {
            for (final Compartment compartment : config.getCompartments(CompartmentState.Source)) {
                for (final DevelopmentComponent component : compartment.getDevelopmentComponents()) {
                    component.setNeedsRebuild(true);
                }
            }
        }
    }

    @Override
    public SCMRevisionState calcRevisionsFromBuild(final AbstractBuild<?, ?> build, final Launcher launcher, final TaskListener listener)
        throws IOException, InterruptedException {
        listener.getLogger().println(Messages.NWDIScm_calculating_revisions_from_build(build.getNumber()));

        final NWDIRevisionState lastRevision = build.getAction(NWDIRevisionState.class);

        return lastRevision == null ? NWDIRevisionState.START_STATE : lastRevision;
    }

    @Override
    protected PollingResult compareRemoteRevisionWith(final AbstractProject<?, ?> project, final Launcher launcher, final FilePath path,
        final TaskListener listener, final SCMRevisionState revisionState) throws IOException, InterruptedException {
        final NWDIProject nwdiProject = (NWDIProject)project;
        NWDIBuild lastBuild = nwdiProject.getLastSuccessfulBuild();

        if (lastBuild == null) {
            lastBuild = nwdiProject.getFirstBuild();
        }

        final PrintStream logger = listener.getLogger();
        nwdiProject.updateDevelopmentConfiguration(logger, path.child(".dtc"));

        logger.println(Messages.NWDIScm_comparing_base_line_activities_with_activities_accumulated_since_last_build(lastBuild.getNumber()));
        final List<Activity> activities =
            getActivities(logger, getDtrBrowser(lastBuild.getDevelopmentConfiguration()), null, (NWDIRevisionState)revisionState);

        final Change changeState = activities.isEmpty() ? Change.NONE : Change.SIGNIFICANT;
        logger.println(Messages.NWDIScm_found_changes(changeState.toString()));

        return new PollingResult(revisionState, new NWDIRevisionState(), changeState);
    }

    /**
     * Write the change log using the given build, file and list of activities.
     * 
     * @param build
     *            the {@link AbstractBuild} to use writing the change log.
     * @param changelogFile
     *            file to write the change log to.
     * @param activities
     *            list of activities to write to change log.
     * @throws IOException
     *             will be re-thrown when writing the change log fails.
     */
    private void writeChangeLog(final AbstractBuild<?, ?> build, final File changelogFile, final Collection<Activity> activities)
        throws IOException {
        changeLogService.writeChangeLog(build, changelogFile, activities);
    }

    /**
     * {@link SCMDescriptor} for {@link NWDIProject}.
     * 
     * @author Dirk Weigenand
     */
    @Extension
    public static class DescriptorImpl extends SCMDescriptor<NWDIScm> {
        /**
         * Create an instance of {@link NWDIScm}s descriptor.
         */
        public DescriptorImpl() {
            super(NWDIScm.class, null);
            load();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getDisplayName() {
            return "NetWeaver Development Infrastructure";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public SCM newInstance(final StaplerRequest req, final JSONObject formData) throws hudson.model.Descriptor.FormException {
            return super.newInstance(req, formData);
        }
    }

    /**
     * Get list of activities since last run. If <code>lastRun</code> is <code>null</code> all activities will be read.
     * 
     * @param logger
     *            the logger to use.
     * @param browser
     *            the {@link DtrBrowser} to be used getting the activities.
     * @param dcFactory
     *            registry for development components to use when determining activities with resources. Should be <code>null</code>, when
     *            only activities should be determined.
     * @param state
     *            the NWDI revision state to use to determine activities.
     * @return a list of {@link Activity} objects that were checked in since the last run or all activities.
     */
    private List<Activity> getActivities(final PrintStream logger, final DtrBrowser browser, final DevelopmentComponentFactory dcFactory,
        final NWDIRevisionState state) {
        final List<Activity> activities = new LinkedList<Activity>();
        final long start = System.currentTimeMillis();
        final boolean dryRun = Boolean.getBoolean("nwdibuild.dry.run");

        if (!dryRun) {
            if (dcFactory == null) {
                activities.addAll(browser.getActivities(state.getCreationDate()));
            }
            else {
                activities.addAll(browser.getActivitiesWithResourcesAndDevelopmentComponents(dcFactory, state.getCreationDate()));
            }
        }

        if (NWDIRevisionState.START_STATE.equals(state)) {
            duration(logger, start, Messages.NWDIScm_determine_activities());
        }
        else {
            duration(logger, start, Messages.NWDIScm_determine_activities_since(String.format("%1$tF %<tT", state.getCreationDate())));
        }

        duration(logger, start, Messages.NWDIScm_read_countof_activities(activities.size()));

        return activities;
    }

    /**
     * Returns an instance of {@link DtrBrowser} using the given development configuration.
     * 
     * @param config
     *            the development configuration to be used to connect to the DTR.
     * @return the {@link DtrBrowser} for browsing the DTR for activities.
     */
    private DtrBrowser getDtrBrowser(final DevelopmentConfiguration config) {
        return new DtrBrowser(config, dtrUser, password);
    }

    /**
     * Determine the time in seconds passed since the given start time and log it using the message given.
     * 
     * @param logger
     *            the logger to use.
     * @param start
     *            begin of action whose duration should be logged.
     * @param message
     *            message to log.
     */
    private void duration(final PrintStream logger, final long start, final String message) {
        final long duration = System.currentTimeMillis() - start;

        logger.println(Messages.NWDIProject_duration_template(message, String.format("%f", duration / A_THOUSAND_MSECS)));
    }
}
