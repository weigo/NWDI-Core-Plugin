/**
 * 
 */
package org.arachna.ant;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.PublicPart;
import org.arachna.netweaver.dc.types.PublicPartType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit tests for {@link AntHelper}.
 * 
 * @author Dirk Weigenand
 */
public class AntHelperTest {
    /**
     * public part name 'default'.
     */
    private static final String DEFAULT_PP = "default";

    /**
     * vendor 'example.com'.
     */
    private static final String EXAMPLE_COM = "example.com";

    /**
     * expected public part location for dc1.
     */
    private static final String DEFAULT_PP_LOCATION_DC1 = "/workspace/.dtc/DCs/example.com/dc1/_comp/gen/default/public/default/lib/java";

    /**
     * instance under test.
     */
    private AntHelper antHelper;

    /**
     * example development component.
     */
    private DevelopmentComponent dc1;

    /**
     * Set up fixture.
     */
    @Before
    public void setUp() {
        final DevelopmentComponentFactory dcFactory = new DevelopmentComponentFactory();
        dc1 = dcFactory.create(EXAMPLE_COM, "dc1");
        dc1.add(new PublicPart(DEFAULT_PP, "", "", PublicPartType.COMPILE));
        antHelper = new AntHelper("/workspace", dcFactory);
    }

    /**
     * Tear down fixture.
     */
    @After
    public void tearDown() {
        antHelper = null;
        dc1 = null;
    }

    /**
     * Test method for
     * {@link org.arachna.ant.AntHelper#getBaseLocation(org.arachna.netweaver.dc.types.DevelopmentComponent, java.lang.String)} .
     */
    @Test
    public final void testGetBaseLocationForDevelopmentComponentAndPublicPart() {
        assertThat(antHelper.getBaseLocation(dc1, DEFAULT_PP), equalTo(DEFAULT_PP_LOCATION_DC1));
    }

    /**
     * Test method for
     * {@link org.arachna.ant.AntHelper#getBaseLocation(org.arachna.netweaver.dc.types.DevelopmentComponent, java.lang.String)} .
     */
    @Test
    public final void testGetBaseLocationForDevelopmentComponentAndEmptyPublicPartReference() {
        assertThat(antHelper.getBaseLocation(dc1, ""), equalTo(DEFAULT_PP_LOCATION_DC1));
    }

    /**
     * Test method for
     * {@link org.arachna.ant.AntHelper#getBaseLocation(org.arachna.netweaver.dc.types.DevelopmentComponent, java.lang.String)} .
     */
    @Test
    public final void testGetBaseLocationForDevelopmentComponentAndNullPublicPartReference() {
        assertThat(antHelper.getBaseLocation(dc1, null), equalTo(DEFAULT_PP_LOCATION_DC1));
    }

    /**
     * Test method for
     * {@link org.arachna.ant.AntHelper#getBaseLocation(org.arachna.netweaver.dc.types.DevelopmentComponent, java.lang.String)} .
     */
    @Test
    public final void testGetBaseLocationForDevelopmentComponentWithoutPublicPartAndNullPublicPartReference() {
        assertThat(antHelper.getBaseLocation(new DevelopmentComponent("dc2", EXAMPLE_COM), null),
            equalTo("/workspace/.dtc/DCs/example.com/dc2/_comp/gen/default"));
    }
}
