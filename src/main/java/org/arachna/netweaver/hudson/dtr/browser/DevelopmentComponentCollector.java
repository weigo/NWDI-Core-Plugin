/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.ClientProtocolException;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;

/**
 * Collector for development components from activities.
 * 
 * @author Dirk Weigenand
 */
final class DevelopmentComponentCollector {
    /**
     * error message when an error occurred computing the associated development
     * components of the given set of activities.
     */
    private static final String ERROR_CALCULATING_AFFECTED_DEVELOPMENT_COMPONENTS =
        "There was an error calculating the affected development components for the given set of activities.";

    /**
     * template for querying details of a activity.
     */
    private static final String ACTIVITY_DETAIL_QUERY_TEMPLATE =
        "%s/dtr/system-tools/reports/ResourceDetails?technical=false&path=%s";

    /**
     * Template for querying resources of an activity.
     */
    private static final String RESOURCE_QUERY_TEMPLATE =
        "%s/dtr/system-tools/reports/ResourceSetDetails?namespace=DAV:&path=%s&name=activity-version-set";

    /**
     * Template for querying details of a resource of an activity.
     */
    private static final String RESOURCE_DETAIL_QUERY_TEMPLATE =
        "%s/dtr/system-tools/reports/ResourceDetails?technical=false&path=/vh/%s";
    /**
     * Logger to use.
     */
    private static final Logger LOGGER = Logger.getLogger(DevelopmentComponentCollector.class.getName());

    /**
     * DtrHttpClient for browsing the DTR.
     */
    private final DtrHttpClient dtrHttpClient;

    /**
     * URL of DTR server.
     */
    private final String dtrUrl;

    /**
     * registry for {@link DevelopmentComponent} objects.
     */
    private final DevelopmentComponentFactory dcFactory;

    /**
     * Create an instance of a <code>DevelopmentComponentCollector</code> with
     * the list of given activities.
     * 
     * @param dtrHttpClient
     *            HTTP-Client for querying the DTR.
     * @param dtrUrl
     *            URL of DTR server.
     * @param dcFactory
     *            registry for {@link DevelopmentComponent} objects. Used to
     *            create and register development components that are related to
     *            an activity in the DTR.
     */
    public DevelopmentComponentCollector(final DtrHttpClient dtrHttpClient, final String dtrUrl,
        final DevelopmentComponentFactory dcFactory) {
        this.dtrHttpClient = dtrHttpClient;
        this.dtrUrl = dtrUrl;
        this.dcFactory = dcFactory;
    }

    /**
     * Collect development components associated to the given list of
     * activities.
     * 
     * @param activities
     *            list of activities the associated development components are
     *            to belooked up
     * @return the set of associated development components
     */
    public Set<DevelopmentComponent> collect(final List<Activity> activities) {
        final Set<DevelopmentComponent> components = new HashSet<DevelopmentComponent>();

        if (activities != null) {
            for (final Activity activity : activities) {
                components.addAll(calculateAffectedDevelopmentComponents(activity));
            }
        }

        return components;
    }

    /**
     * Calculate the set of development components affected by the given
     * activity. The affected components will have their
     * {@link DevelopmentComponent#isNeedsRebuild()} property set to
     * <code>true</code>.
     * 
     * @param activity
     *            activity to calculate affected development components for.
     * @return set of development components affected by the given activity.
     */
    private Set<DevelopmentComponent> calculateAffectedDevelopmentComponents(final Activity activity) {
        final Set<DevelopmentComponent> components = new HashSet<DevelopmentComponent>();

        try {
            updateActivityDetails(activity);
            createActivityResources(activity);

            for (final ActivityResource resource : activity.getResources()) {
                new ResourceDetailsParser(resource).parse(dtrHttpClient.getContent(String.format(
                    RESOURCE_DETAIL_QUERY_TEMPLATE, dtrUrl, resource.getId())));
                components.add(resource.getDevelopmentComponent());
            }
        }
        catch (final ClientProtocolException e) {
            LOGGER.log(Level.SEVERE, ERROR_CALCULATING_AFFECTED_DEVELOPMENT_COMPONENTS, e);
        }
        catch (final IllegalStateException e) {
            LOGGER.log(Level.SEVERE, ERROR_CALCULATING_AFFECTED_DEVELOPMENT_COMPONENTS, e);
        }
        catch (final IOException e) {
            LOGGER.log(Level.SEVERE, ERROR_CALCULATING_AFFECTED_DEVELOPMENT_COMPONENTS, e);
        }

        return components;
    }

    /**
     * Update the given activity with resources (from the DTR HTML report).
     * 
     * @param activity
     *            the Activity to be updated.
     * @throws IOException
     *             when reading the DTR report fails
     */
    private void createActivityResources(final Activity activity) throws IOException {
        final ActivityResourceParser activityResourceParser = new ActivityResourceParser(dcFactory, activity);
        final String queryURL = String.format(RESOURCE_QUERY_TEMPLATE, dtrUrl, activity.getActivityPath());
        activityResourceParser.parse(dtrHttpClient.getContent(queryURL));
    }

    /**
     * Update the details of the given activity from the DTR HTML report.
     * 
     * @param activity
     *            activity to be updated.
     * @throws IOException
     *             when reading the DTR report fails
     */
    private void updateActivityDetails(final Activity activity) throws IOException {
        final ActivityDetailParser activityDetailParser = new ActivityDetailParser(activity);
        final String queryURL = String.format(ACTIVITY_DETAIL_QUERY_TEMPLATE, dtrUrl, activity.getActivityPath());
        activityDetailParser.parse(dtrHttpClient.getContent(queryURL));
    }
}
