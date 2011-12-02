/**
 * 
 */
package org.arachna.netweaver.dc.config;

import java.io.IOException;
import java.io.StringWriter;

import org.arachna.netweaver.dc.types.BuildVariant;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dc.types.PublicPart;
import org.arachna.netweaver.dc.types.PublicPartReference;
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
     * 
     */
    private Compartment usedCompartment;

    /**
     * development component used in writing the development configuration.
     */
    private DevelopmentComponent component;

    /**
     * {@inheritDoc}
     */
    @Override
    @Before
    protected void setUp() throws Exception {
        super.setUp();
        config = new DevelopmentConfiguration("DI0_Example_D");
        config.setCaption("caption");
        config.setDescription("description");

        final BuildVariant variant = new BuildVariant("default");
        variant.addBuildOption("key", "value");

        config.setBuildVariant(variant);

        usedCompartment =
            new Compartment("sap.com_SAP_BUILDT_1", CompartmentState.Archive, "sap.com", "sap.com_SAP_BUILDT_1",
                "SAP_BUILDT");
        compartment =
            new Compartment("example.org_SC_1", CompartmentState.Source, "example.org", "example.org_SC_1", "SC");
        compartment.add(usedCompartment);
        config.add(compartment);

        final DevelopmentComponent component = new DevelopmentComponent("example/dc", "example.org");
        final PublicPartReference publicPartReference =
            new PublicPartReference("sap.com", "com.sap.exception", "default");
        component.add(publicPartReference);

        final PublicPart publicPart = new PublicPart("api", "API public Part", "description");
        component.add(publicPart);
        compartment.add(component);
        writer = new DevelopmentConfigurationXmlWriter(config, System.err);
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
        this.assertXpathEvaluatesTo("no", "/development-configuration/compartment[1]/@archive-state");
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     */
    @Test
    public final void testPersistedConfigurationCompartmentInArchiveState() {
        compartment.setState(CompartmentState.Archive);
        this.assertXpathEvaluatesTo("yes", "/development-configuration/compartment[1]/@archive-state");
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
        this.assertXpathEvaluatesTo("yes",
            "/development-configuration/compartment[1]/used-compartments/used-compartment[1]/@archive-state");
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     */
    @Test
    public final void testPersistedConfigurationUsedCompartmentInSourceState() {
        usedCompartment.setState(CompartmentState.Source);
        this.assertXpathEvaluatesTo("no",
            "/development-configuration/compartment[1]/used-compartments/used-compartment[1]/@archive-state");
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     * 
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

    private void assertXpathEvaluatesTo(final String expected, final String xPath) {
        try {
            this.assertXpathEvaluatesTo(expected, xPath, writeDevelopmentConfiguration());
        }
        catch (final XpathException e) {
            fail(e.getLocalizedMessage());
        }
        catch (final SAXException e) {
            fail(e.getLocalizedMessage());
        }
        catch (final IOException e) {
            fail(e.getLocalizedMessage());
        }
    }

    /**
     * @return
     * @throws IOException
     */
    protected String writeDevelopmentConfiguration() throws IOException {
        final StringWriter target = new StringWriter();
        writer.write(target);

        return target.toString();
    }

}
