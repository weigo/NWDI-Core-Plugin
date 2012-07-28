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
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.arachna.ant.AntHelper;
import org.arachna.ant.ExcludesFactory;
import org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dc.types.JdkHomeAlias;
import org.arachna.netweaver.hudson.dtr.browser.Activity;
import org.arachna.netweaver.hudson.dtr.browser.ActivityResource;
import org.arachna.netweaver.hudson.nwdi.confdef.ConfDefReader;
import org.arachna.netweaver.hudson.util.FilePathHelper;
import org.arachna.netweaver.tools.DIToolCommandExecutionResult;
import org.arachna.netweaver.tools.DIToolDescriptor;
import org.arachna.netweaver.tools.dc.DCToolCommandExecutor;
import org.arachna.xml.XmlReaderHelper;
import org.xml.sax.SAXException;

/**
 * A job for building a NWDI development configuration/track.
 * 
 * @author Dirk Weigenand
 */
public final class NWDIBuild extends AbstractBuild<NWDIProject, NWDIBuild> {
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
     * Create an instance of <code>NWDIBuild</code> using the given <code>NWDIProject</code>.
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
     * Create an instance of <code>NWDIBuild</code> using the given <code>NWDIProject</code> and build directory.
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
     * @return the <code>DevelopmentConfiguration</code> used throughout this build.
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
     * Calculate build sequence for development components affected by activities that triggered this build.
     * 
     * @return build sequence for development components affected by activities that triggered this build.
     */
    public Collection<DevelopmentComponent> getAffectedDevelopmentComponents() {
        if (affectedComponents == null) {
            final NWDIRevisionState revisionState = this.getAction(NWDIRevisionState.class);
            final Set<DevelopmentComponent> affectedComponents = new HashSet<DevelopmentComponent>();

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

            Collection<DevelopmentComponent> components = new LinkedList<DevelopmentComponent>();

            for (DevelopmentComponent component : affectedComponents) {
                try {
                    if (componentExists(component)) {
                        components.add(component);
                    }
                    else {
                        // remove DCs from build that might have come from activities but
                        dcFactory.remove(component);
                        // component.getCompartment().remove(component);
                    }
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace(System.err);
                }
                catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace(System.err);
                }
            }

            // update usage relations from public part references.
            dcFactory.updateUsingDCs();
            final ComponentsNeedingRebuildFinder finder = new ComponentsNeedingRebuildFinder();
            final DependencySorter dependencySorter =
                new DependencySorter(dcFactory,
                    finder.calculateDevelopmentComponentsThatNeedRebuilding(components));

            this.affectedComponents = dependencySorter.determineBuildSequence();
        }

        return affectedComponents;
    }

    private boolean componentExists(DevelopmentComponent component) throws IOException, InterruptedException {
        FilePath dcFolder = this.getWorkspace().child(".dtc/DCs");

        return dcFolder.child(String.format("%s/%s/_comp/.dcdef", component.getVendor(), component.getName())).exists();
    }

    /**
     * Calculate build sequence for development components affected by activities that triggered this build.
     * 
     * @param filter
     *            filter for development components affected by this build. Can be used to filter only DCs relevant for a certain builder.
     * @return build sequence for development components affected by activities that triggered this build.
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
     * Returns the {@link DevelopmentComponentFactory} used throughout this build.
     * 
     * @return <code>DevelopmentComponentFactory</code> used as registry for development components.
     */
    public DevelopmentComponentFactory getDevelopmentComponentFactory() {
        return dcFactory;
    }

    /**
     * Returns a factory for generating ant excludes based on development component type.
     * 
     * @return a factory for generating ant excludes based on development component type.
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
     * Returns the {@link DCToolCommandExecutor} used throughout this build using the given {@link Launcher}.
     * 
     * @param launcher
     *            the launcher to use executing DC tool.
     * @return <code>DCToolCommandExecutor</code> to execute DC tool commands.
     */
    DCToolCommandExecutor getDCToolExecutor(final Launcher launcher) {
        final DevelopmentConfiguration configuration = getDevelopmentConfiguration();
        final NWDIProject.DescriptorImpl descriptor = NWDIProject.DescriptorImpl.DESCRIPTOR;
        final DIToolDescriptor dcToolDescriptor = getDCToolDescriptor(configuration, descriptor);
        return new DCToolCommandExecutor(launcher, getWorkspace(), dcToolDescriptor, configuration);
    }

    /**
     * @param configuration
     * @param descriptor
     * @return
     */
    DIToolDescriptor getDCToolDescriptor(final DevelopmentConfiguration configuration, final NWDIProject.DescriptorImpl descriptor) {
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

        final DIToolDescriptor dcToolDescriptor =
            new DIToolDescriptor(descriptor.getUser(), descriptor.getPassword(), nwdiToolLibraryFolder,
                descriptor.getConfiguredJdkHomePaths(), descriptor.getJdkOpts());
        return dcToolDescriptor;
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
         * Builds all (changed) development components and updates the in core information about them (i.e. all DCs associated with the
         * current track in the development configuration stored in this build) so that this information can be used in post build tasks for
         * e.g. quality control or generation of documentation.
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
            Result result = Result.SUCCESS;

            if (!preBuild(listener, project.getBuilders())) {
                result = Result.FAILURE;
            }

            if (Result.SUCCESS.equals(result) && !preBuild(listener, getProject().getPublishers())) {
                result = Result.FAILURE;
            }

            if (Result.SUCCESS.equals(result) && !buildDevelopmentComponents(listener.getLogger()).isExitCodeOk()) {
                result = Result.FAILURE;
            }

            if (Result.SUCCESS.equals(result)) {
                updateSourceCodeLocations(listener.getLogger(), antHelper);
                writeDevelopmentConfiguration(listener.getLogger());
            }

            if (Result.SUCCESS.equals(result) && !build(project.getBuilders(), antHelper)) {
                result = FAILURE;
            }

            return result;
        }

        /**
         * Run all configured build steps.
         * 
         * @param steps
         *            the build steps to execute.
         * @param antHelper
         *            various NWDI-*-Plugins define {@link AntTaskBuilder}s that need an {@link AntHelper} to execute.
         * @return the result of the executed build steps: <code>true</code> when all build steps executed successfully, <code>false</code>
         *         when a build step failed.
         * @throws IOException
         *             re-thrown from the perform method that executes the build steps
         * @throws InterruptedException
         *             when the build was interrupted
         */
        private boolean build(final Collection<Builder> steps, AntHelper antHelper) throws IOException,
            InterruptedException {
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
         * @return result of DC tool execution
         * @throws IOException
         *             re-thrown from executing the DC build
         * @throws InterruptedException
         *             re-thrown from executing the DC build
         */
        protected DIToolCommandExecutionResult buildDevelopmentComponents(final PrintStream logger) throws IOException,
            InterruptedException {
            NWDIBuild nwdiBuild = NWDIBuild.this;
            final Collection<DevelopmentComponent> affectedComponents =
                nwdiBuild.getAffectedDevelopmentComponents();

            DIToolCommandExecutionResult result = new DIToolCommandExecutionResult("", 0);

            if (!affectedComponents.isEmpty()) {
                logger.append(String.format("Building %s development components.\n", affectedComponents.size()));

                for (final DevelopmentComponent component : affectedComponents) {
                    logger.append(component.getName()).append('\n');
                }

                result = getDCToolExecutor(launcher).buildDevelopmentComponents(affectedComponents);
                // AntHelper antHelper =
                // new AntHelper(FilePathHelper.makeAbsolute(nwdiBuild.getWorkspace()), nwdiBuild.getDevelopmentComponentFactory());
                // DCToolDescriptor dcToolDescriptor = nwdiBuild.getDCToolDescriptor(
                // nwdiBuild.getDevelopmentConfiguration(),
                // nwdiBuild.getParent().getDescriptor());
                // File workspace = new File(antHelper.getPathToWorkspace());
                //
                // // for (DevelopmentComponent component : affectedComponents) {
                // DCBuilder builder = new DCBuilder(workspace, dcToolDescriptor, affectedComponents /* Arrays.asList(component) */);
                // // FIXME: honor build result: i.e. return value
                // builder.perform(nwdiBuild, launcher, listener);
                // // }
            }

            for (final DevelopmentComponent component : affectedComponents) {
                FilePath buildXml =
                    nwdiBuild.getWorkspace().child(
                        String.format("%s/DCs/%s/%s/_comp/gen/default/logs/build.xml", DIToolDescriptor.DTC_FOLDER,
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

            return result;
        }

        /**
         * Saves the current development configuration as XML to the workspace folder.
         * 
         * @param logger
         *            logger for logging exceptions
         * @throws IOException
         *             re-thrown from {@link FilePath} operations
         * @throws InterruptedException
         *             re-thrown from {@link FilePath} operations
         */
        private void writeDevelopmentConfiguration(final PrintStream logger) throws IOException, InterruptedException {
            try {
                FilePath devConfXml = NWDIBuild.this.getWorkspace().child("DevelopmentConfiguration.xml");
                Writer content = new OutputStreamWriter(devConfXml.write(), "UTF-8");
                DevelopmentConfigurationXmlWriter xmlWriter =
                    new DevelopmentConfigurationXmlWriter(NWDIBuild.this.getDevelopmentConfiguration());
                xmlWriter.write(content);
                content.close();
            }
            catch (XMLStreamException e) {
                e.printStackTrace(logger);
            }
        }

        /**
         * Update all development components with the location of their various source folders.
         * 
         * This is necessary since f.e. WebDynpro DCs have <code>gen_ddic</code> and <code>gen_wdp</code> folders that are not listed in
         * <code>.dcdef</code> but are created when the component is built. Those folders have to be considered too when running analysis
         * plugins.
         * 
         * @param logger
         *            Logger to report actions back to build.
         * @param antHelper
         *            {@link AntHelper} to compute the base location of development components.
         * @param antHelper
         */
        private void updateSourceCodeLocations(final PrintStream logger, AntHelper antHelper) {
            Collection<Compartment> compartments =
                NWDIBuild.this.getDevelopmentConfiguration().getCompartments(CompartmentState.Source);

            for (Compartment compartment : compartments) {
                for (final DevelopmentComponent component : compartment.getDevelopmentComponents()) {
                    final BuildLogParser parser = new BuildLogParser(antHelper.getBaseLocation(component));
                    parser.parse();
                    component.setSourceFolders(parser.getSourceFolders());
                    component.setOutputFolder(parser.getOutputFolder());
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
