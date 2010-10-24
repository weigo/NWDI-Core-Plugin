/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.confdef;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.arachna.netweaver.dc.types.BuildVariant;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * JUnit-Test for reading '.confdef' configuration files using {@link ConfDefReader}.
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
     * Test method for reading a development configuration with a default build variant.
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
     * Test method for reading a development configuration with a configured build variant.
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
    private DevelopmentConfiguration readDevelopmentConfiguration(final String configurationName) throws IOException, SAXException {
        final XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        final ConfDefReader developmentConfigurationReader = new ConfDefReader(xmlReader);

        return developmentConfigurationReader.read(this.getClass().getResourceAsStream(configurationName));
    }
}
