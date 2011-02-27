/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStreamReader;

import org.arachna.netweaver.dc.types.PublicPart;
import org.arachna.xml.XmlReaderHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * @author Dirk Weigenand
 * 
 */
public class PublicPartReaderTest {
    /**
     * PublicPart read from example file.
     */
    private PublicPart publicPart;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        final PublicPartReader reader = new PublicPartReader();
        new XmlReaderHelper(reader).parse(new InputStreamReader(this.getClass().getResourceAsStream(
            "/org/arachna/netweaver/hudson/nwdi/dcupdater/API.pp")));
        this.publicPart = reader.getPublicPart();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        this.publicPart = null;
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.dcupdater.PublicPartReader} .
     * 
     * @throws SAXException
     * @throws IOException
     */
    @Test
    public final void testPublicPartName() throws IOException, SAXException {
        assertNotNull(this.publicPart);
        assertThat(this.publicPart.getPublicPart(), equalTo("API"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.dcupdater.PublicPartReader} .
     * 
     * @throws SAXException
     * @throws IOException
     */
    @Test
    public final void testPublicPartCaption() throws IOException, SAXException {
        assertNotNull(this.publicPart);
        assertThat(this.publicPart.getCaption(), equalTo("caption"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.dcupdater.PublicPartReader} .
     * 
     * @throws SAXException
     * @throws IOException
     */
    @Test
    public final void testPublicPartDescription() throws IOException, SAXException {
        assertNotNull(this.publicPart);
        assertThat(this.publicPart.getDescription(), equalTo("description"));
    }
}
