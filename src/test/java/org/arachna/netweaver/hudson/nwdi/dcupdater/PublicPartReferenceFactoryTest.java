/**
 * 
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import org.arachna.netweaver.dc.types.PublicPartReference;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author Dirk Weigenand
 */
public class PublicPartReferenceFactoryTest {
    /**
     * instance under test.
     */
    private PublicPartReferenceFactory factory;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        this.factory = new PublicPartReferenceFactory();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        this.factory = null;
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.dcupdater.PublicPartReferenceFactory#create(java.lang.String)}
     * .
     */
    @Test
    public final void testCreateTcWdWslib() {
        PublicPartReference reference = this.factory.create("tc/wd/wslib");
        PublicPartReference expected = new PublicPartReference("sap.com", "tc/wd/wslib");
        assertThat(reference, equalTo(expected));
    }
}
