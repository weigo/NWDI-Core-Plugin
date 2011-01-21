/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import java.io.InputStream;
import java.util.List;

import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Parser for a activity detail HTML page.
 * 
 * @author Dirk Weigenand
 */
final class ActivityDetailParser {
    /**
     * index of long description in details table.
     */
    private static final int LONG_DESCRIPTION = 15;

    /**
     * XPath expression for selecting the <td>s of the activity's properties.
     */
    private static final String XPATH = "/html/body/table[3]/tr/td";

    /**
     * {@link Activity} to update.
     */
    private final Activity activity;

    /**
     * Create an instance of an <code>ActivityDetailParser</code> with the given
     * <code>Activity</code>.
     * 
     * @param activity
     *            the <code>Activity</code> whose details shall be updated.
     */
    ActivityDetailParser(final Activity activity) {
        this.activity = activity;
    }

    /**
     * Parses the given <code>InputStream</code> and updates the activities
     * details.
     * 
     * @param content
     *            of the activities detail HTML page.
     */
    void parse(final InputStream content) {
        final Document document = JTidyHelper.getDocument(content);

        try {
            final DOMXPath xPath = new DOMXPath(XPATH);

            final List nodes = xPath.selectNodes(document);
            this.activity.setDescription(this.nodeValueAt(nodes, LONG_DESCRIPTION));
        }
        catch (final JaxenException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Extracts the node's value as text at the given index.
     * 
     * @param nodes
     *            a list of {@link Nodes} representing a XPath selection result.
     * 
     * @return node's value as text at the given index.
     * @throws DOMException
     *             when the text to be returned does not fit into the DOMString
     *             implementation
     */
    private String nodeValueAt(final List nodes, final int index) throws DOMException {
        final Node textNode = ((Node)nodes.get(index)).getFirstChild();

        return textNode == null ? "" : textNode.getNodeValue();
    }
}