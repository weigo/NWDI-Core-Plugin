/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import java.util.List;

/**
 * Parser for an activity detail HTML page.
 * 
 * @author Dirk Weigenand
 */
final class ActivityDetailParser extends AbstractResourceParser {
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
     * Create an instance of an <code>ActivityDetailParser</code> with the given <code>Activity</code>.
     * 
     * @param activity
     *            the <code>Activity</code> whose details shall be updated.
     */
    ActivityDetailParser(final Activity activity) {
        this.activity = activity;
    }

    /**
     * Updates the activity's long description.
     * 
     * @param nodes
     *            list of nodes containing an activity's details.
     */
    @Override
    void parseInternal(final List<Object> nodes) {
        this.activity.setDescription(this.nodeValueAt(nodes, LONG_DESCRIPTION));
    }

    @Override
    String getXPath() {
        return XPATH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int getExpectedNodeLen() {
        return LONG_DESCRIPTION;
    }
}
