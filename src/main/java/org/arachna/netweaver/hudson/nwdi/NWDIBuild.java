/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import hudson.Launcher;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.Publisher;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dctool.DCToolCommandExecutor;
import org.arachna.netweaver.dctool.DCToolDescriptor;
import org.arachna.netweaver.dctool.DcToolCommandExecutionResult;
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
public final class NWDIBuild extends Build<NWDIProject, NWDIBuild> {
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

    /**
     * Create an instance of <code>NWDIBuild</code> using the given
     * <code>NWDIProject</code> and build directory.
     * 
     * @param project
     *            parent to use for creating this build.
     * @param buildDir
     *            build directory used for this build.
     * @throws IOException
     *             when saving the current build number fails.
     */
    public NWDIBuild(final NWDIProject project, final File buildDir) throws IOException {
        super(project, buildDir);
    }

    @Override
    public void run() {
        run(new RunnerImpl(this.getDevelopmentConfiguration(), this.getDevelopmentComponentFactory()));
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
                this.developmentConfiguration = confdefReader.read(new StringReader(this.project.getConfDef()));
            }
            catch (final SAXException e) {
                throw new RuntimeException(e);
            }
        }

        return this.developmentConfiguration;
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
     * Runner for this build.
     * 
     * @author Dirk Weigenand
     */
    private final class RunnerImpl extends AbstractRunner {
        /**
         * registry/factory for development components to use throughout the
         * build.
         */
        private final DevelopmentComponentFactory dcFactory;

        /**
         * development configuration to use throughout the build.
         */
        private final DevelopmentConfiguration developmentConfiguration;

        RunnerImpl(final DevelopmentConfiguration developmentConfiguration, final DevelopmentComponentFactory dcFactory) {
            this.developmentConfiguration = developmentConfiguration;
            this.dcFactory = dcFactory;
        }

        /**
         * collection of reporter plugins to be run prior to building.
         */
        private final List<Publisher> reporters = new ArrayList<Publisher>();

        /**
         * Builds all (changed) development components and updates the in core
         * information about them (i.e. all DCs associated with the current
         * track in the development configuration stored in this build) so that
         * this information can be used in post build tasks for e.g. quality
         * control or generation of documentation.
         * 
         * @param listener
         *            the {@link BuildListener} to use for e.g. reporting.
         * @return the build result {@see Result}.
         * @throws Exception
         *             forward all Exceptions thrown by underlying code
         */
        @Override
        protected Result doRun(final BuildListener listener) throws Exception {
            this.reporters.addAll(getProject().getPublishersList().toList());

            if (!preBuild(listener, this.reporters)) {
                return Result.FAILURE;
            }

            final NWDIBuild build = NWDIBuild.this;
            final PrintStream logger = listener.getLogger();

            final Result r = buildDevelopmentComponents(logger, build);

            // FIXME: doesn't find any DCs at the moment
            final DevelopmentComponentUpdater updater =
                new DevelopmentComponentUpdater(build.getWorkspace().absolutize().getName(), this.dcFactory);
            updater.execute();

            return r;
        }

        private int countOfDcsNeedingRebuild() {
            int count = 0;

            for (final DevelopmentComponent component : this.dcFactory.getAll()) {
                if (component.isNeedsRebuild()) {
                    count++;
                }
            }

            return count;
        }

        /**
         * @param listener
         * @param build
         * @throws IOException
         * @throws InterruptedException
         */
        protected Result buildDevelopmentComponents(final PrintStream logger, final NWDIBuild build)
            throws IOException, InterruptedException {
            logger.append(String.format("Building %s development components.\n", countOfDcsNeedingRebuild()));

            // TODO: annotate build results with links to build.log files.
            final DCToolCommandExecutor executor = build.getDCToolExecutor(this.launcher);
            final DcToolCommandExecutionResult result =
                executor.execute(new BuildDevelopmentComponentsCommandBuilder(this.dcFactory,
                    this.developmentConfiguration));
            logger.append("Done building development components.\n");

            return result.getExitCode() == 0 ? null : Result.FAILURE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void post2(final BuildListener listener) throws Exception {
            if (!performAllBuildSteps(listener, this.reporters, true)) {
                setResult(Result.FAILURE);
            }

            if (!performAllBuildSteps(listener, project.getProperties(), true)) {
                setResult(Result.FAILURE);
            }
        }
    }
}