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
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dctool.DCToolCommandExecutor;
import org.arachna.netweaver.dctool.DcToolCommandExecutionResult;
import org.arachna.netweaver.hudson.dtr.browser.Activity;
import org.arachna.netweaver.hudson.dtr.browser.ActivityResource;
import org.arachna.netweaver.hudson.dtr.browser.DtrBrowser;
import org.arachna.netweaver.hudson.nwdi.dcupdater.DevelopmentComponentUpdater;
import org.arachna.netweaver.hudson.util.FilePathHelper;
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
     * Create an instance of a <code>NWDIScm</code>.
     * 
     * @param dtrUser
     *            user to authenticate with.
     * @param password
     *            password to use for authentication.
     * @param cleanCopy
     *            indicate whether only changed development components should be
     *            loaded from the NWDI or all that are contained in the
     *            indicated CBS workspace
     */
    public NWDIScm(final boolean cleanCopy, final String dtrUser, final String password) {
        super();
        this.cleanCopy = cleanCopy;
        this.dtrUser = dtrUser;
        this.password = password;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChangeLogParser createChangeLogParser() {
        return new DtrChangeLogParser();
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
        final PrintStream logger = listener.getLogger();
        final DevelopmentConfiguration config = currentBuild.getDevelopmentConfiguration();

        final Collection<Activity> activities = new ArrayList<Activity>();
        final DCToolCommandExecutor executor = currentBuild.getDCToolExecutor(launcher);

        final DevelopmentComponentFactory dcFactory = currentBuild.getDevelopmentComponentFactory();
        DcToolCommandExecutionResult result = executor.listDevelopmentComponents(dcFactory);

        if (result.isExitCodeOk()) {
            final NWDIBuild lastSuccessfulBuild = currentBuild.getParent().getLastSuccessfulBuild();

            if (lastSuccessfulBuild != null) {
                logger.append(String.format("Getting activities from DTR (since last successful build #%s).\n",
                    lastSuccessfulBuild.getNumber()));
            }
            else {
                logger.append("Getting all activities from DTR.\n");
            }

            activities.addAll(getActivities(logger, getDtrBrowser(config, dcFactory),
                lastSuccessfulBuild != null ? lastSuccessfulBuild.getAction(NWDIRevisionState.class).getCreationDate()
                    : null));

            result = executor.synchronizeDevelopmentComponents(cleanCopy);

            if (!result.isExitCodeOk()) {
                final String output = result.getOutput();
                // FIXME: make heap used for dctool configurable!
                // ignore OutOfMemoryError on exit from dctool
                if (output.contains("java.lang.OutOfMemoryError") && output.contains("java.lang.System.exit")
                    && output.contains("com.sap.tc.devconf.dctool.startup.DCToolMain.main")) {
                    result = new DcToolCommandExecutionResult(output, 0);
                }
            }

            final DevelopmentComponentUpdater updater =
                new DevelopmentComponentUpdater(FilePathHelper.makeAbsolute(currentBuild.getWorkspace().child(".dtc")),
                    dcFactory);
            updater.execute();
        }

        build.addAction(new NWDIRevisionState(activities));
        writeChangeLog(build, changelogFile, activities);

        return result.isExitCodeOk();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SCMRevisionState calcRevisionsFromBuild(final AbstractBuild<?, ?> build, final Launcher launcher,
        final TaskListener listener) throws IOException, InterruptedException {
        listener.getLogger().append(String.format("Calculating revisions from build #%s.\n", build.getNumber()));

        final NWDIRevisionState lastRevision = build.getAction(NWDIRevisionState.class);

        return lastRevision == null ? SCMRevisionState.NONE : lastRevision;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PollingResult compareRemoteRevisionWith(final AbstractProject<?, ?> project, final Launcher launcher,
        final FilePath path, final TaskListener listener, final SCMRevisionState revisionState) throws IOException,
        InterruptedException {
        final NWDIBuild lastBuild = ((NWDIProject)project).getLastBuild();
        final PrintStream logger = listener.getLogger();
        logger.append(String.format(
            "Comparing base line activities with activities accumulated since last build (#%s).\n",
            lastBuild.getNumber()));
        final List<Activity> activities =
            getActivities(logger,
                getDtrBrowser(lastBuild.getDevelopmentConfiguration(), new DevelopmentComponentFactory()),
                getCreationDate(revisionState));
        logger.append(activities.toString());

        final Change changeState = activities.isEmpty() ? Change.NONE : Change.SIGNIFICANT;
        logger.append(String.format("Found changes: %s.\n", changeState.toString()));

        return new PollingResult(revisionState, new NWDIRevisionState(activities), changeState);
    }

    /**
     * Get the creation date from the given {@link SCMRevisionState}.
     * 
     * @param revisionState
     *            an <code>SCMRevisionState</code> object
     * @return the date/time when the given SCM revision state was computed iff
     *         it's type is {@link NWDIRevisionState}, <code>null</code>
     *         otherwise.
     */
    private Date getCreationDate(final SCMRevisionState revisionState) {
        Date creationDate = null;

        if (NWDIRevisionState.class.equals(revisionState.getClass())) {
            final NWDIRevisionState baseRevision = (NWDIRevisionState)revisionState;
            creationDate = baseRevision.getCreationDate();
        }

        return creationDate;
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
     *             will be rethrown when writing the change log fails.
     */
    private void writeChangeLog(final AbstractBuild<?, ?> build, final File changelogFile,
        final Collection<Activity> activities) throws IOException {
        final DtrChangeLogWriter dtrChangeLogWriter =
            new DtrChangeLogWriter(new DtrChangeLogSet(build, activities), new FileWriter(changelogFile));
        dtrChangeLogWriter.write();
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
        public SCM newInstance(final StaplerRequest req, final JSONObject formData)
            throws hudson.model.Descriptor.FormException {
            return super.newInstance(req, formData);
        }
    }

    /**
     * Get list of activities since last run. If <code>lastRun</code> is
     * <code>null</code> all activities will be read.
     * 
     * @param logger
     *            the logger to use.
     * @param browser
     *            the {@link DtrBrowser} to be used getting the activities.
     * @param since
     *            since when to get activities
     * @return a list of {@link Activity} objects that were checked in since the
     *         last run or all activities.
     */
    private List<Activity> getActivities(final PrintStream logger, final DtrBrowser browser, final Date since) {
        final List<Activity> activities = new ArrayList<Activity>();
        long start = System.currentTimeMillis();
        final long startGetActivities = start;

        if (since == null) {
            activities.addAll(browser.getActivities());
        }
        else {
            activities.addAll(browser.getActivities(since));
        }

        duration(logger, start, "Determine activities");

        start = System.currentTimeMillis();
        // update activities with their respective resources
        // FIXME: add methods to DtrBrowser that get activities with their
        // respective resources!
        browser.getDevelopmentComponents(activities);

        for (final Activity activity : activities) {
            for (final ActivityResource resource : activity.getResources()) {
                resource.getDevelopmentComponent().setNeedsRebuild(true);
            }
        }

        duration(logger, start, "Determine affected DCs for activities");
        duration(logger, startGetActivities, String.format("Read %s activities", activities.size()));

        return activities;
    }

    /**
     * Returns an instance of {@link DtrBrowser} using the given development
     * configuration and development component factory.
     * 
     * @param config
     *            the development configuration to be used to connect to the
     *            DTR.
     * @param dcFactory
     *            the development component factory to be used getting
     *            development components associated with activities.
     * @return the {@link DtrBrowser} for browsing the DTR for activities.
     */
    private DtrBrowser getDtrBrowser(final DevelopmentConfiguration config, final DevelopmentComponentFactory dcFactory) {
        return new DtrBrowser(config, dcFactory, dtrUser, password);
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
    private void duration(final PrintStream logger, final long start, final String message) {
        final long duration = System.currentTimeMillis() - start;

        logger.append(String.format("%s (%f sec).\n", message, duration / A_THOUSAND_MSECS));
    }
}
