/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import hudson.FilePath;
import hudson.Util;

import java.io.File;
import java.io.IOException;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.hudson.util.FilePathHelper;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Unit tests for {@link DtrConfigCreator}.
 * 
 * @author Dirk Weigenand
 */
public final class DtrConfigCreatorTest {
    /**
     * URL to DTR without fully qualified DNS host name.
     */
    private static final String DTR_URL_WITHOUT_FULLY_QUALIFIED_HOSTNAME = "http://DI0DB:53000/dtr";

    /**
     * URL to build server.
     */
    private static final String BUILD_SERVER_URL = "http://di0db.example.com:53000";

    /**
     * object under test.
     */
    private DtrConfigCreator configCreator;

    /**
     * workspace to use for tests.
     */
    private File workspace;

    /**
     * development configuration used during test.
     */
    private DevelopmentConfiguration config;

    /**
     * Set up the fixture used during test.
     * 
     * @throws IOException
     *             when creating the temporary directory used as workspace or sub folders in it fail
     * @throws InterruptedException
     *             might be thrown from FilePath operations
     */
    @Before
    public void setUp() throws IOException, InterruptedException {
        workspace = Util.createTempDir();
        config = new DevelopmentConfiguration("DI0_testTrack_D");
        config.setBuildServer(BUILD_SERVER_URL);

        Compartment compartment = Compartment.create("example.com_SC1_1", CompartmentState.Source);
        compartment.setDtrUrl(DTR_URL_WITHOUT_FULLY_QUALIFIED_HOSTNAME);
        config.add(compartment);

        compartment = Compartment.create("example.com_SC2_1", CompartmentState.Source);
        compartment.setDtrUrl(DTR_URL_WITHOUT_FULLY_QUALIFIED_HOSTNAME);
        config.add(compartment);

        configCreator = new DtrConfigCreator(new FilePath(workspace), config);
        configCreator.execute();
    }

    /**
     * Tear down test fixture.
     * 
     * Remote workspace used for testing.
     * 
     * @throws IOException
     *             when removing the workspace fails.
     */
    @After
    public void tearDown() throws IOException {
        config = null;
        configCreator = null;

        Util.deleteRecursive(workspace);
    }

    /**
     * Assert that the various configuration files are created with correct content.
     * 
     * Test method for {@link org.arachna.netweaver.hudson.nwdi.DtrConfigCreator#execute()}.
     */
    @Test
    public void testServersXml() {
        final FilePath workspace = new FilePath(this.workspace);
        final FilePath dotDtc = workspace.child(NWDIConfigFolder.DTC.getName());

        assertFilePathExists(dotDtc);

        final FilePath dotDtr = workspace.child(NWDIConfigFolder.DTR.getName());
        assertFilePathExists(dotDtr);

        final FilePath serversXml = dotDtr.child(DtrConfigCreator.SERVERS_XML);
        assertFilePathExists(serversXml);
        assertContent(serversXml, String.format("/servers/server[@url = '%s/']", BUILD_SERVER_URL));
        assertContent(serversXml, String.format("/servers/server[@url = '%s/']", DTR_URL_WITHOUT_FULLY_QUALIFIED_HOSTNAME));
    }

    /**
     * Assert that the various configuration files are created with correct content.
     * 
     * Test method for {@link org.arachna.netweaver.hudson.nwdi.DtrConfigCreator#execute()}.
     */
    @Test
    public void testClientsXml() {
        final FilePath workspace = new FilePath(this.workspace);
        final FilePath dotDtr = workspace.child(NWDIConfigFolder.DTR.getName());
        assertFilePathExists(dotDtr);

        final FilePath dotDtc = workspace.child(NWDIConfigFolder.DTC.getName());
        assertFilePathExists(dotDtc);

        final FilePath clientsXml = dotDtr.child(DtrConfigCreator.CLIENTS_XML);
        assertFilePathExists(clientsXml);
        assertContent(clientsXml, String.format("/clients/client[@name = '%s']", config.getName()));
        assertContent(clientsXml, String.format("/clients/client[@logicalSystem = '%s']", config.getName()));
        assertContent(clientsXml, String.format("/clients/client[@absoluteLocalRoot = '%s']", FilePathHelper.makeAbsolute(dotDtc)));
    }

    /**
     * Assert that the various configuration files are created with correct content.
     * 
     * Test method for {@link org.arachna.netweaver.hudson.nwdi.DtrConfigCreator#execute()}.
     */
    @Test
    public void testSystemXml() {
        final FilePath workspace = new FilePath(this.workspace);
        final FilePath dotDtr = workspace.child(NWDIConfigFolder.DTR.getName());
        assertFilePathExists(dotDtr);

        final FilePath dotDtc = workspace.child(NWDIConfigFolder.DTC.getName());
        assertFilePathExists(dotDtc);

        final FilePath systemXml = dotDtr.child(config.getName() + ".system");
        assertFilePathExists(systemXml);
        assertContent(systemXml,
            String.format("/system/repositoryServers/repositoryServer[@url = '%s']", DTR_URL_WITHOUT_FULLY_QUALIFIED_HOSTNAME));
    }

    /**
     * Assert that the given <code>FilePath</code> contains content that is matched by the given XPath expression.
     * 
     * @param path
     *            the <code>FilePath</code> whose contents is to be tested.
     * @param xPath
     *            the XPath expression to be used for testing.
     */
    private void assertContent(final FilePath path, final String xPath) {
        try {
            final String content = path.readToString();
            System.err.println(content);
            final Document document = XMLUnit.buildControlDocument(content);
            final XpathEngine engine = XMLUnit.newXpathEngine();
            assertTrue(String.format("xpath '%s' did not match in document '%s'.", xPath, content), engine
                .getMatchingNodes(xPath, document).getLength() == 1);
        }
        catch (final SAXException e) {
            fail(e.getMessage());
        }
        catch (final IOException e) {
            fail(e.getMessage());
        }
        catch (final XpathException e) {
            fail(String.format("%s: '%s'", e.getMessage(), xPath));
        }
    }

    /**
     * Assert that the given <code>FilePath</code> exists.
     * 
     * @param path
     *            the <code>FilePath</code> to be tested for existence.
     */
    private void assertFilePathExists(final FilePath path) {
        try {
            if (!path.exists()) {
                fail("Assert failed: Path " + path.getName() + " does not exist.");
            }
        }
        catch (final IOException e) {
            fail(e.getMessage());
        }
        catch (final InterruptedException e) {
            fail(e.getMessage());
        }
    }
}
