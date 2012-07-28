/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.tools.dc.commands.DevelopmentComponentsReader71;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author G526521
 *
 */
public class DevelopmentComponentsReader71Test {
    /**
     * {@link DevelopmentComponentsReader71} under test.
     */
    private DevelopmentComponentsReader71 dcReader;

    private DevelopmentComponentFactory dcFactory;

    private DevelopmentConfiguration config;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        Reader testData =
            new InputStreamReader(this.getClass().getResourceAsStream(
                "/org/arachna/netweaver/hudson/nwdi/listDCsV71.txt"));
        this.dcFactory = new DevelopmentComponentFactory();
        config = new DevelopmentConfiguration("DI0_Example_D");
        Compartment compartment = new Compartment("example.com_LIBJEE_1", CompartmentState.Source, "example.com", "", "LIBJEE");
        config.add(compartment);
        dcReader = new DevelopmentComponentsReader71(testData, dcFactory, config);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        this.dcFactory = null;
        this.dcReader = null;
        this.config = null;
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.tools.dc.commands.DevelopmentComponentsReader71#read()}
     * .
     *
     * @throws IOException
     */
    @Test
    public final void testRead() throws IOException {
        this.dcReader.read();

        assertEquals(4, this.dcFactory.getAll().size());
        assertNotNull(this.dcFactory.get("itext.org", "itext1.2"));
        assertNotNull(this.dcFactory.get("jfree.org", "jfreechart"));
        assertNotNull(this.dcFactory.get("apache.org", "jee/commons"));
        assertNotNull(this.dcFactory.get("itext.org", "jee/itext2"));
    }

}
