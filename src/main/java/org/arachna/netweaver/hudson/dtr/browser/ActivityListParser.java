/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Node;

/**
 * Parser for a DTR activity list.
 * 
 * @author Dirk Weigenand
 */
final class ActivityListParser extends AbstractResourceParser {
    /**
     * date format for activity dates, resource modification dates etc.
     */
    // FIXME: this format might be dependent on the Locale the NWDI JVM is
    // running with. Should probably be configurable in the NWDI SCM config
    // snippet on the hudson configuration page.
    static final String ACTIVITY_DATE_FORMAT = "dd.MM.yyyy HH:mm:ss z";

    /**
     * XPath expression for extracting activities.
     */
    private static final String XPATH =
        "//a[starts-with(@href, '/dtr/system-tools/reports/ResourceDetails?') and contains(@href, 'path=/act/')]/../..";

    /**
     * List of extracted activities.
     */
    private final List<Activity> activities = new ArrayList<Activity>();

    /**
     * {@link ActivityFilter} to use when parsing activities. Initialized with an accept all filter.
     */
    private ActivityFilter activityFilter = new ActivityFilter() {
        @Override
        public boolean accept(final Activity activity) {
            return true;
        }
    };

    /**
     * date parser for check in times.
     */
    private final SimpleDateFormat dateParser = new SimpleDateFormat(ACTIVITY_DATE_FORMAT);

    /**
     * XPath for matching check in dates.
     */
    private DOMXPath checkInDateXPath;

    /**
     * XPath for matching the short description of an activity.
     */
    private DOMXPath commentXPath;

    /**
     * XPath for matching the principal of an activity.
     */
    private DOMXPath principalXPath;

    /**
     * XPath for matching check in dates.
     */
    private DOMXPath activityXPath;

    /**
     * Create an instance of an {@link ActivityListParser}.
     */
    ActivityListParser() {
        setUpXPaths();
    }

    /**
     * Create an instance of an {@link ActivityListParser}.
     * 
     * @param compartment
     * 
     * @param activityFilter
     *            the {@link ActivityFilter} to be used.
     */
    ActivityListParser(final ActivityFilter activityFilter) {
        if (activityFilter == null) {
            throw new IllegalArgumentException("activityFilter must not be null!");
        }

        this.activityFilter = activityFilter;
        setUpXPaths();
    }

    /**
     * Initialize XPath expression used later on to extract details of activities.
     */
    private void setUpXPaths() {
        try {
            commentXPath = new DOMXPath("td[1]/a");
            principalXPath = new DOMXPath("td[3]/a");
            checkInDateXPath = new DOMXPath("td[4]/text()");
            activityXPath = new DOMXPath("td[1]/a/@href");
        }
        catch (final JaxenException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Create an instance of an {@link Activity} from the given {@link org.w3c.dom.Node}.
     * 
     * @param node
     *            the node the activity's data should be read from.
     * @return the activity extracted from the given node.
     * @throws JaxenException
     *             when there was an error evaluating the XPath expressions used to extract the data
     * @throws ParseException
     *             when there was an error parsing the activity's date.
     */
    private Activity createActivity(final Node node) throws JaxenException, ParseException {
        return new Activity(getActivityUrl(node), getPrincipal(node), getComment(node), getCheckInDate(node));
    }

    /**
     * Get the short description from the given node.
     * 
     * @param node
     *            the node the short description should be read from.
     * @return the short description extracted from the given node.
     * @throws JaxenException
     *             when there was an error evaluating the XPath expressions used to extract the data
     */
    private String getComment(final Node node) throws JaxenException {
        return commentXPath.stringValueOf(node);
    }

    /**
     * Get the check in date from the given node.
     * 
     * @param node
     *            the node the activity's check in date should be read from.
     * @return the check in date extracted from the given node.
     * @throws JaxenException
     *             when there was an error evaluating the XPath expressions used to extract the data
     * @throws ParseException
     *             when there was an error parsing the activity's date.
     */
    private Date getCheckInDate(final Node node) throws JaxenException, ParseException {
        return dateParser.parse(checkInDateXPath.stringValueOf(node));
    }

    /**
     * Get the UME principal's name of the activity from the given node.
     * 
     * @param node
     *            the node the UME princiapl's name should be read from.
     * @return the UME principal's name extracted from the given node.
     * @throws JaxenException
     *             when there was an error evaluating the XPath expressions used to extract the data
     */
    private Principal getPrincipal(final Node node) throws JaxenException {
        return new Principal(principalXPath.stringValueOf(node).replace("/principals/", ""));
    }

    /**
     * Get the activity's url from the given node.
     * 
     * @param node
     *            the node the activity's url should be read from.
     * @return the activity's url extracted from the given node.
     * @throws JaxenException
     *             when there was an error evaluating the XPath expressions used to extract the data
     */
    private String getActivityUrl(final Node node) throws JaxenException {
        return activityXPath.stringValueOf(node);
    }

    /**
     * Extract activities from the given {@link java.io.InputStream}.
     * 
     * @param nodes
     *            extracted from activity list.
     */
    @Override
    void parseInternal(final List nodes) {
        Node node;
        Activity activity;

        try {
            for (final Object returnValue : nodes) {
                node = (Node)returnValue;
                activity = createActivity(node);

                if (activity != null && activityFilter.accept(activity)) {
                    activities.add(activity);
                }
            }
        }
        catch (final ParseException e) {
            throw new IllegalStateException(e);
        }
        catch (final JaxenException e) {
            throw new IllegalStateException(e);
        }
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
        return 0;
    }

    /**
     * Returns the list of extracted activities.
     * 
     * @return the list of extracted activities.
     */
    List<Activity> getActivities() {
        return activities;
    }
}
