/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.confdef;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.arachna.netweaver.dc.types.BuildVariant;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * JUnit-Test for reading '.confdef' configuration files using
 * {@link ConfDefReader}.
 * 
 * @author Dirk Weigenand
 */
public final class ConfDefReaderTest {
    /**
     * constant for property 'com.sap.jdk.javac.force_fork'.
     */
    private static final String COM_SAP_JDK_JAVAC_FORCE_FORK = "com.sap.jdk.javac.force_fork";

    /**
     * constant for property 'com.sap.jdk.home_path_key'.
     */
    private static final String COM_SAP_JDK_HOME_PATH_KEY = "com.sap.jdk.home_path_key";

    /**
     * Test method for reading a development configuration with a default build
     * variant.
     */
    @Test
    public void testReadDevelopmentConfigurationWithDefaultBuildVariant() {
        try {
            final DevelopmentConfiguration configuration =
                readDevelopmentConfiguration("DevelopmentConfigurationWithDefaultBuildVariant.confdef");
            final BuildVariant variant = configuration.getBuildVariant();
            assertNotNull(variant);
            assertNull(variant.getBuildOption(COM_SAP_JDK_HOME_PATH_KEY));
            assertNull(variant.getBuildOption(COM_SAP_JDK_JAVAC_FORCE_FORK));
        }
        catch (final IOException e) {
            fail(e.getLocalizedMessage());
        }
        catch (final SAXException e) {
            fail(e.getLocalizedMessage());
        }
    }

    /**
     * Test method for reading a development configuration with a configured
     * build variant.
     */
    @Test
    public void testReadDevelopmentConfigurationWithConfiguredDefaultBuildVariant() {
        try {
            final DevelopmentConfiguration configuration =
                readDevelopmentConfiguration("DevelopmentConfigurationWithConfiguredDefaultBuildVariant.confdef");
            final BuildVariant variant = configuration.getBuildVariant();
            assertNotNull(variant);
            assertEquals("JDK1.3.1_HOME", variant.getBuildOption(COM_SAP_JDK_HOME_PATH_KEY));
            assertEquals("true", variant.getBuildOption(COM_SAP_JDK_JAVAC_FORCE_FORK));
            assertEquals("http://di0db.example.com:50000", configuration.getBuildServer());
        }
        catch (final IOException e) {
            fail(e.getLocalizedMessage());
        }
        catch (final SAXException e) {
            fail(e.getLocalizedMessage());
        }
    }

    @Test
    public void testReadExampleConfDef() {
        try {
            final DevelopmentConfiguration configuration = readDevelopmentConfiguration("Example.confdef");

            final Collection<Compartment> compartments = configuration.getCompartments();

            assertThat(8, equalTo(compartments.size()));

            final Set<String> compartmentNames = getExpectedCompartmentNames();

            for (final Compartment compartment : compartments) {
                assertThat(compartmentNames, hasItem(compartment.getName()));
            }
        }
        catch (final IOException e) {
            fail(e.getLocalizedMessage());
        }
        catch (final SAXException e) {
            fail(e.getLocalizedMessage());
        }
    }

    @Test
    public void testReadExampleConfDefAndVerifyThatGetCompartmentsReturnsTheCorrectCompartments() {
        try {
            final DevelopmentConfiguration configuration = readDevelopmentConfiguration("Example.confdef");

            for (final String compartmentName : getExpectedCompartmentNames()) {
                assertThat(null, is(not(equalTo(configuration.getCompartment(compartmentName)))));
            }
        }
        catch (final IOException e) {
            fail(e.getLocalizedMessage());
        }
        catch (final SAXException e) {
            fail(e.getLocalizedMessage());
        }
    }

    /**
     * @return
     */
    private Set<String> getExpectedCompartmentNames() {
        final Set<String> compartmentNames = new HashSet<String>();

        compartmentNames.add("example.com_EXAMPLE_SC1_1");
        compartmentNames.add("sap.com_FRAMEWORK_1");
        compartmentNames.add("sap.com_ENGFACADE_1");
        compartmentNames.add("sap.com_EP_BUILDT_1");
        compartmentNames.add("sap.com_FRAMEWORK_1");
        compartmentNames.add("sap.com_SAP_BUILDT_1");
        compartmentNames.add("sap.com_SAP_JTECHS_1");
        compartmentNames.add("sap.com_SAP-JEE_1");
        compartmentNames.add("sap.com_WD-RUNTIME_1");

        return compartmentNames;
    }

    /**
     * Read a development configuration with the given resource name.
     * 
     * @param configurationName
     *            resource to be read.
     * @return the read <code>DevelopmentConfiguration</code>.
     * @throws IOException
     *             any <code>IOException</code> thrown in the underlying code.
     * @throws SAXException
     *             any <code>SAXException</code> thrown in the underlying code.
     */
    private DevelopmentConfiguration readDevelopmentConfiguration(final String configurationName) throws IOException,
        SAXException {
        final XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        final ConfDefReader developmentConfigurationReader = new ConfDefReader(xmlReader);

        return developmentConfigurationReader.read(this.getClass().getResourceAsStream(configurationName));
    }
}
