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
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.arachna.netweaver.dc.config.DevelopmentConfigurationReader;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dctool.DCToolCommandExecutor;
import org.arachna.netweaver.dctool.DcToolCommandExecutionResult;
import org.arachna.netweaver.hudson.dtr.browser.Activity;
import org.arachna.netweaver.hudson.dtr.browser.ActivityResource;
import org.arachna.netweaver.hudson.dtr.browser.DtrBrowser;
import org.arachna.netweaver.hudson.nwdi.dcupdater.DevelopmentComponentUpdater;
import org.arachna.netweaver.hudson.util.FilePathHelper;
import org.arachna.xml.XmlReaderHelper;
import org.kohsuke.stapler.StaplerRequest;
import org.xml.sax.SAXException;

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
        DcToolCommandExecutionResult result = readOrListDevelopmentComponents(workspace, config, executor, dcFactory);
        String dtcPath = FilePathHelper.makeAbsolute(currentBuild.getWorkspace().child(".dtc"));

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

            boolean cleanCopy = currentBuild.getPreviousBuild() == null || this.cleanCopy;

            setNeedsRebuildPropertyOnAllDevelopmentComponentsInSourceState(config, cleanCopy);

            if (cleanCopy || !activities.isEmpty()) {
                // synchronize sources
                result = executor.synchronizeDevelopmentComponents(dcFactory, cleanCopy, true);
                // update used DCs
                new DevelopmentComponentUpdater(dtcPath,
                    dcFactory).execute();

                if (result.isExitCodeOk()) {
                    // synchronize used DCs
                    result = executor.synchronizeDevelopmentComponents(dcFactory, cleanCopy, false);
                }

                if (!result.isExitCodeOk()) {
                    final String output = result.getOutput();

                    // FIXME: make heap used for dctool configurable!
                    // ignore OutOfMemoryError on exit from dctool
                    if (output.contains("java.lang.OutOfMemoryError") && output.contains("java.lang.System.exit")
                        && output.contains("com.sap.tc.devconf.dctool.startup.DCToolMain.main")) {
                        result = new DcToolCommandExecutionResult(output, 0);
                    }
                }

            }
        }

        new DevelopmentComponentUpdater(dtcPath,
            dcFactory).execute();

        build.addAction(new NWDIRevisionState(activities));
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
        boolean cleanCopy) {
        if (cleanCopy) {
            for (final Compartment compartment : config.getCompartments(CompartmentState.Source)) {
                for (final DevelopmentComponent component : compartment.getDevelopmentComponents()) {
                    component.setNeedsRebuild(true);
                }
            }
        }
    }

    /**
     * Read the development configuration saved during the last build or list development components from CBS if it doesn't exist.
     * 
     * @param workspace
     *            workspace folder where the development configuration was saved.
     * @param config
     *            development configuration to be used throughout the build
     * @param executor
     *            executor for listing development components from the CBS
     * @param dcFactory
     *            registry for development components.
     * @return the result of DC tool execution
     * @throws IOException
     *             re-thrown from DC tool execution
     * @throws InterruptedException
     *             re-thrown from DC tool execution
     */
    private DcToolCommandExecutionResult readOrListDevelopmentComponents(FilePath workspace,
        DevelopmentConfiguration config, final DCToolCommandExecutor executor,
        final DevelopmentComponentFactory dcFactory) throws IOException, InterruptedException {
        FilePath devConfFile = workspace.child("DevelopmentConfiguration.xml");
        DcToolCommandExecutionResult result = new DcToolCommandExecutionResult("", 0);

        if (!devConfFile.exists()) {
            result = executor.listDevelopmentComponents(dcFactory);
        }
        else {
            DevelopmentConfigurationReader configurationReader = new DevelopmentConfigurationReader(dcFactory);

            try {
                new XmlReaderHelper(configurationReader).parse(new InputStreamReader(devConfFile.read()));
                DevelopmentConfiguration savedConfig = configurationReader.getDevelopmentConfiguration();

                for (Compartment compartment : savedConfig.getCompartments()) {
                    Compartment original = config.getCompartment(compartment.getName());

                    // new compartment in development configuration that did not
                    // exist in previous build
                    if (original == null) {
                        original =
                            new Compartment(compartment.getName(), compartment.getState(), compartment.getVendor(),
                                compartment.getCaption(), compartment.getSoftwareComponent());
                        original.setDtrUrl(config.getDtrServerUrl());
                        original.setInactiveLocation(compartment.getInactiveLocation());
                        original.set(compartment.getUsedCompartments());
                        config.add(original);
                    }

                    original.add(compartment.getDevelopmentComponents());
                }
            }
            catch (SAXException e) {
                throw new RuntimeException(e);
            }
        }

        return result;
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
        NWDIProject nwdiProject = (NWDIProject)project;
        NWDIBuild lastBuild = nwdiProject.getLastSuccessfulBuild();

        if (lastBuild == null) {
            lastBuild = nwdiProject.getFirstBuild();
        }

        final PrintStream logger = listener.getLogger();
        logger.append(String.format(
            "Comparing base line activities with activities accumulated since last build (#%s).\n",
            lastBuild.getNumber()));

        final List<Activity> activities =
            getActivities(logger,
                getDtrBrowser(lastBuild.getDevelopmentConfiguration(), new DevelopmentComponentFactory()),
                getCreationDate(revisionState));

        final Change changeState = activities.isEmpty() ? Change.NONE : Change.SIGNIFICANT;
        logger.append(String.format("Found changes: %s.\n", changeState.toString()));

        return new PollingResult(revisionState, new NWDIRevisionState(activities), changeState);
    }

    /**
     * Get the creation date from the given {@link SCMRevisionState}.
     * 
     * @param revisionState
     *            an <code>SCMRevisionState</code> object
     * @return the date/time when the given SCM revision state was computed iff it's type is {@link NWDIRevisionState}, <code>null</code>
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
     * Get list of activities since last run. If <code>lastRun</code> is <code>null</code> all activities will be read.
     * 
     * @param logger
     *            the logger to use.
     * @param browser
     *            the {@link DtrBrowser} to be used getting the activities.
     * @param since
     *            since when to get activities
     * @return a list of {@link Activity} objects that were checked in since the last run or all activities.
     */
    private List<Activity> getActivities(final PrintStream logger, final DtrBrowser browser, final Date since) {
        final List<Activity> activities = new ArrayList<Activity>();
        long start = System.currentTimeMillis();
        final long startGetActivities = start;

        if (since == null) {
            activities.addAll(browser.getActivities());
            duration(logger, start, "Determine activities");
        }
        else {
            activities.addAll(browser.getActivities(since));
            duration(logger, start, String.format("Determine activities since %1$tF %<tT", since));
        }

        start = System.currentTimeMillis();
        // update activities with their respective resources
        // FIXME: add methods to DtrBrowser that get activities with their
        // respective resources!
        browser.getDevelopmentComponents(activities);
        browser.close();

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
     * Returns an instance of {@link DtrBrowser} using the given development configuration and development component factory.
     * 
     * @param config
     *            the development configuration to be used to connect to the DTR.
     * @param dcFactory
     *            the development component factory to be used getting development components associated with activities.
     * @return the {@link DtrBrowser} for browsing the DTR for activities.
     */
    private DtrBrowser getDtrBrowser(final DevelopmentConfiguration config, final DevelopmentComponentFactory dcFactory) {
        return new DtrBrowser(config, dcFactory, dtrUser, password);
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

        logger.append(String.format("%s (%f sec).\n", message, duration / A_THOUSAND_MSECS));
    }
}
