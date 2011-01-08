/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.tasks.Publisher;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dctool.DCToolCommandExecutor;
import org.arachna.netweaver.dctool.DCToolDescriptor;
import org.arachna.netweaver.hudson.nwdi.confdef.ConfDefReader;
import org.arachna.netweaver.hudson.nwdi.dcupdater.DevelopmentComponentUpdater;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * A job for building a NWDI development configuration/track.
 * 
 * @author Dirk Weigenand
 * 
 */
public final class NWDIBuild extends AbstractBuild<NWDIProject, NWDIBuild> {
    /**
     * Executor for DC tool commands used throughout.
     */
    private transient DCToolCommandExecutor dcToolExecutor;

    /**
     * the development configuration this build will process.
     */
    private DevelopmentConfiguration developmentConfiguration;

    /**
     * Registry for development components.
     */
    private final DevelopmentComponentFactory dcFactory = new DevelopmentComponentFactory();

    /**
     * Create an instance of <code>NWDIBuild</code> using the given
     * <code>NWDIProject</code>.
     * 
     * @param project
     *            parent to use for creating this build.
     * @throws IOException
     *             when saving the current build number fails.
     */
    public NWDIBuild(final NWDIProject project) throws IOException {
        super(project);
    }

    @Override
    public void run() {
        run(new RunnerImpl());
    }

    /**
     * Returns the {@link DevelopmentConfiguration} used throughout this build.
     * 
     * @return the <code>DevelopmentConfiguration</code> used throughout this
     *         build.
     */
    public DevelopmentConfiguration getDevelopmentConfiguration() {
        if (this.developmentConfiguration == null) {
            try {
                final ConfDefReader confdefReader = new ConfDefReader(XMLReaderFactory.createXMLReader());
                this.developmentConfiguration = confdefReader.read(new StringReader(this.getProject().getConfDef()));
            }
            catch (final SAXException e) {
                throw new RuntimeException(e);
            }
        }

        return this.developmentConfiguration;
    }

    /**
     * Returns the {@link DCToolCommandExecutor} used throughout this build
     * using the given {@link Launcher}.
     * 
     * @param launcher
     *            the launcher to use executing DC tool.
     * @return <code>DCToolCommandExecutor</code> to execute DC tool commands.
     */
    DCToolCommandExecutor getDCToolExecutor(final Launcher launcher) {
        if (this.dcToolExecutor == null) {
            final NWDIProject.DescriptorImpl descriptor = this.getParent().getDescriptor();
            final DCToolDescriptor dcToolDescriptor =
                new DCToolDescriptor(descriptor.getUser(), descriptor.getPassword(), descriptor.getNwdiToolLibFolder(),
                    this.getWorkspace().child(".dtr").getName(), descriptor.getConfiguredJdkHomePaths());

            this.dcToolExecutor =
                new DCToolCommandExecutor(launcher, this.getWorkspace(), dcToolDescriptor,
                    this.getDevelopmentConfiguration());
        }

        return this.dcToolExecutor;
    }

    /**
     * Returns the {@link DevelopmentComponentFactory} used throughout this
     * build.
     * 
     * @return <code>DevelopmentComponentFactory</code> used as registry for
     *         development components.
     */
    DevelopmentComponentFactory getDevelopmentComponentFactory() {
        return this.dcFactory;
    }

    /**
     * Run the build.
     * 
     * @author Dirk Weigenand
     */
    private final class RunnerImpl extends AbstractRunner {
        /**
         * collection of reporter plugins to be run prior to building.
         */
        private List<Publisher> reporters;

        /**
         * Builds all (changed) development components and updates the in core
         * information about them (i.e. all DCs associated with the current
         * track in the development configuration stored in this build) so that
         * this information can be used in post build tasks for e.g. quality
         * control or generation of documentation.
         * 
         * @param listener
         *            the {@link BuildListener} to use for i.e. reporting.
         * @return the build result {@see Result}.
         * @throws Exception
         *             forward all Exceptions thrown by underlying code
         */
        @Override
        protected Result doRun(final BuildListener listener) throws Exception {
            this.createOrUpdateConfiguration();
            this.reporters = getProject().getPublishersList().toList();

            if (!preBuild(listener, reporters)) {
                return Result.FAILURE;
            }

            Result r = null;
            final NWDIBuild build = NWDIBuild.this;
            buildDevelopmentComponents(listener, build);
            // update DCs with info from various config/log files now on disk
            final DevelopmentComponentUpdater updater =
                new DevelopmentComponentUpdater(build.getWorkspace().absolutize().getName(),
                    build.getDevelopmentComponentFactory());
            updater.execute();

            return r;
        }

        /**
         * Creates or updates the DTR and development configuration files used
         * by DC tool.
         * 
         * @throws IOException
         *             when creation of one of the configuration files fails.
         * @throws InterruptedException
         *             when the operation was canceled by the user.
         */
        private void createOrUpdateConfiguration() throws IOException, InterruptedException {
            final DtrConfigCreator configCreator =
                new DtrConfigCreator(getWorkspace(), NWDIBuild.this.getDevelopmentConfiguration(), NWDIBuild.this
                    .getProject().getConfDef());
            configCreator.execute();
        }

        /**
         * @param listener
         * @param build
         * @throws IOException
         * @throws InterruptedException
         */
        protected void buildDevelopmentComponents(final BuildListener listener, final NWDIBuild build)
            throws IOException, InterruptedException {
            final DCToolCommandExecutor executor = build.getDCToolExecutor(this.launcher);
            listener.getLogger().append(
                executor.execute(new BuildDevelopmentComponentsCommandBuilder(build.getDevelopmentComponentFactory(),
                    build.getDevelopmentConfiguration())));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void post2(final BuildListener listener) throws Exception {
            if (!performAllBuildSteps(listener, reporters, true)) {
                setResult(Result.FAILURE);
            }

            if (!performAllBuildSteps(listener, project.getProperties(), true)) {
                setResult(Result.FAILURE);
            }
        }
    }
}