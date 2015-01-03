/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;

/**
 * A browser for the design time repository's web browser interface.
 * 
 * @author Dirk Weigenand
 */
public final class DtrBrowser {
    /**
     * Error message indicating a communication error with the DTR.
     */
    private static final String ERROR_COMMUNICATING_WITH_DTR = "An error occured communicating with the DTR.";

    /**
     * Error message when extracting activities from DTR failed.
     */
    private static final String ERROR_READING_ACTIVITIES =
        "There was an error reading the list of activities (URL: %s) from the DTR.";

    /**
     * query for reading activities for a given compartment.
     */
    private static final String ACTIVITY_QUERY = "%s/system-tools/reports/ActivityQuery?wspPath=/%s"
        + "&user=&closedOnly=on&isnFrom=&isnTo=&nonEmptyOnly=on&folderPath=&command=Show";

    /**
     * DtrHttpClient for browsing the DTR.
     */
    private final DtrHttpClient dtrHttpClient;

    /**
     * development configuration to use in queries.
     */
    private final DevelopmentConfiguration config;

    /**
     * Create an instance of a <code>DtrBrowser</code>.
     * 
     * @param config
     *            the {@link DevelopmentConfiguration} to use in queries.
     * @param dtrUser
     *            user for accessing the DTR.
     * @param password
     *            password to authenticate the user against the DTR's UME.
     */
    public DtrBrowser(final DevelopmentConfiguration config, final String dtrUser, final String password) {
        this.config = config;
        dtrHttpClient = new DtrHttpClient(dtrUser, password);
    }

    /**
     * Get list of activities in the given workspace containing the given
     * compartment.
     * 
     * @param compartment
     *            compartment to use for retrieving activities.
     * @param activityFilter
     *            filter for NWDI activities.
     * @return list of retrieved activities (may be empty).
     */
    private List<Activity> getActivities(final Compartment compartment, final ActivityFilter activityFilter) {
        final String queryUrl =
            String.format(ACTIVITY_QUERY, compartment.getDtrUrl(), compartment.getInactiveLocation());

        try {
            final ActivityListParser activityListBrowser = new ActivityListParser(activityFilter);
            activityListBrowser.parse(dtrHttpClient.getContent(queryUrl));
            return activityListBrowser.getActivities();
        }
        catch (final ClientProtocolException e) {
            throw new IllegalStateException(ERROR_COMMUNICATING_WITH_DTR, e);
        }
        catch (final IOException e) {
            throw new IllegalStateException(String.format(ERROR_READING_ACTIVITIES, queryUrl), e);
        }
        catch (final IllegalStateException ise) {
            throw new IllegalStateException(String.format(ERROR_READING_ACTIVITIES, queryUrl), ise);
        }
    }

    /**
     * Get a list of activities in the given workspace matching the given
     * {@link ActivityFilter}.
     * 
     * @param activityFilter
     *            an {@link ActivityFilter} for filtering the list of returned
     *            activities.
     * @return a list of activities matching the given {@link ActivityFilter} in
     *         the given workspace.
     */
    private List<Activity> getActivities(final ActivityFilter activityFilter) {
        final List<Activity> activities = new ArrayList<Activity>();

        for (final Compartment compartment : config.getCompartments(CompartmentState.Source)) {
            activities.addAll(this.getActivities(compartment, activityFilter));
        }

        return activities;
    }

    /**
     * Get a list of activities in the given workspace matching the given
     * {@link ActivityFilter}.
     * 
     * @param since
     *            date after which to look for activities.
     * @return a list of activities in the given workspace.
     */
    public List<Activity> getActivities(final Date since) {
        final List<Activity> activities = this.getActivities(new ActivityCheckinDateFilter(since));

        dtrHttpClient.close();

        return activities;
    }

    /**
     * Determine activities from DTR that were checked in after the given date
     * <code>since</code>. Also collect the affected resources and development
     * components. Those components will have the property
     * <code>needsRebuild</code> set to <code>true</code> afterwards.
     * 
     * @param dcFactory
     *            registry for development components to use for when querying
     *            development components affected by activities.
     * @param since
     *            date since when new activities should be detected.
     * @return list of detected activities.
     */

    public List<Activity> getActivitiesWithResourcesAndDevelopmentComponents(
        final DevelopmentComponentFactory dcFactory, final Date since) {
        final List<Activity> activities = this.getActivities(new ActivityCheckinDateFilter(since));

        for (final DevelopmentComponent component : new DevelopmentComponentCollector(dtrHttpClient,
            config.getCmsUrl(), dcFactory).collect(activities)) {
            component.setNeedsRebuild(true);
        }

        dtrHttpClient.close();

        return activities;
    }
}
