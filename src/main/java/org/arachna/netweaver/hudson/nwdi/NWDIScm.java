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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.json.JSONObject;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dctool.DCToolCommandExecutor;
import org.arachna.netweaver.dctool.DcToolCommandExecutionResult;
import org.arachna.netweaver.hudson.dtr.browser.Activity;
import org.arachna.netweaver.hudson.dtr.browser.DtrBrowser;
import org.arachna.netweaver.hudson.nwdi.dcupdater.DevelopmentComponentUpdater;
import org.arachna.netweaver.hudson.util.FilePathHelper;
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
     * @param cleanCopy
     *            indicate whether only changed development components should be
     *            loaded from the NWDI or all that are contained in the
     *            indicated CBS workspace
     */
    @DataBoundConstructor
    public NWDIScm(final boolean cleanCopy, final String dtrUser, final String password) {
        super();
        this.cleanCopy = cleanCopy;
        this.dtrUser = dtrUser;
        this.password = password;
    }

    /*
     * (non-Javadoc * *
     * 
     * @see hudson.scm.SCM#getDescriptor()
     */
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    /*
     * (non-Javado * *
     * 
     * @see hudson.scm.SCM#createChangeLogParser()
     */
    @Override
    public ChangeLogParser createChangeLogParser() {
        return new DtrChangeLogParser();
    }

    /*
     * (non-Javad * *
     * 
     * @see hudson.scm.SCM#requiresWorkspaceForPolling()
     */
    @Override
    public boolean requiresWorkspaceForPolling() {
        return true;
    }

    /*
     * (non-Java * *
     * 
     * @see hudson.scm.SCM#checkout(hudson.model.AbstractBuild, hudson.Launcher,
     * hudson.FilePath, hudson.model.BuildListener, java.io.File)
     */
    @Override
    public boolean checkout(final AbstractBuild<?, ?> build, final Launcher launcher, final FilePath workspace,
        final BuildListener listener, final File changelogFile) throws IOException, InterruptedException {
        final NWDIBuild currentBuild = (NWDIBuild)build;
        final PrintStream logger = listener.getLogger();
        final DevelopmentConfiguration config = currentBuild.getDevelopmentConfiguration();
        logger.append(String.format("Reading development components for %s from NWDI.\n", config.getName()));

        final Collection<Activity> activities = new ArrayList<Activity>();
        final DCToolCommandExecutor executor = currentBuild.getDCToolExecutor(launcher);

        DcToolCommandExecutionResult result = executor.execute(new ListDcCommandBuilder(config));

        if (result.isExitCodeOk()) {
            final DevelopmentComponentFactory dcFactory = currentBuild.getDevelopmentComponentFactory();
            new DevelopmentComponentsReader(new StringReader(result.getOutput()), dcFactory, config).read();
            logger.append(String.format("Read %s development components from NWDI.\n", dcFactory.getAll().size()));

            // FIXME: last build or last successful one?
            final NWDIBuild lastSuccessfulBuild = currentBuild.getParent().getLastSuccessfulBuild();

            logger.append("Synchronizing development components from NWDI.\n");
            result = executor.execute(new SyncDevelopmentComponentsCommandBuilder(config, this.cleanCopy));
            logger.append("Done synchronizing development components from NWDI.\n");

            if (lastSuccessfulBuild != null) {
                logger.append(String.format("Getting activities from DTR (since last successful build #%s).\n",
                    lastSuccessfulBuild.getNumber()));
            }
            else {
                logger.append("Getting all activities from DTR.\n");
            }

            activities.addAll(this.getActivities(this.getDtrBrowser(config, dcFactory),
                lastSuccessfulBuild != null ? lastSuccessfulBuild.getAction(NWDIRevisionState.class).getCreationDate()
                    : null));
            logger.append(String.format("Read %s activities.\n", activities.size()));

            // FIXME: doesn't find any DCs at the moment
            final DevelopmentComponentUpdater updater =
                new DevelopmentComponentUpdater(FilePathHelper.makeAbsolute(currentBuild.getWorkspace().child(".dtc")),
                    dcFactory);
            updater.execute();
        }

        build.addAction(new NWDIRevisionState(activities));
        this.writeChangeLog(build, changelogFile, activities);

        return result.isExitCodeOk();
    }

    @Override
    public SCMRevisionState calcRevisionsFromBuild(final AbstractBuild<?, ?> build, final Launcher launcher,
        final TaskListener listener) throws IOException, InterruptedException {
        listener.getLogger().append(String.format("Calculating revisions from build #%s.", build.getNumber()));

        final NWDIRevisionState lastRevision = build.getAction(NWDIRevisionState.class);

        return lastRevision == null ? SCMRevisionState.NONE : lastRevision;
    }

    @Override
    protected PollingResult compareRemoteRevisionWith(final AbstractProject<?, ?> project, final Launcher launcher,
        final FilePath path, final TaskListener listener, final SCMRevisionState revisionState) throws IOException,
        InterruptedException {
        final NWDIBuild lastBuild = ((NWDIProject)project).getLastBuild();
        final PrintStream logger = listener.getLogger();
        logger
            .append(String.format("Comparing base line activities with activities accumulated since last build (#%s).",
                lastBuild.getNumber()));
        final List<Activity> activities =
            this.getActivities(
                this.getDtrBrowser(lastBuild.getDevelopmentConfiguration(), new DevelopmentComponentFactory()),
                getCreationDate(revisionState));
        logger.append(activities.toString());

        final Change changeState = activities.isEmpty() ? Change.NONE : Change.SIGNIFICANT;
        logger.append(String.format("Found changes: %s.", changeState.toString()));

        return new PollingResult(revisionState, new NWDIRevisionState(activities), changeState);
    }

    private Date getCreationDate(final SCMRevisionState revisionState) {
        Date creationDate = null;

        if (NWDIRevisionState.class.equals(revisionState.getClass())) {
            final NWDIRevisionState baseRevision = (NWDIRevisionState)revisionState;
            creationDate = baseRevision.getCreationDate();
        }

        return creationDate;
    }

    /**
     * Write the change log using the given list of activi * *
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
        final Collection<Activity> activities) throws IOException {
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
     * <code>null</code> all activities will be calcu *
     * 
     * @param build
     *            the build currently running
     * @return a list of {@link Activity} objects that were checked in since the
     *         last run or all activities.
     */
    private List<Activity> getActivities(final DtrBrowser browser, final Date since) {
        final List<Activity> activities = new ArrayList<Activity>();
        final long start = System.currentTimeMillis();

        if (since == null) {
            activities.addAll(browser.getActivities());
        }
        else {
            activities.addAll(browser.getActivities(since));
        }

        this.duration(start, "getActivities");
        this.updateActivitiesWithResources(browser, activities);

        return activities;
    }

    /**
     * @param project
     * @param descriptor
     * @return
     */
    private DtrBrowser getDtrBrowser(final DevelopmentConfiguration config, final DevelopmentComponentFactory dcFactory) {
        return new DtrBrowser(config, dcFactory, this.dtrUser, this.password);
    }

    /**
     * @param browser
     * @param activities
     */
    private void updateActivitiesWithResources(final DtrBrowser browser, final List<Activity> activities) {
        long start;
        start = System.currentTimeMillis();
        // update activities with their respective resources
        // FIXME: add methods to DtrBrowser that get activities with their
        // respective resources!

        final Set<DevelopmentComponent> developmentComponents = browser.getDevelopmentComponents(activities);

        for (final DevelopmentComponent component : developmentComponents) {
            component.setNeedsRebuild(true);
        }

        this.duration(start, "getDevelopmentComponents");
    }

    private void duration(final long start, final String message) {
        final long duration = System.currentTimeMillis() - start;

        LOGGER.log(Level.INFO, String.format("%s took %d.%d sec.\n", message, (duration / 1000), (duration % 1000)));
    }
}
