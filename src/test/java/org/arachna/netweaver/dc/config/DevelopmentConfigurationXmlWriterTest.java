/**
 * 
 */
package org.arachna.netweaver.dc.config;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
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

    private DevelopmentComponent component;

    private PublicPartReference publicPartReference;

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

        component = new DevelopmentComponent("example/dc", "example.org");
        publicPartReference = new PublicPartReference("sap.com", "com.sap.exception", "default");
        component.add(publicPartReference);

        publicPart = new PublicPart("api", "API public Part", "description");
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
     * 
     * @throws IOException
     * @throws ResourceNotFoundException
     * @throws MethodInvocationException
     * @throws ParseErrorException
     * @throws SAXException
     * @throws XpathException
     */
    @Test
    public final void testPersistedConfigurationName() throws ParseErrorException, MethodInvocationException,
        ResourceNotFoundException, IOException, XpathException, SAXException {
        final String target = writeDevelopmentConfiguration();
        this.assertXpathEvaluatesTo(config.getName(), "/development-configuration/@name", target.toString());
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     * 
     * @throws IOException
     * @throws ResourceNotFoundException
     * @throws MethodInvocationException
     * @throws ParseErrorException
     * @throws SAXException
     * @throws XpathException
     */
    @Test
    public final void testPersistedConfigurationCaption() throws ParseErrorException, MethodInvocationException,
        ResourceNotFoundException, IOException, XpathException, SAXException {
        this.assertXpathEvaluatesTo(config.getCaption(), "/development-configuration/@caption",
            writeDevelopmentConfiguration());
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     * 
     * @throws IOException
     * @throws ResourceNotFoundException
     * @throws MethodInvocationException
     * @throws ParseErrorException
     * @throws SAXException
     * @throws XpathException
     */
    @Test
    public final void testPersistedConfigurationDescription() throws ParseErrorException, MethodInvocationException,
        ResourceNotFoundException, IOException, XpathException, SAXException {
        this.assertXpathEvaluatesTo(config.getDescription(), "/development-configuration/@description",
            writeDevelopmentConfiguration());
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     * 
     * @throws IOException
     * @throws ResourceNotFoundException
     * @throws MethodInvocationException
     * @throws ParseErrorException
     * @throws SAXException
     * @throws XpathException
     */
    @Test
    public final void testPersistedConfigurationBuildVariant() throws ParseErrorException, MethodInvocationException,
        ResourceNotFoundException, IOException, XpathException, SAXException {
        final String target = writeDevelopmentConfiguration();
        final String key = config.getBuildVariant().getBuildOptionNames().iterator().next();
        final String content = target.toString();
        final String baseXpath = "/development-configuration/build-variant/option[1]/%s";
        this.assertXpathEvaluatesTo(key, String.format(baseXpath, "@name"), content);
        this.assertXpathEvaluatesTo(config.getBuildVariant().getBuildOption(key), String.format(baseXpath, "@value"),
            content);
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     * 
     * @throws IOException
     * @throws ResourceNotFoundException
     * @throws MethodInvocationException
     * @throws ParseErrorException
     * @throws SAXException
     * @throws XpathException
     */
    @Test
    public final void testPersistedConfigurationCompartmentName() throws ParseErrorException,
        MethodInvocationException, ResourceNotFoundException, IOException, XpathException, SAXException {
        this.assertXpathEvaluatesTo(compartment.getName(), "/development-configuration/compartment[1]/@name",
            writeDevelopmentConfiguration());
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     * 
     * @throws IOException
     * @throws ResourceNotFoundException
     * @throws MethodInvocationException
     * @throws ParseErrorException
     * @throws SAXException
     * @throws XpathException
     */
    @Test
    public final void testPersistedConfigurationCompartmentInSourceState() throws ParseErrorException,
        MethodInvocationException, ResourceNotFoundException, IOException, XpathException, SAXException {
        this.assertXpathEvaluatesTo("no", "/development-configuration/compartment[1]/@archive-state",
            writeDevelopmentConfiguration());
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     * 
     * @throws IOException
     * @throws ResourceNotFoundException
     * @throws MethodInvocationException
     * @throws ParseErrorException
     * @throws SAXException
     * @throws XpathException
     */
    @Test
    public final void testPersistedConfigurationCompartmentInArchiveState() throws ParseErrorException,
        MethodInvocationException, ResourceNotFoundException, IOException, XpathException, SAXException {
        compartment.setState(CompartmentState.Archive);
        this.assertXpathEvaluatesTo("yes", "/development-configuration/compartment[1]/@archive-state",
            writeDevelopmentConfiguration());
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     * 
     * @throws IOException
     * @throws ResourceNotFoundException
     * @throws MethodInvocationException
     * @throws ParseErrorException
     * @throws SAXException
     * @throws XpathException
     */
    @Test
    public final void testPersistedConfigurationCompartmentScName() throws ParseErrorException,
        MethodInvocationException, ResourceNotFoundException, IOException, XpathException, SAXException {
        this.assertXpathEvaluatesTo(compartment.getSoftwareComponent(),
            "/development-configuration/compartment[1]/@sc-name", writeDevelopmentConfiguration());
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     * 
     * @throws IOException
     * @throws ResourceNotFoundException
     * @throws MethodInvocationException
     * @throws ParseErrorException
     * @throws SAXException
     * @throws XpathException
     */
    @Test
    public final void testPersistedConfigurationCompartmentVendor() throws ParseErrorException,
        MethodInvocationException, ResourceNotFoundException, IOException, XpathException, SAXException {
        this.assertXpathEvaluatesTo(compartment.getVendor(), "/development-configuration/compartment[1]/@vendor",
            writeDevelopmentConfiguration());
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     * 
     * @throws IOException
     * @throws ResourceNotFoundException
     * @throws MethodInvocationException
     * @throws ParseErrorException
     * @throws SAXException
     * @throws XpathException
     */
    @Test
    public final void testPersistedConfigurationUsedCompartmentInArchiveState() throws ParseErrorException,
        MethodInvocationException, ResourceNotFoundException, IOException, XpathException, SAXException {
        this.assertXpathEvaluatesTo("yes",
            "/development-configuration/compartment[1]/used-compartments/used-compartment[1]/@archive-state",
            writeDevelopmentConfiguration());
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     * 
     * @throws IOException
     * @throws ResourceNotFoundException
     * @throws MethodInvocationException
     * @throws ParseErrorException
     * @throws SAXException
     * @throws XpathException
     */
    @Test
    public final void testPersistedConfigurationUsedCompartmentInSourceState() throws ParseErrorException,
        MethodInvocationException, ResourceNotFoundException, IOException, XpathException, SAXException {
        usedCompartment.setState(CompartmentState.Source);
        this.assertXpathEvaluatesTo("no",
            "/development-configuration/compartment[1]/used-compartments/used-compartment[1]/@archive-state",
            writeDevelopmentConfiguration());
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     * 
     * @throws IOException
     * @throws ResourceNotFoundException
     * @throws MethodInvocationException
     * @throws ParseErrorException
     * @throws SAXException
     * @throws XpathException
     */
    @Test
    public final void testPersistedConfigurationUsedCompartmentScName() throws ParseErrorException,
        MethodInvocationException, ResourceNotFoundException, IOException, XpathException, SAXException {
        this.assertXpathEvaluatesTo(usedCompartment.getSoftwareComponent(),
            "/development-configuration/compartment[1]/used-compartments/used-compartment[1]/@sc-name",
            writeDevelopmentConfiguration());
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     * 
     * @throws IOException
     * @throws ResourceNotFoundException
     * @throws MethodInvocationException
     * @throws ParseErrorException
     * @throws SAXException
     * @throws XpathException
     */
    @Test
    public final void testPersistedConfigurationUsedCompartmentVendor() throws ParseErrorException,
        MethodInvocationException, ResourceNotFoundException, IOException, XpathException, SAXException {
        this.assertXpathEvaluatesTo(usedCompartment.getVendor(),
            "/development-configuration/compartment[1]/used-compartments/used-compartment[1]/@vendor",
            writeDevelopmentConfiguration());
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     * 
     * @throws IOException
     * @throws ResourceNotFoundException
     * @throws MethodInvocationException
     * @throws ParseErrorException
     * @throws SAXException
     * @throws XpathException
     */
    @Test
    public final void testPersistedConfigurationUsedCompartmentName() throws ParseErrorException,
        MethodInvocationException, ResourceNotFoundException, IOException, XpathException, SAXException {
        this.assertXpathEvaluatesTo(usedCompartment.getName(),
            "/development-configuration/compartment[1]/used-compartments/used-compartment[1]/@name",
            writeDevelopmentConfiguration());
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.config.DevelopmentConfigurationXmlWriter#write(java.io.Writer)}
     * .
     * 
     * @throws IOException
     * @throws ResourceNotFoundException
     * @throws MethodInvocationException
     * @throws ParseErrorException
     * @throws SAXException
     * @throws XpathException
     */
    @Test
    public final void testPersistedConfigurationDevelopmentComponentName() throws ParseErrorException,
        MethodInvocationException, ResourceNotFoundException, IOException, XpathException, SAXException {
        System.err.println(writeDevelopmentConfiguration());
        this.assertXpathEvaluatesTo(component.getName(),
            "/development-configuration/compartment[1]/development-components/development-component[1]/@name",
            writeDevelopmentConfiguration());
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
