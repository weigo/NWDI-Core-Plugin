/**
 * 
 */
package org.arachna.netweaver.dctool.commands;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test for reading/parsing output of dctool of NW 7.1+ for listdcs command.
 * 
 * @author Dirk Weigenand (G526521)
 */
public class DevelopmentComponentsReader71Test {
    /**
     * 
     */
    private static final String TC_KM_FRWK = "tc/km/frwk";

    /**
     * 
     */
    private static final String SAP_COM = "sap.com";

    /**
     * Registry for development components.
     */
    private DevelopmentComponentFactory dcFactory;

    /**
     * sample development configuration for testing purposes.
     */
    private DevelopmentConfiguration developmentConfiguration;

    /**
     * @throws IOException
     */
    @Before
    public void setUp() throws IOException {
        dcFactory = new DevelopmentComponentFactory();
        InputStream listDCsOutput =
            this.getClass().getResourceAsStream("/org/arachna/netweaver/dctool/commands/ListDCsCommandOutput71+.txt");
        this.developmentConfiguration = new DevelopmentConfiguration("DI1_WS_D");
        this.developmentConfiguration.add(new Compartment("sap.com_KMC-CM_1", CompartmentState.Archive, SAP_COM, "", "KMC-CM"));
        DevelopmentComponentsReader71 reader = new DevelopmentComponentsReader71(new InputStreamReader(listDCsOutput), dcFactory,
            developmentConfiguration);
        reader.read();
    }

    /**
     */
    @After
    public void tearDown() {
        dcFactory = null;
        developmentConfiguration = null;
    }

    /**
     * Test method for {@link org.arachna.netweaver.dctool.commands.DevelopmentComponentsReader71#read()}.
     */
    @Test
    public void testRead() {
        DevelopmentComponent component = this.dcFactory.get(SAP_COM, TC_KM_FRWK);
        assertThat(component.getName(), equalTo(TC_KM_FRWK));
        assertThat(component.getVendor(), equalTo(SAP_COM));
    }
}
