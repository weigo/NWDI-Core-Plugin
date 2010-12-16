/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import hudson.Extension;
import hudson.FilePath;
import hudson.Util;
import hudson.model.DependencyGraph;
import hudson.model.Descriptor;
import hudson.model.Descriptor.FormException;
import hudson.model.ItemGroup;
import hudson.model.TopLevelItem;
import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.tasks.Publisher;
import hudson.util.DescribableList;
import hudson.util.FormValidation;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.arachna.netweaver.dctool.JdkHomeAlias;
import org.arachna.netweaver.dctool.JdkHomePaths;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * A project for building tracks residing in a NWDI.
 * 
 * @author Dirk Weigenand
 */
public final class NWDIProject extends AbstractProject<NWDIProject, NWDIBuild> implements TopLevelItem {
    /**
     * Global descriptor/configuraton for NWDIProjects.
     */
    @Extension(ordinal = 1000)
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    /**
     * Create an instance of a NWDI project.
     * 
     * @param parent
     * @param name
     */
    public NWDIProject(final ItemGroup parent, final String name) {
        super(parent, name);

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
    public DescriptorImpl getDescriptor() {
        return DESCRIPTOR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void submit(final StaplerRequest req, final StaplerResponse rsp) throws IOException, ServletException,
        FormException {
        super.submit(req, rsp);
    }

    /**
     * Descriptor for NWDIProjects. Contains the global configuration commonly
     * used for different NWDI tracks.
     * 
     * @author Dirk Weigenand
     * 
     */
    public static final class DescriptorImpl extends AbstractProjectDescriptor {
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
         * path to the 'JDK_1_3_1_HOME' installation.
         */
        private String jdk131Home;

        /**
         * path to the 'JDK_1_4_2_HOME' installation.
         */
        private String jdk142Home;

        /**
         * @return the user
         */
        public String getUser() {
            return this.user;
        }

        /**
         * @param user
         *            the user to set
         */
        public void setUser(final String user) {
            this.user = user;
        }

        /**
         * @return the password
         */
        public String getPassword() {
            return this.password;
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
         * @return the jdk131Home
         */
        public String getJdk131Home() {
            return jdk131Home;
        }

        /**
         * @param jdk131Home
         *            the jdk131Home to set
         */
        public void setJdk131Home(final String jdk131Home) {
            this.jdk131Home = jdk131Home;
        }

        /**
         * @return the jdk142Home
         */
        public String getJdk142Home() {
            return jdk142Home;
        }

        /**
         * @param jdk142Home
         *            the jdk142Home to set
         */
        public void setJdk142Home(final String jdk142Home) {
            this.jdk142Home = jdk142Home;
        }

        /*
         * (non-Javadoc)
         * @see
         * hudson.model.Descriptor#configure(org.kohsuke.stapler.StaplerRequest,
         * net.sf.json.JSONObject)
         */
        @Override
        public boolean configure(final StaplerRequest req, final JSONObject json) throws FormException {
            this.jdk131Home = getParameter(req, "NWDIPlugin.jdk131Home");
            this.jdk142Home = getParameter(req, "NWDIPlugin.jdk142Home");
            this.nwdiToolLibFolder = getParameter(req, "NWDIPlugin.nwdiToolLibFolder");
            this.user = getParameter(req, "NWDIPlugin.user");
            this.password = getParameter(req, "NWDIPlugin.password");

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
                catch (IOException e) {
                    result = FormValidation.error(Messages.NWDIProject_NwdiToolLibFolder_ioexception());
                }
                catch (InterruptedException e) {
                    result = FormValidation.error("The form validation has been cancelled.");
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
            return folder != null && folder.child("dc").exists() ? FormValidation.ok() : FormValidation
                .error("The given folder does not point to a proper NWDI tool library installation!");
        }

        /**
         * Returns the requested parameter from the given {@link StaplerRequest}
         * .
         * 
         * @param req
         *            the <code>StaplerRequest</code> to extract the parameter
         *            from.
         * @param parameter
         *            the name of the requested parameter.
         * @return the requested parameter from the given
         *         <code>StaplerRequest</code>.
         */
        private String getParameter(final StaplerRequest req, final String parameter) {
            return Util.fixEmpty(req.getParameter(parameter).trim());
        }

        /**
         * Validate the 'user' parameter.
         * 
         * @param value
         *            the form value for the 'user' field.
         * @return the form validation value.
         */
        public FormValidation doUserCheck(@QueryParameter final String value) {
            return Util.fixEmptyAndTrim(value) == null ? FormValidation
                .error("Please provide the UME user to authenticate with.") : FormValidation.ok();
        }

        /**
         * Validate the 'user' parameter.
         * 
         * @param value
         *            the form value for the 'user' field.
         * @return the form validation value.
         */
        public FormValidation doPasswordCheck(@QueryParameter final String value) {
            return Util.fixEmptyAndTrim(value) == null ? FormValidation
                .error("Please provide the password for the UME user to authenticate with.") : FormValidation.ok();
        }

        /**
         * Returns the path mappings for the configured JDK homes.
         * 
         * @return path mappings for the configured JDK homes.
         */
        JdkHomePaths getConfiguredJdkHomePaths() {
            final JdkHomePaths paths = new JdkHomePaths();

            paths.add(JdkHomeAlias.Jdk131Home, this.getJdk131Home());
            paths.add(JdkHomeAlias.Jdk142Home, this.getJdk142Home());

            return paths;
        }

        @Override
        public String getDisplayName() {
            // FIXME: return localized display name
            return "NWDI Project";
        }

        @Override
        public NWDIProject newInstance(final String name) {
            return new NWDIProject(Hudson.getInstance(), name);
        }
    }

    @Override
    public DescribableList<Publisher, Descriptor<Publisher>> getPublishersList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isFingerprintConfigured() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected void buildDependencyGraph(final DependencyGraph graph) {
        // TODO Auto-generated method stub
    }
}
