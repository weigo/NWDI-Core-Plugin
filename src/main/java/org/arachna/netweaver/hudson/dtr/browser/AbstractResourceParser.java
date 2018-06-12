/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Base class for resource parsers. Implements logic common to all parsers extracting information read from the DTR.
 * 
 * @author Dirk Weigenand
 */
abstract class AbstractResourceParser {
    /**
     * Parses the given <code>InputStream</code> and updates the activities details.
     * 
     * @param content
     *            of the activities detail HTML page.
     */
    final void parse(final InputStream content) {
        final Document document = JTidyHelper.getDocument(content);

        try {
            content.close();
            final DOMXPath xPath = new DOMXPath(getXPath());

            final List selectedNodes = xPath.selectNodes(document);

            if (selectedNodes.size() < getExpectedNodeLen()) {
                throw new IllegalStateException(String.format("%s did not yield expected node count!", getXPath()));
            }

            parseInternal(selectedNodes);
        }
        catch (final JaxenException e) {
            throw new IllegalStateException(e);
        }
        catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Extracts the node's value as text at the given index.
     * 
     * @param nodes
     *            a list of {@link Node} objects representing a XPath selection result.
     * @param index
     *            index into the list of nodes
     * 
     * @return node's value as text at the given index.
     */
    protected final String nodeValueAt(final List<Object> nodes, final int index) {
        final Node textNode = ((Node)nodes.get(index)).getFirstChild();

        return textNode == null ? "" : textNode.getNodeValue();
    }

    /**
     * Extract resources from the given list of nodes.
     * 
     * @param nodes
     *            list of selected nodes
     */
    abstract void parseInternal(List nodes);

    /**
     * Get the XPath to use extracting resources.
     * 
     * @return XPath to use extracting resources.
     */
    abstract String getXPath();

    /**
     * Return the minimum count of nodes one can expect when applying the XPath expression returned by {@see #getXPath()}.
     * 
     * @return minimum count of nodes selected by getXPath().
     */
    abstract int getExpectedNodeLen();
}
