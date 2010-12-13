/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Extractor for {@link Compartment} objects from a DTR workspace listing.
 * 
 * @author Dirk Weigenand
 */
final class SoftwareComponentsParser {
    /**
     * XPath expression for extracting compartments from a workspace listing in
     * DTR.
     */
    private static final String XPATH = "/html/body//a[starts-with(@href, '/dtr/ws/%s/')]";

    /**
     * Extracts a list of compartments from the given {@link InputStream}
     * containing the listing of the given workspace.
     * 
     * @param compartmentList
     *            HTML-Page containing the listing of the given workspace
     * @param workSpace
     *            name of workspace that was listed
     * @return list of {@link Compartment} that were extracted from the
     *         HTML-Page
     */
    public List<Compartment> parse(final InputStream compartmentList, final DevelopmentConfiguration config) {
        final List<Compartment> compartments = new ArrayList<Compartment>();
        final String path = String.format(XPATH, config.getWorkspace());
        final Document document = JTidyHelper.getDocument(compartmentList);

        try {
            final DOMXPath xPath = new DOMXPath(path);
            Node node;

            for (final Object returnValue : xPath.selectNodes(document)) {
                node = (Node)returnValue;
                final String compartmentName =
                    createCompartmentName(node.getAttributes().getNamedItem("href").getNodeValue());
                final Compartment compartment = config.getCompartment(compartmentName);

                if (compartment != null) {
                    compartments.add(compartment);
                }
                else {
                    System.err.println(compartmentName + " could not be found in " + config.getName());
                }
            }
        }
        catch (final JaxenException e) {
            throw new RuntimeException(e);
        }

        return compartments;
    }

    /**
     * Create an {@link Compartment} object from the given link.
     * 
     * @param href
     *            link to compartment in html page.
     * @return compartment object parsed from given link.
     */
    private String createCompartmentName(final String href) {
        final int firstUnderScore = href.lastIndexOf('/');
        final String name = href.substring(firstUnderScore + 1, href.length()) + "_1";

        return name;
    }
}
