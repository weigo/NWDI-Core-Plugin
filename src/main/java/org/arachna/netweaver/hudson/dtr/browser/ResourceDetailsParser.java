/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Parser for details of a resource associated with an NWDI activitiy.
 * 
 * @author Dirk Weigenand
 */
final class ResourceDetailsParser extends AbstractResourceParser {
    /**
     * Logger for errors.
     */
    private static final Logger LOGGER = Logger.getLogger(ResourceDetailsParser.class.getName());

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
     * XPath expression for selecting property name/value nodes from the associated table in the html page returned upon
     * query.
     */
    private static final String XPATH = "/html/body/table[3]/tr/td";

    /**
     * Resource that is to be updated with details for the associated activity.
     */
    private final ActivityResource resource;

    /**
     * Create an instance of a <code>ResourceDetailsParser</code> with the given {@link ActivityResource} that is to be
     * updated.
     * 
     * @param resource
     *            the {@link ActivityResource} to update.
     */
    ResourceDetailsParser(final ActivityResource resource) {
        this.resource = resource;
    }

    /**
     * Update the {@link ActivityResource} given at object instantiation with information read from the given html page.
     * 
     * @param nodes
     *            list of nodes extracted from resource detail page
     */
    @Override
    void parseInternal(final List<Object> nodes) {
        final SimpleDateFormat format = new SimpleDateFormat(ActivityListParser.ACTIVITY_DATE_FORMAT);

        try {
            this.resource.setCreationDate(format.parse(nodeValueAt(nodes, CREATION_DATE)));
            this.resource.setLastModified(format.parse(nodeValueAt(nodes, LAST_MODIFIED)));
            this.resource.setSequenceNumber(Integer.valueOf(nodeValueAt(nodes, SEQUENCE_NUMBER)));
            this.resource.setDeleted(Boolean.valueOf("yes".equals(nodeValueAt(nodes, IS_DELETED).toLowerCase())));
        }
        catch (final ParseException e) {
            LOGGER.log(Level.SEVERE,
                String.format("Error parsing date using format string:\n%s", ActivityListParser.ACTIVITY_DATE_FORMAT),
                e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    String getXPath() {
        return XPATH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int getExpectedNodeLen() {
        return IS_DELETED;
    }
}
