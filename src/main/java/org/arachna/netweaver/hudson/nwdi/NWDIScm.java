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
import hudson.model.Run;
import hudson.scm.ChangeLogParser;
import hudson.scm.PollingResult;
import hudson.scm.SCMDescriptor;
import hudson.scm.SCMRevisionState;
import hudson.scm.SCM;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.sf.json.JSONObject;

import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.hudson.dtr.browser.Activity;
import org.arachna.netweaver.hudson.dtr.browser.DtrBrowser;
import org.arachna.netweaver.hudson.nwdi.confdef.ConfDefReader;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLReaderFactory;

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
     * Content of .confdef file (development configuration of track to be monitored).
     */
    private final String confdef;

    /**
     * UME user of NWDI server.
     */
    private String user;

    /**
     * authenticate user using this password.
     */
    private String password;

    /**
     * Get a clean copy of all development components from NWDI.
     */
    private final boolean cleanCopy;

    /**
     * The development configuration the SCM is to be run on.
     */
    private transient DevelopmentConfiguration developmentConfiguration;

    /**
     * Create an instance of a <code>NWDIScm</code>.
     * 
     * @param confdef
     *            the uploaded <code>.confdef</code> development configuration file to use.
     * @param cleanCopy
     *            indicate whether only changed development components should be loaded from the NWDI or all that are contained in the
     *            indicated CBS workspace
     */
    @DataBoundConstructor
    public NWDIScm(final String confdef, final boolean cleanCopy) {
        super();
        this.confdef = confdef;
        this.cleanCopy = cleanCopy;
        this.getDevelopmentConfiguration();
    }

    /**
     * Set the user to use for authentication.
     * 
     * @param user
     *            the user to use for authentication against the UME.
     */
    void setUser(final String user) {
        this.user = user;
    }

    /**
     * Set the password to use for authentication.
     * 
     * @param password
     *            the password to use for authentication against the UME.
     */
    void setPassword(final String password) {
        this.password = password;
    }

    /**
     * @return the confdef
     */
    public String getConfdef() {
        return confdef;
    }

    /*
     * (non-Javadoc)
     * @see hudson.scm.SCM#getDescriptor()
     */
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    /*
     * (non-Javadoc)
     * @see hudson.scm.SCM#createChangeLogParser()
     */
    @Override
    public ChangeLogParser createChangeLogParser() {
        return new DtrChangeLogParser();
    }

    /*
     * (non-Javadoc)
     * @see hudson.scm.SCM#requiresWorkspaceForPolling()
     */
    @Override
    public boolean requiresWorkspaceForPolling() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see hudson.scm.SCM#checkout(hudson.model.AbstractBuild, hudson.Launcher, hudson.FilePath, hudson.model.BuildListener, java.io.File)
     */
    @Override
    public boolean checkout(final AbstractBuild build, final Launcher launcher, final FilePath workspace, final BuildListener listener,
            final File changelogFile) throws IOException, InterruptedException {

        final DtrBrowser browser = new DtrBrowser(this.developmentConfiguration, null, this.user, this.password);
        // FIXME: get the list of activities from the NWDIRevisionState object calculated by calcRevisionsFromBuild
        final List<Activity> activities = this.getActivities(build.getPreviousBuild());

        // update activities with their respective resources
        // FIXME: add methods to DtrBrowser that get activities with their
        // respective resources!
        browser.getDevelopmentComponents(activities);

        final DtrChangeLogWriter dtrChangeLogWriter =
                new DtrChangeLogWriter(new DtrChangeLogSet(build, activities), new FileWriter(changelogFile));
        dtrChangeLogWriter.write();

        return !activities.isEmpty();
    }

    /*
     * (non-Javadoc)
     * @see hudson.scm.SCM#pollChanges(hudson.model.AbstractProject, hudson.Launcher, hudson.FilePath, hudson.model.TaskListener)
     */
    @Override
    public boolean pollChanges(final AbstractProject project, final Launcher launcher, final FilePath workspace, final TaskListener listener)
            throws IOException, InterruptedException {
        return !this.getActivities(project.getLastBuild()).isEmpty();
    }

    /**
     * @return the cleanCopy
     */
    public boolean isCleanCopy() {
        return cleanCopy;
    }

    @Extension
    public static class DescriptorImpl extends SCMDescriptor<NWDIScm> {
        /**
         * UME user to use when connecting to NWDI.
         */
        private String user;

        /**
         * password to use when authenticating.
         */
        private String password;

        public DescriptorImpl() {
            super(NWDIScm.class, null);
            load();
        }

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

        @Override
        public String getDisplayName() {
            return "NetWeaver Development Infrastructure";
        }

        /*
         * (non-Javadoc)
         * @see hudson.model.Descriptor#configure(org.kohsuke.stapler.StaplerRequest, net.sf.json.JSONObject)
         */
        @Override
        public boolean configure(final StaplerRequest req, final JSONObject json) throws FormException {
            save();

            return super.configure(req, json);
        }

        @Override
        public SCM newInstance(final StaplerRequest req, final JSONObject formData) throws hudson.model.Descriptor.FormException {
            NWDIScm scm = (NWDIScm)super.newInstance(req, formData);
            scm.setPassword(this.password);
            scm.setUser(this.user);

            return scm;
        }
    }

    /**
     * Get list of activities since last run. If <code>lastRun</code> is <code>null</code> all activities will be calculated.
     * 
     * @param lastRun
     *            last run of a build or <code>null</code> if this run is the first.
     * @return a list of {@link Activity} objects that were checked in since the last run or all activities.
     */
    private List<Activity> getActivities(final Run lastRun) {
        final DtrBrowser browser =
                new DtrBrowser(this.developmentConfiguration, new DevelopmentComponentFactory(), this.user, this.password);
        final List<Activity> activities = new ArrayList<Activity>();

        if (lastRun == null) {
            activities.addAll(browser.getActivities());
        }
        else {
            activities.addAll(browser.getActivities(lastRun.getTime()));
        }

        return activities;
    }

    @Override
    public SCMRevisionState calcRevisionsFromBuild(AbstractBuild<?, ?> build, Launcher launcher, TaskListener listener) throws IOException,
            InterruptedException {
        return new NWDIRevisionState(this.getActivities(build));
    }

    /**
     */
    private void getDevelopmentConfiguration() {
        ConfDefReader confdefReader = null;
        try {
            confdefReader = new ConfDefReader(XMLReaderFactory.createXMLReader());
            this.developmentConfiguration = confdefReader.read(new StringReader(this.confdef));
        }
        catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected PollingResult compareRemoteRevisionWith(AbstractProject<?, ?> project, Launcher launcher, FilePath path,
            TaskListener listener, SCMRevisionState revisionState) throws IOException, InterruptedException {
        // TODO Auto-generated method stub
        return null;
    }
}
