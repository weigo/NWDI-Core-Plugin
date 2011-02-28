/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import java.util.Set;

import org.arachna.netweaver.dc.types.PublicPartReference;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Dirk Weigenand
 * 
 */
public class WebDynproProjectPropertiesReaderTest extends AbstractZipFileContentProvidingTest {
    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected String getResourceName() {
        return "ProjectProperties.zip";
    }

    @Test
    public void testReadWebDynproProjectProperties() {
        WebDynproProjectPropertiesReader reader = new WebDynproProjectPropertiesReader(this.getBasePath());
        final Set<PublicPartReference> publicParts = reader.read();

        assertThat(publicParts, hasSize(5));
    }
}
