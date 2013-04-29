/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.PublicPartReference;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link WebDynproProjectPropertiesReader}.
 * 
 * @author Dirk Weigenand
 */
public class WebDynproProjectPropertiesReaderTest {
    /**
     * Instance under test.
     */
    private WebDynproProjectPropertiesReader propertiesReader;

    /**
     * sample component for testing.
     */
    private DevelopmentComponent component;

    /**
     * set up fixture.
     */
    @Before
    public void setUp() {
        propertiesReader = new WebDynproProjectPropertiesReader();
        component = new DevelopmentComponent("", "");
        propertiesReader.execute(component, getProjectWDProperties());
    }

    @Test
    public void testReadWebDynproProjectProperties() {
        final Collection<PublicPartReference> publicParts = component.getUsedDevelopmentComponents();

        assertThat(publicParts, hasSize(5));
    }

    /**
     * Get example input from class path.
     * 
     * @return reader object with example input.
     */
    private Reader getProjectWDProperties() {
        return new InputStreamReader(this.getClass().getResourceAsStream(
            "/org/arachna/netweaver/hudson/nwdi/dcupdater/ProjectProperties.wdproperties"));
    }
}
