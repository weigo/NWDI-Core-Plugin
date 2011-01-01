/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.ClientProtocolException;
import org.arachna.netweaver.dc.types.Compartment;
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
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(DtrBrowser.class.getName());

    /**
     * query for reading activities for a given compartment.
     */
    private static final String ACTIVITY_QUERY = "%s/dtr/system-tools/reports/ActivityQuery?wspPath=/%s"
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
     * registry for {@link DevelopmentComponent} objects.
     */
    private DevelopmentComponentFactory dcFactory = new DevelopmentComponentFactory();

    /**
     * Create an instance of a <code>DtrBrowser</code>.
     * 
     * @param config
     *            the {@link DevelopmentConfiguration} to use in queries.
     * @param dcFactory
     *            registry of {@link DevelopmentComponent} objects.
     * @param dtrUser
     *            user for accessing the DTR.
     * @param password
     *            password to authenticate the user against the DTR's UME.
     */
    public DtrBrowser(final DevelopmentConfiguration config, final DevelopmentComponentFactory dcFactory,
        final String dtrUser, final String password) {
        this.config = config;
        this.dtrHttpClient = new DtrHttpClient(dtrUser, password);
        this.dcFactory = dcFactory;
    }

    /**
     * Get list of compartments contained in the given workspace.
     * 
     * @return list of compartments found in workspace.
     */
    public List<Compartment> getCompartments() {
        final SoftwareComponentsParser componentsBrowser = new SoftwareComponentsParser();
        final List<Compartment> compartments = new ArrayList<Compartment>();

        try {
            // FIXME: should use the dtr location of a software component of the
            // given development configuration
            final String queryUrl = String.format("%s/dtr/ws/%s", this.config.getCmsUrl(), this.config.getWorkspace());
            final InputStream result = this.dtrHttpClient.getContent(queryUrl);
            compartments.addAll(componentsBrowser.parse(result, config));
        }
        catch (final ClientProtocolException e) {
            LOGGER.log(Level.SEVERE, "An error occured communicating with the DTR.", e);
        }
        catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "An error occured reading the list of compartments from the DTR.", e);
        }

        return compartments;
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
    public List<Activity> getActivities(final Compartment compartment, final ActivityFilter activityFilter) {
        final List<Activity> activities = new ArrayList<Activity>();
        final ActivityListParser activityListBrowser = new ActivityListParser(activityFilter);

        try {
            final String queryUrl =
                String.format(ACTIVITY_QUERY, compartment.getDtrUrl(), compartment.getInactiveLocation());
            activities.addAll(activityListBrowser.parse(this.dtrHttpClient.getContent(queryUrl)));
        }
        catch (final ClientProtocolException e) {
            LOGGER.log(Level.SEVERE, "There occured an error communicating with the DTR.", e);
        }
        catch (final IOException e) {
            LOGGER.log(
                Level.SEVERE,
                String.format("There was an error reading the list of activities (for %s/%s_%s) from the DTR.",
                    config.getWorkspace(), compartment.getVendor(), compartment.getName()), e);
        }

        return activities;
    }

    /**
     * Extract changed development components from the given list of activities.
     * 
     * @param activities
     *            activities the changed development components shall be
     *            extracted from.
     * @return list of changed development components associated with the given
     *         activities.
     */
    public Set<DevelopmentComponent> getDevelopmentComponents(final List<Activity> activities) {
        final DevelopmentComponentCollector collector =
            new DevelopmentComponentCollector(this.dtrHttpClient, this.config.getCmsUrl(), this.dcFactory);

        return collector.collect(activities);
    }

    /**
     * Get a list of changed development components for the given workspace
     * since the given date.
     * 
     * @return a list of changed development components
     */
    public Set<DevelopmentComponent> getChangedDevelopmentComponents() {
        return this.getChangedDevelopmentComponents(new ActivityCheckinDateFilter());
    }

    /**
     * Get a list of changed development components for the given workspace
     * since the given date.
     * 
     * @param activityFilter
     *            filter the activities using the given {@link ActivityFilter}.
     * @return a list of changed development components matched by the given
     *         {@link ActivityFilter}.
     */
    private Set<DevelopmentComponent> getChangedDevelopmentComponents(final ActivityFilter activityFilter) {
        final List<Activity> activities = this.getActivities(activityFilter);
        Collections.sort(activities, new ActivityByCheckInDateComparator());
        final long start = System.currentTimeMillis();

        final Set<DevelopmentComponent> changedComponents = this.getDevelopmentComponents(activities);
        this.duration(start, "getDevelopmentComponents");

        return changedComponents;
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
    public List<Activity> getActivities(final ActivityFilter activityFilter) {
        final List<Activity> activities = new ArrayList<Activity>();

        final long start = System.currentTimeMillis();

        for (final Compartment compartment : this.getCompartments()) {
            activities.addAll(this.getActivities(compartment, activityFilter));
        }

        this.duration(start, "getActivities");

        return activities;
    }

    /**
     * Get a list of all activities in the given workspace.
     * 
     * @return a list of all activities in the given workspace.
     */
    public List<Activity> getActivities() {
        final ActivityFilter activityFilter = new ActivityFilter() {
            public boolean accept(final Activity activity) {
                return true;
            }
        };

        return this.getActivities(activityFilter);
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
        return this.getActivities(this.createActivityCheckinDateFilter(since));
    }

    /**
     * Get a list of changed development components for the given workspace
     * since the given date.
     * 
     * @param since
     *            date after which to look for activities.
     * @return a list of changed development components
     */
    public Set<DevelopmentComponent> getChangedDevelopmentComponents(final Date since) {
        return this.getChangedDevelopmentComponents(this.createActivityCheckinDateFilter(since));
    }

    /**
     * @param since
     *            start date for activity filtering.
     * @return the configured filter (with the given start date and the current
     *         time).
     */
    private ActivityFilter createActivityCheckinDateFilter(final Date since) {
        return new ActivityCheckinDateFilter(since, Calendar.getInstance().getTime());
    }

    /**
     * Compute the time spent since the given start time and log it using the
     * given message.
     * 
     * @param start
     *            the start time to use for computing the duration until now
     * @param msg
     *            message to log.
     */
    private void duration(final long start, final String msg) {
        final long duration = System.currentTimeMillis() - start;
        LOGGER.log(Level.INFO, String.format("%s took %d.%d sec. to complete.", msg, duration / 1000, duration % 1000));
    }
}
