/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.confdef;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.tools.dc.commands.DevelopmentComponentsReader70;
import org.arachna.xml.XmlReaderHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Dirk Weigenand
 */
public class ConfDefReaderAndListCompartmentsTest {
    /**
     * expected development components.
     */
    private static final Set<String> EXPECTED_DEVELOPMENT_COMPONENTS = new HashSet<String>();

    /**
     * Development configuration to use for tests.
     */
    private DevelopmentConfiguration developmentConfiguration;

    /**
     * registry for development components.
     */
    private DevelopmentComponentFactory dcFactory;

    @BeforeClass
    public static void setUpBeforeClass() {
        EXPECTED_DEVELOPMENT_COMPONENTS.add("example.com:lib/service/mail");
        EXPECTED_DEVELOPMENT_COMPONENTS.add("apache.org:jee/commons");
        EXPECTED_DEVELOPMENT_COMPONENTS.add("apache.org:jee/log4j");
        EXPECTED_DEVELOPMENT_COMPONENTS.add("itext.org:itext1.2");
        EXPECTED_DEVELOPMENT_COMPONENTS.add("apache.org:log4j");
        EXPECTED_DEVELOPMENT_COMPONENTS.add("example.com:lib/junit");
        EXPECTED_DEVELOPMENT_COMPONENTS.add("example.com:lib/jee/service/mail");
        EXPECTED_DEVELOPMENT_COMPONENTS.add("itext.org:jee/itext2");
        EXPECTED_DEVELOPMENT_COMPONENTS.add("apache.org:commons");
        EXPECTED_DEVELOPMENT_COMPONENTS.add("example.com:lib/jdbc");
        EXPECTED_DEVELOPMENT_COMPONENTS.add("example.com:lib/spring");
        EXPECTED_DEVELOPMENT_COMPONENTS.add("apache.org:struts1.2");
        EXPECTED_DEVELOPMENT_COMPONENTS.add("example.com:lib/jee/spring");
        EXPECTED_DEVELOPMENT_COMPONENTS.add("apache.org:lib/struts1.2");
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        this.dcFactory = new DevelopmentComponentFactory();
        final ConfDefReader reader = new ConfDefReader();
        new XmlReaderHelper(reader)
            .parse(new InputStreamReader(this.getClass().getResourceAsStream("Example.confdef")));

        this.developmentConfiguration = reader.getDevelopmentConfiguration();

        final Reader dcToolOutputReader =
            new InputStreamReader(this.getClass().getResourceAsStream("ListDevelopmentComponentsExample.out"));
        final DevelopmentComponentsReader70 developmentComponentsReader =
            new DevelopmentComponentsReader70(dcToolOutputReader, this.dcFactory, this.developmentConfiguration);
        developmentComponentsReader.read();
    }

    /**
     *
     */
    @After
    public void tearDown() {
        this.dcFactory = null;
        this.developmentConfiguration = null;
    }

    @Test
    public void testConfDefReadingAndCompartmentListing() throws Exception {
        final Compartment compartment = this.developmentConfiguration.getCompartment("example.com_EXAMPLE_SC1_1");

        for (final DevelopmentComponent component : compartment.getDevelopmentComponents()) {
            assertThat(EXPECTED_DEVELOPMENT_COMPONENTS, hasItem(component.getVendor() + ":" + component.getName()));
        }
    }

    @Test
    public void testConfDefReadingAndCompartmentHasDtrServerUrl() throws Exception {
        final Compartment compartment = this.developmentConfiguration.getCompartment("example.com_EXAMPLE_SC1_1");
        assertThat(compartment.getDtrUrl(), is(equalTo("http://di0db:53000/dtr")));
    }
}
