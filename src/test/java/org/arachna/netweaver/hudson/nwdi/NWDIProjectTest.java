/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import hudson.model.Descriptor.FormException;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;
import org.jvnet.hudson.test.HudsonTestCase;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Unit tests for {@link NWDIProject}.
 * 
 * @author Dirk Weigenand
 */
public class NWDIProjectTest extends HudsonTestCase {
    /**
     * form property for NWDI user.
     */
    private static final String USER = "user";

    /**
     * form property for password.
     */
    private static final String PASSWORD = "password";

    /**
     * form property for path to NWDI tool library for NetWeaver 7.1+.
     */
    private static final String NWDITOOLLIB71 = "nwdiToolLibFolder71";

    /**
     * form property for path to NWDI tool library for NetWeaver 7.0.x.
     */
    private static final String NWDITOOLLIB = "nwdiToolLibFolder";

    /**
     * form property for the paths to the JDKs to be used.
     */
    private static final String JDK_HOME_PATHS = "jdkHomePaths";

    /**
     * A sample NWDI project, necessary for getting to the descriptor.
     */
    private NWDIProject project;

    /**
     * The descriptor under test.
     */
    private NWDIProject.DescriptorImpl descriptor;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        project = hudson.createProject(NWDIProject.class, "nwdi");
        descriptor = project.getDescriptor();
    }

    /**
     * Test
     * {@link NWDIProject.DescriptorImpl#configure(StaplerRequest, JSONObject)}.
     * 
     * @throws FormException
     */
    @Test
    public void testDescriptorImplConfigureWithEmptyRequest() throws FormException {
        final JSONObject json = new JSONObject();
        json.accumulate(JDK_HOME_PATHS, "");
        json.accumulate(NWDITOOLLIB, "");
        json.accumulate(NWDITOOLLIB71, "");
        json.accumulate(PASSWORD, "");
        json.accumulate(USER, "");
        descriptor.configure(null, json);

        assertThat(descriptor.getJdkHomePaths(), equalTo(""));
        assertThat(descriptor.getNwdiToolLibFolder(), equalTo(""));
        assertThat(descriptor.getUser(), equalTo(""));
        assertThat(descriptor.getPassword(), equalTo(""));
    }

    /**
     * Test
     * {@link NWDIProject.DescriptorImpl#configure(StaplerRequest, JSONObject)}.
     * 
     * @throws FormException
     */
    @Test
    public void testDescriptorImplConfigureWithParameters() throws FormException {
        final String jdk16 = "/opt/jdk1.6";
        final String nwdiToolsFolder = "/opt/nwdi/lib";
        final String user = USER;
        final String password = "secret";

        final JSONObject json = new JSONObject();
        json.accumulate(JDK_HOME_PATHS, jdk16);
        json.accumulate(NWDITOOLLIB, nwdiToolsFolder);
        json.accumulate(NWDITOOLLIB71, nwdiToolsFolder);
        json.accumulate(PASSWORD, password);
        json.accumulate(USER, user);
        descriptor.configure(null, json);

        assertThat(descriptor.getJdkHomePaths(), equalTo(jdk16));
        assertThat(descriptor.getNwdiToolLibFolder(), equalTo(nwdiToolsFolder));
        assertThat(descriptor.getUser(), equalTo(user));
        assertThat(descriptor.getPassword(), equalTo(password));
    }

    @Test
    public void testDoInvalidJdkHomePathsCheck() {
        final String invalidPathSpec = "xyz";
        final FormValidation validationResult = descriptor.doJdkHomePathsCheck(invalidPathSpec);
        assertThat(validationResult.kind, equalTo(FormValidation.Kind.ERROR));
    }

    @Test
    public void testDoEmptyJdkHomePathsCheck() {
        final String invalidPathSpec = "";
        final FormValidation validationResult = descriptor.doJdkHomePathsCheck(invalidPathSpec);
        assertThat(validationResult.kind, equalTo(FormValidation.Kind.ERROR));
    }
}
