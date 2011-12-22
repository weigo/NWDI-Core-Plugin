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
     * instance under test.
     */
    private AntHelper antHelper;

    /**
     * registry for development components.
     */
    private DevelopmentComponentFactory dcFactory;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        dcFactory = new DevelopmentComponentFactory();
        DevelopmentComponent component = dcFactory.create("example.com", "dc1");
        component.add(new PublicPart("default", "", "", PublicPartType.COMPILE));
        dcFactory.create("example.com", "dc2");
        this.antHelper = new AntHelper("/workspace", dcFactory);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        this.dcFactory = null;
        this.antHelper = null;
    }

    /**
     * Test method for
     * {@link org.arachna.ant.AntHelper#getBaseLocation(org.arachna.netweaver.dc.types.DevelopmentComponent, java.lang.String)}
     * .
     */
    @Test
    public final void testGetBaseLocationForDevelopmentComponentAndPublicPart() {
        assertThat(this.antHelper.getBaseLocation(dcFactory.get("example.com", "dc1"), "default"),
            equalTo("/workspace/.dtc/DCs/example.com/dc1/_comp/gen/default/public/default/lib/java"));
    }

    /**
     * Test method for
     * {@link org.arachna.ant.AntHelper#getBaseLocation(org.arachna.netweaver.dc.types.DevelopmentComponent, java.lang.String)}
     * .
     */
    @Test
    public final void testGetBaseLocationForDevelopmentComponentAndEmptyPublicPartReference() {
        assertThat(this.antHelper.getBaseLocation(dcFactory.get("example.com", "dc1"), ""),
            equalTo("/workspace/.dtc/DCs/example.com/dc1/_comp/gen/default/public/default/lib/java"));
    }

    /**
     * Test method for
     * {@link org.arachna.ant.AntHelper#getBaseLocation(org.arachna.netweaver.dc.types.DevelopmentComponent, java.lang.String)}
     * .
     */
    @Test
    public final void testGetBaseLocationForDevelopmentComponentAndNullPublicPartReference() {
        assertThat(this.antHelper.getBaseLocation(dcFactory.get("example.com", "dc1"), null),
            equalTo("/workspace/.dtc/DCs/example.com/dc1/_comp/gen/default/public/default/lib/java"));
    }

    /**
     * Test method for
     * {@link org.arachna.ant.AntHelper#getBaseLocation(org.arachna.netweaver.dc.types.DevelopmentComponent, java.lang.String)}
     * .
     */
    @Test
    public final void testGetBaseLocationForDevelopmentComponentWithoutPublicPartAndNullPublicPartReference() {
        assertThat(this.antHelper.getBaseLocation(dcFactory.get("example.com", "dc2"), null),
            equalTo("/workspace/.dtc/DCs/example.com/dc2/_comp/gen/default"));
    }
}
