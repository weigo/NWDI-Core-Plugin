/**
 * 
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.arachna.netweaver.dc.types.PublicPartReference;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link PublicPartReferenceFactory}.
 * 
 * @author Dirk Weigenand
 */
public class PublicPartReferenceFactoryTest {
    /**
     * example DC name.
     */
    private static final String DC1 = "DC1";

    /**
     * example vendor name.
     */
    private static final String EXAMPLE_COM = "example.com";

    /**
     * example black listed reference name.
     */
    private static final String TC_WD_WSLIB = "tc/wd/wslib";

    /**
     * instance under test.
     */
    private PublicPartReferenceFactory factory;

    /**
     * Set up fixture.
     */
    @Before
    public void setUp() {
        factory = new PublicPartReferenceFactory();
    }

    /**
     * Reset fixture.
     */
    @After
    public void tearDown() {
        factory = null;
    }

    /**
     * Test method for
     * {@link PublicPartReferenceFactory#create(java.lang.String)}.
     */
    @Test
    public void testCreateTcWdWslib() {
        final PublicPartReference reference = factory.create(TC_WD_WSLIB);
        assertThat(reference.getComponentName(), equalTo(TC_WD_WSLIB));
        assertThat(reference.getVendor(), equalTo("sap.com"));
    }

    /**
     * Test method for
     * {@link PublicPartReferenceFactory#create(java.lang.String)}.
     */
    @Test
    public void assertFactoryCreatesCorrectVendorAndDCNameFromReferenceWithSlash() {
        final PublicPartReference reference = factory.create("example.com/DC1");
        assertThat(reference.getComponentName(), equalTo(DC1));
        assertThat(reference.getVendor(), equalTo(EXAMPLE_COM));
    }

    /**
     * Test method for
     * {@link PublicPartReferenceFactory#create(java.lang.String)}.
     */
    @Test
    public void assertFactoryCreatesCorrectVendorAndDCNameFromReferenceWithTile() {
        final PublicPartReference reference = factory.create("example.com~DC1");
        assertThat(reference.getComponentName(), equalTo(DC1));
        assertThat(reference.getVendor(), equalTo(EXAMPLE_COM));
    }
}
