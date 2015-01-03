/**
 * 
 */
package org.arachna.netweaver.tools.cbs;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.StringReader;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link DCListReader}.
 * 
 * @author Dirk Weigenand
 */
public class DCListReaderTest {
    /**
     * development configuration to update when list of DCs is parsed.
     */
    private DevelopmentConfiguration config;

    /**
     * Registry for DCs.
     */
    private DevelopmentComponentFactory dcFactory;

    /**
     * instance under test.
     */
    private DCListReader reader;

    /**
     * test compartment.
     */
    private Compartment compartment;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() {
        config = new DevelopmentConfiguration("DI0_Example_D");
        dcFactory = new DevelopmentComponentFactory();
        reader = new DCListReader(config, dcFactory);
        compartment = Compartment.create("springsource.org", "SPRINGFRAMEWORK", CompartmentState.Archive, "");
        config.add(compartment);
        reader
            .execute(new StringReader(
                "1664    springsource.org_SPRINGFRAMEWORK_1              "
                    + "lib/spring                                        springsource.org         all\n"
                    + "1665    springsource.org_SPRINGFRAMEWORK_1              lib/jee/spring"
                    + "                                        springsource.org         all"));
    }

    /**
     * Test method for {@link org.arachna.netweaver.tools.cbs.DCListReader#execute(java.io.Reader)}.
     */
    @Test
    public final void testThatComponentsAreAddedToDevelopmentComponentFactory() {
        assertThat(dcFactory.get("springsource.org", "lib/spring"), notNullValue());
        assertThat(dcFactory.get("springsource.org", "lib/jee/spring"), notNullValue());
    }

    /**
     * Test method for {@link org.arachna.netweaver.tools.cbs.DCListReader#execute(java.io.Reader)}.
     */
    @Test
    public final void testThatComponentsAreAddedToCompartment() {
        assertThat(compartment.getDevelopmentComponents(), hasItem(dcFactory.get("springsource.org", "lib/spring")));
        assertThat(compartment.getDevelopmentComponents(), hasItem(dcFactory.get("springsource.org", "lib/jee/spring")));
    }
}
