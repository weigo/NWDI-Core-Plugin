/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.BuildListener;
import hudson.model.DependencyGraph;
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.model.TopLevelItem;
import hudson.model.AbstractBuild;
import hudson.model.Descriptor.FormException;
import hudson.model.Hudson;
import hudson.model.Project;
import hudson.scm.SCM;
import hudson.util.FormValidation;

import java.io.File;
import java.io.IOException;

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
public class NWDIProject extends Project<NWDIProject, NWDIBuild> implements TopLevelItem {
    /**
     * Constant for a 1000 milliseconds.
     */
    private static final float THOUSAND_MILLI_SECONDS = 1000f;

    /**
     * Global descriptor/configuration for NWDIProjects.
     */
    @Extension(ordinal = 1000)
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

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
        return DESCRIPTOR;
    }

    /**
     * Returns the SCM to be used by this NWDI project.
     * 
     * @return SCM to be used by this NWDI project.
     */
    @Override
    public SCM getScm() {
        return new NWDIScm(cleanCopy, DESCRIPTOR.getUser(), DESCRIPTOR.getPassword());
    }

    @Override
    public void setScm(final SCM scm) throws IOException {
        final boolean useGivenScm = scm != null && NWDIScm.class.equals(scm.getClass());

        super.setScm(useGivenScm ? scm : new NWDIScm(cleanCopy, DESCRIPTOR.getUser(), DESCRIPTOR.getPassword()));
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
        // not needed since setting the SCM saves automatically.
        save();
        super.submit(req, rsp);
    }

    /**
     * Descriptor for NWDIProjects. Contains the global configuration commonly
     * used for different NWDI tracks.
     * 
     * @author Dirk Weigenand
     */
    public static class DescriptorImpl extends AbstractProjectDescriptor {
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
         * 'JDK_HOME_PATHS'.
         */
        private String jdkHomePaths;

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
         * @return the nwdiToolLibFolder
         */
        public String getNwdiToolLibFolder() {
            return nwdiToolLibFolder;
        }

        /**
         * @param nwdiToolLibFolder
         *            the nwdiToolLibFolder to set
         */
        public void setNwdiToolLibFolder(final String nwdiToolLibFolder) {
            this.nwdiToolLibFolder = nwdiToolLibFolder;
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
            user = Util.fixNull(json.getString("user"));
            password = Util.fixNull(json.getString("password"));

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
            FormValidation result = FormValidation.ok();
            final String nwdiToolLibFolder = Util.fixEmptyAndTrim(value);

            if (nwdiToolLibFolder == null) {
                result = FormValidation.error(Messages.NWDIProject_NwdiToolLibFolder_missing());
            }
            else {
                final FilePath folder = new FilePath(new File(nwdiToolLibFolder));

                try {
                    if (!folder.exists()) {
                        result = FormValidation.error(Messages.NWDIProject_NwdiToolLibFolder_nonexistant());
                    }
                    else {
                        // look for a 'dc' sub folder in the tools folder
                        // (getParent()).
                        result = validateDcToolFolder(folder.getParent());
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
