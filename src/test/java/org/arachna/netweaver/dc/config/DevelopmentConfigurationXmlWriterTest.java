/**
 * 
 */
package org.arachna.netweaver.dc.config;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.stream.XMLStreamException;

import org.arachna.netweaver.dc.types.BuildVariant;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dc.types.PublicPart;
import org.arachna.netweaver.dc.types.PublicPartReference;
import org.arachna.netweaver.dc.types.PublicPartType;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * JUnit test case for {@link DevelopmentConfigurationXmlWriter}.
 * 
 * @author Dirk Weigenand
 */
public class DevelopmentConfigurationXmlWriterTest extends XMLTestCase {
    /**
     * xpath for selecting the first used compartment in archive state of the
     * first serialized compartment.
     */
    private static final String XPATH_USED_COMPARTMENT_IN_ARCHIVE_STATE =
        "/development-configuration/compartment[1]/used-compartments/used-compartment[1]/@archive-state";

    /**
     * expected xpath selection result.
     */
    private static final String NO = "no";

    /**
     * expected xpath selection result.
     */
    private static final String YES = "yes";

    /**
     * xpath expression for selecting the first compartment in archive state.
     */
    private static final String XPATH_COMPARTMENT_IN_ARCHIVE_STATE =
        "/development-configuration/compartment[1]/@archive-state";

    /**
     * vendor SAP.
     */
    private static final String SAP_COM = "sap.com";

    /**
     * example vendor.
     */
    private static final String EXAMPLE_ORG = "example.org";

    /**
     * example compartment.
     */
    private static final String EXAMPLE_ORG_SC_1 = "example.org_SC_1";

    /**
     * compartment SAP_BUILDT.
     */
    private static final String SAP_COM_SAP_BUILDT_1 = "sap.com_SAP_BUILDT_1";

    /**
     * a sample value...
     */
    private static final String DEFAULT = "default";

    /**
     * sample value for a description...
     */
    private static final String DESCRIPTION = "description";

    /**
     * Example configuration.
     */
    private DevelopmentConfiguration config;

    /**
     * Writer under test.
     */
    private DevelopmentConfigurationXmlWriter writer;

    /**
     * An example compartment.
     */
    private Compartment compartment;

    /**
     * an example used compartment.
     */
    private Compartment usedCompartment;

    /**
     * An example development component.
     */
    private DevelopmentComponent component;

    /**
     * An example reference to a public part (of an used DC).
     */
    private PublicPartReference publicPartReference;

    /**
     * An example public part.
     */
    private PublicPart publicPart;

    /**
     * {@inheritDoc}
     */
    @Override
    @Before
    protected void setUp() throws Exception {
        super.setUp();
        config = new DevelopmentConfiguration("DI0_Example_D");
        config.setCaption("caption");
        config.setDescription(DESCRIPTION);

        final BuildVariant variant = new BuildVariant(DEFAULT);
        variant.addBuildOption("key", "value");

        config.setBuildVariant(variant);

        usedCompartment =
            new Compartment(SAP_COM_SAP_BUILDT_1, CompartmentState.Archive, SAP_COM, SAP_COM_SAP_BUILDT_1, "SAP_BUILDT");
        compartment = new Compartment(EXAMPLE_ORG_SC_1, CompartmentState.Source, EXAMPLE_ORG, EXAMPLE_ORG_SC_1, "SC");
        compartment.add(usedCompartment);
        config.add(compartment);

        component = new DevelopmentComponent("example/dc", EXAMPLE_ORG);
        publicPartReference = new PublicPartReference(SAP_COM, "com.sap.exception", DEFAULT);
        component.add(publicPartReference);

        publicPart = new PublicPart("api", "API public Part", DESCRIPTION, PublicPartType.COMPILE);
        component.add(publicPart);
        compartment.add(component);
        writer = new DevelopmentConfigurationXmlWriter(config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @After
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     */
    @Test
    public final void testPersistedConfigurationName() {
        this.assertXpathEvaluatesTo(config.getName(), "/development-configuration/@name");
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     */
    @Test
    public final void testPersistedConfigurationCaption() {
        this.assertXpathEvaluatesTo(config.getCaption(), "/development-configuration/@caption");
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     */
    @Test
    public final void testPersistedConfigurationDescription() {
        this.assertXpathEvaluatesTo(config.getDescription(), "/development-configuration/@description");
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     */
    @Test
    public final void testPersistedConfigurationBuildVariant() {
        final String key = config.getBuildVariant().getBuildOptionNames().iterator().next();
        final String baseXpath = "/development-configuration/build-variant/option[1]/%s";
        this.assertXpathEvaluatesTo(key, String.format(baseXpath, "@name"));
        this.assertXpathEvaluatesTo(config.getBuildVariant().getBuildOption(key), String.format(baseXpath, "@value"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     */
    @Test
    public final void testPersistedConfigurationCompartmentName() {
        this.assertXpathEvaluatesTo(compartment.getName(), "/development-configuration/compartment[1]/@name");
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     */
    @Test
    public final void testPersistedConfigurationCompartmentInSourceState() {
        this.assertXpathEvaluatesTo(NO, XPATH_COMPARTMENT_IN_ARCHIVE_STATE);
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     */
    @Test
    public final void testPersistedConfigurationCompartmentInArchiveState() {
        compartment.setState(CompartmentState.Archive);
        this.assertXpathEvaluatesTo(YES, XPATH_COMPARTMENT_IN_ARCHIVE_STATE);
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     */
    @Test
    public final void testPersistedConfigurationCompartmentScName() {
        this.assertXpathEvaluatesTo(compartment.getSoftwareComponent(),
            "/development-configuration/compartment[1]/@sc-name");
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     */
    @Test
    public final void testPersistedConfigurationCompartmentVendor() {
        this.assertXpathEvaluatesTo(compartment.getVendor(), "/development-configuration/compartment[1]/@vendor");
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     */
    @Test
    public final void testPersistedConfigurationUsedCompartmentInArchiveState() {
        this.assertXpathEvaluatesTo(YES, XPATH_USED_COMPARTMENT_IN_ARCHIVE_STATE);
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     */
    @Test
    public final void testPersistedConfigurationUsedCompartmentInSourceState() {
        usedCompartment.setState(CompartmentState.Source);
        this.assertXpathEvaluatesTo(NO, XPATH_USED_COMPARTMENT_IN_ARCHIVE_STATE);
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     */
    @Test
    public final void testPersistedConfigurationUsedCompartmentScName() {
        this.assertXpathEvaluatesTo(usedCompartment.getSoftwareComponent(),
            "/development-configuration/compartment[1]/used-compartments/used-compartment[1]/@sc-name");
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     */
    @Test
    public final void testPersistedConfigurationUsedCompartmentVendor() {
        this.assertXpathEvaluatesTo(usedCompartment.getVendor(),
            "/development-configuration/compartment[1]/used-compartments/used-compartment[1]/@vendor");
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     */
    @Test
    public final void testPersistedConfigurationUsedCompartmentName() {
        this.assertXpathEvaluatesTo(usedCompartment.getName(),
            "/development-configuration/compartment[1]/used-compartments/used-compartment[1]/@name");
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     */
    @Test
    public final void testPersistedConfigurationDevelopmentComponentName() {
        this.assertXpathEvaluatesTo(component.getName(),
            "/development-configuration/compartment[1]/development-components/development-component[1]/@name");
    }

    /**
     * Helper method for asserting the value of xpath results.
     * 
     * @param expected
     *            expted result of xpath expression.
     * @param xPath
     *            xpath
     */
    private void assertXpathEvaluatesTo(final String expected, final String xPath) {
        try {
            this.assertXpathEvaluatesTo(expected, xPath, writeDevelopmentConfiguration());
        }
        catch (final XpathException e) {
            fail(e.getMessage());
        }
        catch (final SAXException e) {
            fail(e.getMessage());
        }
        catch (final IOException e) {
            fail(e.getMessage());
        }
        catch (final XMLStreamException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Write development configuration into a string.
     * 
     * @return result of XML serialization.
     * @throws XMLStreamException
     *             thrown when serialization to XML fails
     */
    protected String writeDevelopmentConfiguration() throws XMLStreamException {
        final StringWriter target = new StringWriter();
        writer.write(target);

        return target.toString();
    }

}
