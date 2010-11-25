/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link SoftwareComponentsParser}.
 * 
 * @author Dirk Weigenand
 */
public class SoftwareComponentsParserTest {
    /**
     * expected compartment names.
     */
    private static final String[] COMPARTMENT_NAMES = new String[] { "EXAMPLE_SC1", "EXAMPLE_SC2", "EXAMPLE_SC3" };

    /**
     * Mapping for compartments read from example output of compartment list of
     * a track.
     */
    private final Map<String, Compartment> compartments = new HashMap<String, Compartment>();

    /**
     */
    @Before
    public void setUp() {
        this.compartments.clear();
        final InputStream input = this.getClass().getResourceAsStream("ExampleTrack.htm");
        final SoftwareComponentsParser browser = new SoftwareComponentsParser();
        final DevelopmentConfiguration config = new DevelopmentConfiguration("Example");
        config.add(new Compartment("EXAMPLE_SC1", CompartmentState.Source, "example.com", "", "EXAMPLE_SC1_1"));
        config.add(new Compartment("EXAMPLE_SC2", CompartmentState.Source, "example.com", "", "EXAMPLE_SC2_1"));
        config.add(new Compartment("EXAMPLE_SC3", CompartmentState.Source, "example.com", "", "EXAMPLE_SC3_1"));

        for (final Compartment compartment : browser.parse(input, config)) {
            this.compartments.put(compartment.getName(), compartment);
        }
    }

    /**
     * Validate that compartments are read correctly from the listing in the
     * DTR.
     */
    @Test
    public void testGetSoftwareComponentsForDevelopmentConfiguration() {
        Compartment compartment;

        for (final String compartmentName : COMPARTMENT_NAMES) {
            compartment = compartments.get(compartmentName);

            assertNotNull(compartment);
            assertEquals("example.com", compartment.getVendor());
            assertEquals(compartmentName, compartment.getName());
        }
    }
}
