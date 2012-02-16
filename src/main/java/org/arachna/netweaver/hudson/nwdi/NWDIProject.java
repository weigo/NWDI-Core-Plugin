/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.BuildableItemWithBuildWrappers;
import hudson.model.DependencyGraph;
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.model.SCMedItem;
import hudson.model.Saveable;
import hudson.model.TopLevelItem;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.Descriptor.FormException;
import hudson.model.Hudson;
import hudson.scm.SCM;
import hudson.tasks.BuildStep;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrappers;
import hudson.tasks.Builder;
import hudson.tasks.Publisher;
import hudson.triggers.Trigger;
import hudson.util.DescribableList;
import hudson.util.FormValidation;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.arachna.netweaver.dctool.JdkHomePaths;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * A project for building tracks residing in a NWDI.
 * 
 * @author Dirk Weigenand
 */
public class NWDIProject extends AbstractProject<NWDIProject, NWDIBuild> implements SCMedItem, Saveable,
    BuildableItemWithBuildWrappers, TopLevelItem {
    /**
     * Constant for a 1000 milliseconds.
     */
    private static final float THOUSAND_MILLI_SECONDS = 1000f;

    /**
     * parameter name for project configuration controlling whether the
     * workspace should be clean before building.
     */
    private static final String PARAMETER_CLEAN_COPY = "cleanCopy";

    /**
     * Store the content of the '.confdef' configuration file for a development
     * configuration.
     */
    private String confDef;

    /**
     * clean workspace before building when <code>true</code>.
     */
    private boolean cleanCopy;

    /**
     * List of active {@link Builder}s configured for this project.
     */
    private DescribableList<Builder, Descriptor<Builder>> builders = new DescribableList<Builder, Descriptor<Builder>>(
        this);

    /**
     * List of active {@link Publisher}s configured for this project.
     */
    private DescribableList<Publisher, Descriptor<Publisher>> publishers =
        new DescribableList<Publisher, Descriptor<Publisher>>(this);

    /**
     * List of active {@link BuildWrapper}s configured for this project.
     */
    private DescribableList<BuildWrapper, Descriptor<BuildWrapper>> buildWrappers =
        new DescribableList<BuildWrapper, Descriptor<BuildWrapper>>(this);

    /**
     * Create an instance of a NWDI project.
     * 
     * @param parent
     *            the parent <code>ItemGroup</code> in the project configuration
     *            page.
     * @param name
     *            project name
     */
    public NWDIProject(final ItemGroup<?> parent, final String name) {
        super(parent, name);
    }

    /**
     * Create an instance of a NWDI project using the given project name and
     * configuration.
     * 
     * @param name
     *            project name
     * @param confDef
     *            development configuration file
     * @param cleanCopy
     *            clean workspace before building when <code>true</code>
     */
    @DataBoundConstructor
    public NWDIProject(final String name, final String confDef, final boolean cleanCopy) {
        super(Hudson.getInstance(), name);
        this.confDef = confDef;
        this.cleanCopy = cleanCopy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<NWDIBuild> getBuildClass() {
        return NWDIBuild.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Hudson getParent() {
        return Hudson.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLoad(final ItemGroup<? extends Item> parent, final String name) throws IOException {
        super.onLoad(parent, name);
        builders.setOwner(this);
        publishers.setOwner(this);
        buildWrappers.setOwner(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hudson.model.AbstractProject#checkout(hudson.model.AbstractBuild,
     * hudson.Launcher, hudson.model.BuildListener, java.io.File)
     */
    @Override
    public boolean checkout(final AbstractBuild build, final Launcher launcher, final BuildListener listener,
        final File changelogFile) throws IOException, InterruptedException {
        final NWDIBuild nwdiBuild = (NWDIBuild)build;

        if (nwdiBuild.isCleanCopy()) {
            final long start = System.currentTimeMillis();
            // FIXME: add I18N for message
            listener.getLogger().append("Wiping workspace...");
            build.getWorkspace().deleteContents();
            listener.getLogger().append(
                String.format(" (%f sec.).\n", (System.currentTimeMillis() - start) / THOUSAND_MILLI_SECONDS));
        }

        final DtrConfigCreator configCreator =
            new DtrConfigCreator(build.getWorkspace(), nwdiBuild.getDevelopmentConfiguration(), getConfDef());

        configCreator.execute();

        return super.checkout(build, launcher, listener, changelogFile);
    }

    /**
     * {@inheritDoc}
     */
    public DescriptorImpl getDescriptor() {
        return NWDIProject.DescriptorImpl.DESCRIPTOR;
    }

    /**
     * Returns the SCM to be used by this NWDI project.
     * 
     * @return SCM to be used by this NWDI project.
     */
    @Override
    public SCM getScm() {
        return new NWDIScm(cleanCopy, DescriptorImpl.DESCRIPTOR.getUser(), DescriptorImpl.DESCRIPTOR.getPassword());
    }

    @Override
    public void setScm(final SCM scm) throws IOException {
        final boolean useGivenScm = scm != null && NWDIScm.class.equals(scm.getClass());

        super.setScm(useGivenScm ? scm : new NWDIScm(cleanCopy, DescriptorImpl.DESCRIPTOR.getUser(),
            DescriptorImpl.DESCRIPTOR.getPassword()));
    }

    public AbstractProject<?, ?> asProject() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void submit(final StaplerRequest req, final StaplerResponse rsp) throws IOException, ServletException,
        FormException {
        final JSONObject json = req.getSubmittedForm();
        confDef = json.getString("developmentConfiguration");
        cleanCopy = json.getBoolean(PARAMETER_CLEAN_COPY);
        setScm(new NWDIScm(cleanCopy, getDescriptor().getUser(), getDescriptor().getPassword()));

        buildWrappers.rebuild(req, json, BuildWrappers.getFor(this));
        builders.rebuildHetero(req, json, Builder.all(), "builder");
        publishers.rebuild(req, json, BuildStepDescriptor.filter(Publisher.all(), this.getClass()));

        // not needed since setting the SCM saves automatically.
        save();
        super.submit(req, rsp);
    }

    public List<Builder> getBuilders() {
        return builders.toList();
    }

    public Map<Descriptor<Publisher>, Publisher> getPublishers() {
        return publishers.toMap();
    }

    public DescribableList<Builder, Descriptor<Builder>> getBuildersList() {
        return builders;
    }

    public DescribableList<Publisher, Descriptor<Publisher>> getPublishersList() {
        return publishers;
    }

    public Map<Descriptor<BuildWrapper>, BuildWrapper> getBuildWrappers() {
        return buildWrappers.toMap();
    }

    public DescribableList<BuildWrapper, Descriptor<BuildWrapper>> getBuildWrappersList() {
        return buildWrappers;
    }

    @Override
    protected List<Action> createTransientActions() {
        List<Action> r = super.createTransientActions();

        for (BuildStep step : getBuildersList())
            r.addAll(step.getProjectActions(this));
        for (BuildStep step : getPublishersList())
            r.addAll(step.getProjectActions(this));
        for (BuildWrapper step : getBuildWrappers().values())
            r.addAll(step.getProjectActions(this));
        for (Trigger trigger : getTriggers().values())
            r.addAll(trigger.getProjectActions());

        return r;
    }

    /**
     * Descriptor for NWDIProjects. Contains the global configuration commonly
     * used for different NWDI tracks.
     * 
     * @author Dirk Weigenand
     */
    @Extension
    public static class DescriptorImpl extends AbstractProjectDescriptor {
        /**
         * Global descriptor/configuration for NWDIProjects.
         */
        public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

        /**
         * Constant for password request parameter.
         */
        protected static final String PASSWORD = "NWDIPlugin.password";

        /**
         * Constant for user request parameter.
         */
        protected static final String USER = "NWDIPlugin.user";

        /**
         * Constant for nwdi tool libraries folder request parameter.
         */
        protected static final String TOOL_LIB_FOLDER = "NWDIPlugin.nwdiToolLibFolder";

        /**
         * Constant for nwdi tool libraries folder request parameter.
         */
        protected static final String TOOL_LIB_FOLDER71 = "NWDIPlugin.nwdiToolLibFolder71";

        /**
         * Constant for JDK home paths request parameter.
         */
        protected static final String JDK_HOME_PATHS = "NWDIPlugin.jdkHomePaths";

        /**
         * Constant for dc tool sub folder in nwdi tool library folder.
         */
        private static final String DC_SUB_FOLDER = "dc";

        /**
         * UME user to use when connecting to NWDI.
         */
        private String user;

        /**
         * password to use when authenticating.
         */
        private String password;

        /**
         * folder where the NWDI tool library files are stored.
         */
        private String nwdiToolLibFolder;

        /**
         * folder where the NWDI tool library files are stored.
         */
        private String nwdiToolLibFolder71;

        /**
         * 'JDK_HOME_PATHS'.
         */
        private String jdkHomePaths;

        /**
         * Options that should be passed to the JDK executing the DC tool.
         */
        private String jdkOpts;

        /**
         * Create descriptor for NWDI-Projects and load global configuration
         * data.
         */
        public DescriptorImpl() {
            load();
        }

        /**
         * Returns the user for authentication against the NWDI.
         * 
         * @return the user for authentication against the NWDI
         */
        public String getUser() {
            return user;
        }

        /**
         * Sets the user for authentication against the NWDI.
         * 
         * @param user
         *            the user for authentication against the NWDI.
         */
        public void setUser(final String user) {
            this.user = user;
        }

        /**
         * @return the password
         */
        public String getPassword() {
            return password;
        }

        /**
         * @param password
         *            the password to set
         */
        public void setPassword(final String password) {
            this.password = password;
        }

        /**
         * Get the folder of the DI tools for NetWeaver 7.0 and before.
         * 
         * @return the folder of the DI tools for NetWeaver 7.0 and before.
         */
        public String getNwdiToolLibFolder() {
            return nwdiToolLibFolder;
        }

        /**
         * Set the folder of the DI tools for NetWeaver 7.0 and before.
         * 
         * @param nwdiToolLibFolder
         *            the folder of the DI tools for NetWeaver 7.0 and before.
         */
        public void setNwdiToolLibFolder(final String nwdiToolLibFolder) {
            this.nwdiToolLibFolder = nwdiToolLibFolder;
        }

        /**
         * Get the folder of the DI tools for NetWeaver 7.1 and later.
         * 
         * @return the folder of the DI tools for NetWeaver 7.1 and later.
         */
        public String getNwdiToolLibFolder71() {
            return nwdiToolLibFolder71;
        }

        /**
         * Get the folder of the DI tools for NetWeaver 7.1 and later.
         * 
         * @param nwdiToolLibFolder
         *            the folder of the DI tools for NetWeaver 7.1 and later.
         */
        public void setNwdiToolLibFolder71(final String nwdiToolLibFolder) {
            nwdiToolLibFolder71 = nwdiToolLibFolder;
        }

        /**
         * Returns the paths to JDK installations to be used for building
         * tracks.
         * 
         * @return the paths to JDK installations to be used for building
         *         tracks.
         */
        public String getJdkHomePaths() {
            return jdkHomePaths;
        }

        /**
         * Set the paths to JDK installations to be used for building tracks.
         * 
         * @param jdkHomePaths
         *            the paths to JDK installations to be used for building
         *            tracks.
         */
        public void setJdkHomePaths(final String jdkHomePaths) {
            this.jdkHomePaths = jdkHomePaths;
        }

        /**
         * @return the jdkOpts
         */
        String getJdkOpts() {
            return jdkOpts;
        }

        /**
         * @param jdkOpts
         *            the jdkOpts to set
         */
        void setJdkOpts(String jdkOpts) {
            this.jdkOpts = jdkOpts;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * hudson.model.Descriptor#configure(org.kohsuke.stapler.StaplerRequest,
         * net.sf.json.JSONObject)
         */
        @Override
        public boolean configure(final StaplerRequest req, final JSONObject json) throws FormException {
            jdkHomePaths = Util.fixNull(json.getString("jdkHomePaths"));
            nwdiToolLibFolder = Util.fixNull(json.getString("nwdiToolLibFolder"));
            nwdiToolLibFolder71 = Util.fixNull(json.getString("nwdiToolLibFolder71"));
            user = Util.fixNull(json.getString("user"));
            password = Util.fixNull(json.getString("password"));
            jdkOpts = Util.fixNull(json.getString("jdkOpts"));

            save();

            return super.configure(req, json);
        }

        /**
         * Validate the 'NwdiToolLibFolder' parameter.
         * 
         * @param value
         *            the form value for the 'NwdiToolLibFolder' field.
         * @return the form validation value.
         */
        public FormValidation doNwdiToolLibFolderCheck(@QueryParameter final String value) {
            return validateNwdiToolLibraryFolder(value);
        }

        /**
         * Validate the 'NwdiToolLibFolder' parameter.
         * 
         * @param value
         *            the form value for the 'NwdiToolLibFolder' field.
         * @return the form validation value.
         */
        public FormValidation doNwdiToolLibFolder71Check(@QueryParameter final String value) {
            return validateNwdiToolLibraryFolder(value);
        }

        /**
         * Verify the given values for options that should be passed to the JDK
         * executing the dctool.
         * 
         * @param value
         *            verify that all options passed to the JDK start with -X.
         * @return
         */
        public FormValidation doJdkOptsCheck(@QueryParameter final String value) {
            String[] jdkOpts = value == null ? new String[0] : value.split(" ");
            FormValidation result = FormValidation.ok();

            for (String option : jdkOpts) {
                if (!option.startsWith("-X")) {
                    result = FormValidation.error(Messages.NWDIProject_invalid_jdk_option(option));
                    break;
                }
            }

            return result;
        }

        /**
         * @param value
         * @return
         */
        private FormValidation validateNwdiToolLibraryFolder(final String value) {
            FormValidation result = FormValidation.ok();
            final String nwdiToolLibFolder = Util.fixEmptyAndTrim(value);

            if (nwdiToolLibFolder == null) {
                result = FormValidation.error(Messages.NWDIProject_NwdiToolLibFolder_missing());
            }
            else {
                final FilePath folder = new FilePath(new File(nwdiToolLibFolder));

                try {
                    if (!folder.exists()) {
                        result = FormValidation.error(Messages.NWDIProject_NwdiToolLibFolder_nonexistant(value));
                    }
                    else {
                        // look for a 'dc' sub folder in the tools folder
                        result = validateDcToolFolder(folder);
                    }
                }
                catch (final IOException e) {
                    result = FormValidation.error(Messages.NWDIProject_NwdiToolLibFolder_ioexception());
                }
                catch (final InterruptedException e) {
                    result = FormValidation.error("Form validation has been cancelled.");
                }
            }

            return result;
        }

        /**
         * Validate that the given <code>FilePath</code> contains a 'dc' sub
         * folder.
         * 
         * @param folder
         *            the 'tools' folder of a NWDI tool library installation.
         * @return the validation result <code>FormValidation.ok()</code> when
         *         the 'dc' sub folder exists,
         *         <code>FormValidation.error()</code> otherwise.
         * @throws IOException
         *             when an error occurred accessing the folder.
         * @throws InterruptedException
         *             when the operation was canceled.
         */
        protected FormValidation validateDcToolFolder(final FilePath folder) throws IOException, InterruptedException {
            return folder != null && folder.child(DC_SUB_FOLDER).exists() ? FormValidation.ok() : FormValidation
                .error(Messages.NWDIProject_NwdiToolLibFolder_no_proper_installation_folder(DC_SUB_FOLDER));
        }

        /**
         * Validate the given JdkHomePath.
         * 
         * @param value
         *            path to a JDK.
         * @return the result of the validation.
         */
        public FormValidation doJdkHomePathsCheck(@QueryParameter final String value) {
            final JdkHomePathsParser parser = new JdkHomePathsParser(value);
            final JdkHomePaths paths = parser.parse();

            FormValidation result = FormValidation.ok();

            if (paths.getAliases().isEmpty()) {
                result = FormValidation.error(Messages.NWDIProject_specify_jdk_homes());
            }
            else if (parser.hasInvalidJdkHomeNames()) {
                result =
                    FormValidation.error(Messages.NWDIProject_invalid_jdk_homes_specified(parser
                        .getInvalidJdkHomeNames()));
            }

            return result;
        }

        /**
         * Validate the 'user' parameter.
         * 
         * @param value
         *            the form value for the 'user' field.
         * @return the form validation value.
         */
        public FormValidation doUserCheck(@QueryParameter final String value) {
            return Util.fixEmptyAndTrim(value) == null ? FormValidation.error(Messages.NWDIProject_user_missing())
                : FormValidation.ok();
        }

        /**
         * Validate the 'password' parameter.
         * 
         * @param value
         *            the form value for the 'password' field.
         * @return the form validation value.
         */
        public FormValidation doPasswordCheck(@QueryParameter final String value) {
            return Util.fixEmptyAndTrim(value) == null ? FormValidation.error(Messages.NWDIProject_password_missing())
                : FormValidation.ok();
        }

        /**
         * Returns the path mappings for the configured JDK homes.
         * 
         * @return path mappings for the configured JDK homes.
         */
        JdkHomePaths getConfiguredJdkHomePaths() {
            return new JdkHomePathsParser(getJdkHomePaths()).parse();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getDisplayName() {
            return Messages.NWDIProject_title();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public NWDIProject newInstance(final ItemGroup group, final String name) {
            return new NWDIProject(group, name);
        }
    }

    @Override
    public boolean isFingerprintConfigured() {
        return false;
    }

    @Override
    protected void buildDependencyGraph(final DependencyGraph graph) {
    }

    /**
     * Set the content of the '.confdef' configuration file.
     * 
     * @param confDef
     *            content of the '.confdef' configuration file.
     */
    public void setConfDef(final String confDef) {
        this.confDef = confDef;
    }

    /**
     * Return the content of the '.confdef' configuration file.
     * 
     * @return the content of the '.confdef' configuration file.
     */
    public String getConfDef() {
        return confDef;
    }

    /**
     * Return whether the workspace should be cleaned before building.
     * 
     * @return whether the workspace should be cleaned before building (
     *         <code>true</code> yes, leave it as it is otherwise).
     */
    public boolean isCleanCopy() {
        return cleanCopy;
    }

    /**
     * Indicate whether the workspace should be cleaned before building.
     * 
     * @param cleanCopy
     *            whether the workspace should be cleaned before building (
     *            <code>true</code> yes, leave it as it is otherwise).
     */
    public void setCleanCopy(final boolean cleanCopy) {
        this.cleanCopy = cleanCopy;
    }
}
