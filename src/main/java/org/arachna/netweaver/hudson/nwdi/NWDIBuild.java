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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dctool.DCToolCommandExecutor;
import org.arachna.netweaver.dctool.DCToolDescriptor;
import org.arachna.netweaver.dctool.DcToolCommandExecutionResult;
import org.arachna.netweaver.hudson.dtr.browser.Activity;
import org.arachna.netweaver.hudson.dtr.browser.ActivityResource;
import org.arachna.netweaver.hudson.nwdi.confdef.ConfDefReader;
import org.arachna.xml.XmlReaderHelper;
import org.xml.sax.SAXException;

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
     * development components affected by activities leading to this build.
     */
    private transient Collection<DevelopmentComponent> affectedComponents;

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
                final ConfDefReader confdefReader = new ConfDefReader();
                new XmlReaderHelper(confdefReader).parse(new StringReader(this.project.getConfDef()));
                this.developmentConfiguration = confdefReader.getDevelopmentConfiguration();
            }
            catch (final SAXException e) {
                throw new RuntimeException(e);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return this.developmentConfiguration;
    }

    /**
     * Calculate build sequence for development components affected by
     * activities that triggered this build.
     * 
     * @return build sequence for development components affected by activities
     *         that triggered this build.
     */
    public Collection<DevelopmentComponent> getAffectedDevelopmentComponents() {
        if (this.affectedComponents == null) {
            final NWDIRevisionState revisionState = this.getAction(NWDIRevisionState.class);
            final Collection<DevelopmentComponent> affectedComponents = new HashSet<DevelopmentComponent>();

            for (final Activity activity : revisionState.getActivities()) {
                for (final ActivityResource resource : activity.getResources()) {
                    affectedComponents.add(resource.getDevelopmentComponent());
                }
            }

            // update usage relations from public part references.
            this.dcFactory.updateUsingDCs();
            final ComponentsNeedingRebuildFinder finder = new ComponentsNeedingRebuildFinder();
            final DependencySorter dependencySorter =
                new DependencySorter(this.dcFactory,
                    finder.calculateDevelopmentComponentsThatNeedRebuilding(affectedComponents));

            this.affectedComponents = dependencySorter.determineBuildSequence();
        }

        return this.affectedComponents;
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

        /**
         * collection of reporter plugins to be run prior to building.
         */
        private final List<Publisher> reporters = new ArrayList<Publisher>();

        /**
         * Create an instance of <code>RunnerImpl</code> with the given
         * {@link DevelopmentConfiguration} and
         * {@link DevelopmentComponentFactory}.
         * 
         * @param developmentConfiguration
         *            development configuration to be used in this run.
         * @param dcFactory
         *            development component registry to be used in this run.
         */
        RunnerImpl(final DevelopmentConfiguration developmentConfiguration, final DevelopmentComponentFactory dcFactory) {
            this.developmentConfiguration = developmentConfiguration;
            this.dcFactory = dcFactory;
        }

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

            final PrintStream logger = listener.getLogger();

            final Result r = buildDevelopmentComponents(logger);

            return r;
        }

        /**
         * build affected development components.
         * 
         * @param logger
         *            logger to log build messages
         * @return build result
         * @throws IOException
         * @throws InterruptedException
         */
        protected Result buildDevelopmentComponents(final PrintStream logger) throws IOException, InterruptedException {
            final Collection<DevelopmentComponent> affectedComponents =
                NWDIBuild.this.getAffectedDevelopmentComponents();
            logger.append(String.format("Building %s development components.\n", affectedComponents.size()));

            for (final DevelopmentComponent component : affectedComponents) {
                logger.append(component.getName()).append('\n');
            }
            // TODO: annotate build results with links to build.log files.
            final DCToolCommandExecutor executor = NWDIBuild.this.getDCToolExecutor(this.launcher);
            final DcToolCommandExecutionResult result =
                executor.execute(new BuildDevelopmentComponentsCommandBuilder(this.developmentConfiguration,
                    affectedComponents));
            logger.append("Done building development components.\n");

            return result.isExitCodeOk() ? null : Result.FAILURE;
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
