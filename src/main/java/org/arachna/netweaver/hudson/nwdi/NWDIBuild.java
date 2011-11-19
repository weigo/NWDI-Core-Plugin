/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import static hudson.model.Result.FAILURE;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.tasks.BuildStep;
import hudson.tasks.Builder;
import hudson.tasks.Publisher;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.arachna.ant.AntHelper;
import org.arachna.ant.ExcludesFactory;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dctool.DCToolCommandExecutor;
import org.arachna.netweaver.dctool.DCToolDescriptor;
import org.arachna.netweaver.dctool.DcToolCommandExecutionResult;
import org.arachna.netweaver.dctool.JdkHomeAlias;
import org.arachna.netweaver.hudson.dtr.browser.Activity;
import org.arachna.netweaver.hudson.dtr.browser.ActivityResource;
import org.arachna.netweaver.hudson.nwdi.confdef.ConfDefReader;
import org.arachna.netweaver.hudson.util.FilePathHelper;
import org.arachna.xml.XmlReaderHelper;
import org.xml.sax.SAXException;

/**
 * A job for building a NWDI development configuration/track.
 * 
 * @author Dirk Weigenand
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
     * development components affected by activities leading to this build.
     */
    private transient Collection<DevelopmentComponent> affectedComponents;

    /**
     * Factory for generating ant excludes based on development component type.
     */
    private final transient ExcludesFactory excludesFactory = new ExcludesFactory();

    /**
     * wipe workspace before building.
     */
    private boolean cleanCopy;

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
        cleanCopy = project.isCleanCopy();
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
        run(new RunnerImpl());
    }

    /**
     * Returns the {@link DevelopmentConfiguration} used throughout this build.
     * 
     * @return the <code>DevelopmentConfiguration</code> used throughout this
     *         build.
     */
    public DevelopmentConfiguration getDevelopmentConfiguration() {
        if (developmentConfiguration == null) {
            try {
                final ConfDefReader confdefReader = new ConfDefReader();
                new XmlReaderHelper(confdefReader).parse(new StringReader(project.getConfDef()));
                developmentConfiguration = confdefReader.getDevelopmentConfiguration();
            }
            catch (final SAXException e) {
                throw new RuntimeException(e);
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

        return developmentConfiguration;
    }

    /**
     * Calculate build sequence for development components affected by
     * activities that triggered this build.
     * 
     * @return build sequence for development components affected by activities
     *         that triggered this build.
     */
    public Collection<DevelopmentComponent> getAffectedDevelopmentComponents() {
        if (affectedComponents == null) {
            final NWDIRevisionState revisionState = this.getAction(NWDIRevisionState.class);
            final Collection<DevelopmentComponent> affectedComponents = new HashSet<DevelopmentComponent>();

            for (final Activity activity : revisionState.getActivities()) {
                for (final ActivityResource resource : activity.getResources()) {
                    DevelopmentComponent component = resource.getDevelopmentComponent();

                    // ignore DCs without compartment: those were probably
                    // deleted.
                    if (component.getCompartment() != null) {
                        affectedComponents.add(component);
                    }
                }
            }

            // honor the cleanCopy property of NWDIProject
            for (final Compartment compartment : getDevelopmentConfiguration().getCompartments(CompartmentState.Source)) {
                for (final DevelopmentComponent component : compartment.getDevelopmentComponents()) {
                    if (component.isNeedsRebuild()) {
                        affectedComponents.add(component);
                    }
                }
            }

            // update usage relations from public part references.
            dcFactory.updateUsingDCs();
            final ComponentsNeedingRebuildFinder finder = new ComponentsNeedingRebuildFinder();
            final DependencySorter dependencySorter =
                new DependencySorter(dcFactory,
                    finder.calculateDevelopmentComponentsThatNeedRebuilding(affectedComponents));

            this.affectedComponents = dependencySorter.determineBuildSequence();
        }

        return affectedComponents;
    }

    /**
     * Calculate build sequence for development components affected by
     * activities that triggered this build.
     * 
     * @return build sequence for development components affected by activities
     *         that triggered this build.
     */
    public Collection<DevelopmentComponent> getAffectedDevelopmentComponents(final IDevelopmentComponentFilter filter) {
        final Collection<DevelopmentComponent> filteredDCs = new ArrayList<DevelopmentComponent>();

        if (filter != null) {
            for (final DevelopmentComponent component : this.getAffectedDevelopmentComponents()) {
                if (filter.accept(component)) {
                    filteredDCs.add(component);
                }
            }
        }

        return filteredDCs;
    }

    /**
     * Returns the {@link DevelopmentComponentFactory} used throughout this
     * build.
     * 
     * @return <code>DevelopmentComponentFactory</code> used as registry for
     *         development components.
     */
    public DevelopmentComponentFactory getDevelopmentComponentFactory() {
        return dcFactory;
    }

    /**
     * Returns a factory for generating ant excludes based on development
     * component type.
     * 
     * @return a factory for generating ant excludes based on development
     *         component type.
     */
    public ExcludesFactory getExcludesFactory() {
        return excludesFactory;
    }

    /**
     * 
     * @return the cleanCopy
     */
    public boolean isCleanCopy() {
        return cleanCopy;
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
        if (dcToolExecutor == null) {
            final NWDIProject.DescriptorImpl descriptor = getParent().getDescriptor();
            final DevelopmentConfiguration configuration = getDevelopmentConfiguration();
            final JdkHomeAlias alias = configuration.getJdkHomeAlias();
            String nwdiToolLibraryFolder = "";

            if (JdkHomeAlias.Jdk131Home.equals(alias) || JdkHomeAlias.Jdk142Home.equals(alias)) {
                nwdiToolLibraryFolder = descriptor.getNwdiToolLibFolder();
            }
            else if (JdkHomeAlias.Jdk150Home.equals(alias) || JdkHomeAlias.Jdk160Home.equals(alias)) {
                nwdiToolLibraryFolder = descriptor.getNwdiToolLibFolder71();
            }
            else {
                throw new RuntimeException(String.format("Cannot map JdkHomeAlias '%s' onto a NWDITOOLLIB folder.",
                    alias));
            }

            final DCToolDescriptor dcToolDescriptor =
                new DCToolDescriptor(descriptor.getUser(), descriptor.getPassword(), nwdiToolLibraryFolder,
                    descriptor.getConfiguredJdkHomePaths());
            dcToolExecutor = new DCToolCommandExecutor(launcher, getWorkspace(), dcToolDescriptor, configuration);
        }

        return dcToolExecutor;
    }

    /**
     * Runner for this build.
     * 
     * @author Dirk Weigenand
     */
    private final class RunnerImpl extends AbstractRunner {
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
            AntHelper antHelper = new AntHelper(FilePathHelper.makeAbsolute(getWorkspace()), dcFactory);
            reporters.addAll(getProject().getPublishersList().toList());

            if (!preBuild(listener, project.getBuilders())) {
                return Result.FAILURE;
            }

            if (!preBuild(listener, getProject().getPublishers())) {
                return Result.FAILURE;
            }

            final PrintStream logger = listener.getLogger();

            Result r = buildDevelopmentComponents(logger, antHelper);

            if (!Result.FAILURE.equals(r) && !build(listener, project.getBuilders(), antHelper)) {
                r = FAILURE;
            }

            return r;
        }

        private boolean build(final BuildListener listener, final Collection<Builder> steps, AntHelper antHelper)
            throws IOException, InterruptedException {
            for (final BuildStep bs : steps) {

                if (AntTaskBuilder.class.isAssignableFrom(bs.getClass())) {
                    ((AntTaskBuilder)bs).setAntHelper(antHelper);
                }

                if (!perform(bs, listener)) {
                    return false;
                }
            }

            return true;
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
        protected Result buildDevelopmentComponents(final PrintStream logger, AntHelper antHelper) throws IOException,
            InterruptedException {
            final Collection<DevelopmentComponent> affectedComponents =
                NWDIBuild.this.getAffectedDevelopmentComponents();
            logger.append(String.format("Building %s development components.\n", affectedComponents.size()));

            for (final DevelopmentComponent component : affectedComponents) {
                logger.append(component.getName()).append('\n');
            }

            // TODO: annotate build results with links to build.log files.
            final DcToolCommandExecutionResult result =
                getDCToolExecutor(launcher).buildDevelopmentComponents(affectedComponents);

            for (final DevelopmentComponent component : affectedComponents) {
                FilePath buildXml =
                    NWDIBuild.this.getWorkspace().child(
                        String.format("%s/DCs/%s/%s/_comp/gen/default/logs/build.xml", DCToolDescriptor.DTC_FOLDER,
                            component.getVendor(), component.getName()));
                if (buildXml.exists()) {
                    String content =
                        buildXml.readToString().replaceFirst(
                            "project name=\"DC Build\"",
                            String.format("project name=\"%s~%s\"", component.getVendor(),
                                component.getName().replace('/', '~')));
                    buildXml.write(content, "UTF-8");
                }
            }

            updateSourceCodeLocations(logger, antHelper);

            return result.isExitCodeOk() ? null : Result.FAILURE;
        }

        /**
         * Update all development components with the location of their various
         * source folders.
         * 
         * This is necessary since f.e. WebDynpro DCs have <code>gen_ddic</code>
         * and <code>gen_wdp</code> that are not listed in <code>.dcdef</code>
         * but are created when the component is built. Those folders have to be
         * considered too when running analysis plugins.
         * 
         * @param logger
         *            Logger to report actions back to build.
         */
        private void updateSourceCodeLocations(final PrintStream logger, AntHelper antHelper) {
            Collection<Compartment> compartments =
                NWDIBuild.this.getDevelopmentConfiguration().getCompartments(CompartmentState.Source);

            for (Compartment compartment : compartments) {
                for (final DevelopmentComponent component : compartment.getDevelopmentComponents()) {
                    final BuildLogParser parser = new BuildLogParser(antHelper.getBaseLocation(component));
                    parser.parse();
                    component.setSourceFolders(parser.getSourceFolders());
                }
            }
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
