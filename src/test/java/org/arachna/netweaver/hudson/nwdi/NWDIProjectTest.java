/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import hudson.model.Descriptor.FormException;
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.model.TopLevelItem;
import hudson.model.TopLevelItemDescriptor;
import hudson.model.Hudson;
import hudson.model.Job;
import hudson.search.SearchIndex;
import hudson.search.Search;
import hudson.security.ACL;
import hudson.security.Permission;
import hudson.util.FormValidation;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import net.sf.json.JSONObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Unit tests for {@link NWDIProject}.
 * 
 * @author Dirk Weigenand
 */
public class NWDIProjectTest implements TopLevelItem {
    /**
     * Descriptor under test.
     */
    private NWDIProject.DescriptorImpl descriptor;

    /**
     * Initialize empty descriptor.
     */
    @Before
    public void setUp() {
        this.descriptor = new NWDIProject.DescriptorImpl() {
            @Override
            public void load() {
            }

            @Override
            public void save() {
            }
        };
    }

    /**
     * Reset descriptor.
     */
    @After
    public void tearDown() {
        this.descriptor = null;
    }

    /**
     * Test
     * {@link NWDIProject.DescriptorImpl#configure(StaplerRequest, JSONObject)}.
     * 
     * @throws FormException
     */
    @Test
    public void testDescriptorImplConfigureWithEmptyRequest() throws FormException {
        // final StaplerRequest request = new FakeStaplerRequest(new String[]
        // {}, new String[] {});
        JSONObject json = new JSONObject();
        json.accumulate("jdkHomePaths", "");
        json.accumulate("nwdiToolLibFolder", "");
        json.accumulate("nwdiToolLibFolder71", "");
        json.accumulate("password", "");
        json.accumulate("user", "");
        this.descriptor.configure(null, json);

        assertThat(this.descriptor.getJdkHomePaths(), equalTo(""));
        assertThat(this.descriptor.getNwdiToolLibFolder(), equalTo(""));
        assertThat(this.descriptor.getUser(), equalTo(""));
        assertThat(this.descriptor.getPassword(), equalTo(""));
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
        final String user = "user";
        final String password = "secret";

        JSONObject json = new JSONObject();
        json.accumulate("jdkHomePaths", jdk16);
        json.accumulate("nwdiToolLibFolder", nwdiToolsFolder);
        json.accumulate("nwdiToolLibFolder71", nwdiToolsFolder);
        json.accumulate("password", password);
        json.accumulate("user", user);
        this.descriptor.configure(null, json);

        assertThat(this.descriptor.getJdkHomePaths(), equalTo(jdk16));
        assertThat(this.descriptor.getNwdiToolLibFolder(), equalTo(nwdiToolsFolder));
        assertThat(this.descriptor.getUser(), equalTo(user));
        assertThat(this.descriptor.getPassword(), equalTo(password));
    }

    @Test
    public void testDoInvalidJdkHomePathsCheck() {
        final String invalidPathSpec = "xyz";
        final FormValidation validationResult = this.descriptor.doJdkHomePathsCheck(invalidPathSpec);
        assertThat(validationResult.kind, equalTo(FormValidation.Kind.ERROR));
    }

    @Test
    public void testDoEmptyJdkHomePathsCheck() {
        final String invalidPathSpec = "";
        final FormValidation validationResult = this.descriptor.doJdkHomePathsCheck(invalidPathSpec);
        assertThat(validationResult.kind, equalTo(FormValidation.Kind.ERROR));
    }

    /**
     * Empty implementation. Throws {@link UnsupportedOperationException}.
     * {@inheritDoc}
     */
    public Collection<? extends Job> getAllJobs() {
        throw new UnsupportedOperationException();
    }

    /**
     * Empty implementation. Throws {@link UnsupportedOperationException}.
     * {@inheritDoc}
     */
    public String getName() {
        throw new UnsupportedOperationException();
    }

    /**
     * Empty implementation. Throws {@link UnsupportedOperationException}.
     * {@inheritDoc}
     */
    public String getFullName() {
        throw new UnsupportedOperationException();
    }

    /**
     * Empty implementation. Throws {@link UnsupportedOperationException}.
     * {@inheritDoc}
     */
    public String getDisplayName() {
        throw new UnsupportedOperationException();
    }

    /**
     * Empty implementation. Throws {@link UnsupportedOperationException}.
     * {@inheritDoc}
     */
    public String getFullDisplayName() {
        throw new UnsupportedOperationException();
    }

    /**
     * Empty implementation. Throws {@link UnsupportedOperationException}.
     * {@inheritDoc}
     */
    public String getUrl() {
        throw new UnsupportedOperationException();
    }

    /**
     * Empty implementation. Throws {@link UnsupportedOperationException}.
     * {@inheritDoc}
     */
    public String getShortUrl() {
        throw new UnsupportedOperationException();
    }

    /**
     * Empty implementation. Throws {@link UnsupportedOperationException}.
     * {@inheritDoc}
     */
    public String getAbsoluteUrl() {
        throw new UnsupportedOperationException();
    }

    /**
     * Empty implementation. Throws {@link UnsupportedOperationException}.
     * {@inheritDoc}
     */
    public void onLoad(final ItemGroup<? extends Item> parent, final String name) throws IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * Empty implementation. Throws {@link UnsupportedOperationException}.
     * {@inheritDoc}
     */
    public void onCopiedFrom(final Item src) {
        throw new UnsupportedOperationException();
    }

    /**
     * Empty implementation. Throws {@link UnsupportedOperationException}.
     * {@inheritDoc}
     */
    public void onCreatedFromScratch() {
        throw new UnsupportedOperationException();
    }

    /**
     * Empty implementation. Throws {@link UnsupportedOperationException}.
     * {@inheritDoc}
     */
    public void save() throws IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * Empty implementation. Throws {@link UnsupportedOperationException}.
     * {@inheritDoc}
     */
    public void delete() throws IOException, InterruptedException {
        throw new UnsupportedOperationException();
    }

    /**
     * Empty implementation. Throws {@link UnsupportedOperationException}.
     * {@inheritDoc}
     */
    public File getRootDir() {
        throw new UnsupportedOperationException();
    }

    /**
     * Empty implementation. Throws {@link UnsupportedOperationException}.
     * {@inheritDoc}
     */
    public Search getSearch() {
        throw new UnsupportedOperationException();
    }

    /**
     * Empty implementation. Throws {@link UnsupportedOperationException}.
     * {@inheritDoc}
     */
    public String getSearchName() {
        throw new UnsupportedOperationException();
    }

    /**
     * Empty implementation. Throws {@link UnsupportedOperationException}.
     * {@inheritDoc}
     */
    public String getSearchUrl() {
        throw new UnsupportedOperationException();
    }

    /**
     * Empty implementation. Throws {@link UnsupportedOperationException}.
     * {@inheritDoc}
     */
    public SearchIndex getSearchIndex() {
        throw new UnsupportedOperationException();
    }

    /**
     * Empty implementation. Throws {@link UnsupportedOperationException}.
     * {@inheritDoc}
     */
    public ACL getACL() {
        throw new UnsupportedOperationException();
    }

    /**
     * Empty implementation. Throws {@link UnsupportedOperationException}.
     * {@inheritDoc}
     */
    public void checkPermission(final Permission permission) {
        throw new UnsupportedOperationException();
    }

    /**
     * Empty implementation. Throws {@link UnsupportedOperationException}.
     * {@inheritDoc}
     */
    public boolean hasPermission(final Permission permission) {
        throw new UnsupportedOperationException();
    }

    /**
     * Empty implementation. Throws {@link UnsupportedOperationException}.
     * {@inheritDoc}
     */
    public Hudson getParent() {
        throw new UnsupportedOperationException();
    }

    /**
     * Empty implementation. Throws {@link UnsupportedOperationException}.
     * {@inheritDoc}
     */
    public TopLevelItemDescriptor getDescriptor() {
        throw new UnsupportedOperationException();
    }
}
