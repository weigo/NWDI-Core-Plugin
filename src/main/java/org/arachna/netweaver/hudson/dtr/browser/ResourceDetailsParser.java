/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Parser for details of a resource associated with an NWDI activitiy.
 * 
 * @author Dirk Weigenand
 */
final class ResourceDetailsParser {
    /**
     * Logger for errors.
     */
    private static final Logger LOGGER = Logger.getLogger(ResourceDetailsParser.class);

    /**
     * index into property table for the resources creation date.
     */
    private static final int CREATION_DATE = 9;

    /**
     * index into property table for the resources creation date.
     */
    private static final int LAST_MODIFIED = 11;

    /**
     * index into property table for the resources creation date.
     */
    private static final int SEQUENCE_NUMBER = 21;

    /**
     * index into property table for the resources creation date.
     */
    private static final int IS_DELETED = 23;

    /**
     * XPath expression for selecting property name/value nodes from the
     * associated table in the html page returned upon query.
     */
    private static final String XPATH = "/html/body/table[3]/tr/td";

    /**
     * Resource that is to be updated with details for the associated activity.
     */
    private final ActivityResource resource;

    /**
     * Create an instance of a <code>ResourceDetailsParser</code> with the given
     * {@link ActivityResource} that is to be updated.
     */
    ResourceDetailsParser(final ActivityResource resource) {
        this.resource = resource;
    }

    /**
     * Update the {@link ActivityResource} given at object instantiation with
     * information read from the given html page.
     * 
     * @param resourcePage
     *            html page returned from the DTR for our resource query.
     */
    void parse(final InputStream resourcePage) {
        final Document content = JTidyHelper.getDocument(resourcePage);

        try {
            final DOMXPath xpath = new DOMXPath(XPATH);
            final SimpleDateFormat format = new SimpleDateFormat(ActivityListParser.ACTIVITY_DATE_FORMAT);
            final List nodes = xpath.selectNodes(content);

            this.resource.setCreationDate(format.parse(nodeValueAt(nodes, CREATION_DATE)));
            this.resource.setLastModified(format.parse(nodeValueAt(nodes, LAST_MODIFIED)));
            this.resource.setSequenceNumber(Integer.valueOf(nodeValueAt(nodes, SEQUENCE_NUMBER)));
            this.resource.setDeleted(Boolean.valueOf("yes".equals(nodeValueAt(nodes, IS_DELETED).toLowerCase())));
        }
        catch (final JaxenException e) {
            LOGGER.error(String.format("Error parsing response to resource request using:\n%s", XPATH), e);
        }
        catch (final ParseException e) {
            LOGGER.error(
                String.format("Error parsing date using format string:\n%s", ActivityListParser.ACTIVITY_DATE_FORMAT),
                e);
        }
    }

    /**
     * @param nodes
     * @return
     * @throws DOMException
     */
    private String nodeValueAt(final List nodes, final int index) throws DOMException {
        return ((Node)nodes.get(index)).getFirstChild().getNodeValue();
    }
}
