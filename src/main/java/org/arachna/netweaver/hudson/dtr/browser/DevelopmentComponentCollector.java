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
     * Logger to use.
     */
    private static final Logger LOG = Logger.getLogger(DevelopmentComponentCollector.class.getName());

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
    private DevelopmentComponentFactory dcFactory = new DevelopmentComponentFactory();

    /**
     * Create an instance of a <code>DevelopmentComponentCollector</code> with the list of given activities.
     * 
     * @param dtrHttpClient
     *            HTTP-Client for querying the DTR.
     * @param dtrUrl
     *            URL of DTR server.
     * @param dcFactory
     *            registry for {@link DevelopmentComponent} objects. Used to create and register development components that are related to
     *            an activity in the DTR.
     */
    public DevelopmentComponentCollector(final DtrHttpClient dtrHttpClient, final String dtrUrl, final DevelopmentComponentFactory dcFactory) {
        this.dtrHttpClient = dtrHttpClient;
        this.dtrUrl = dtrUrl;
        this.dcFactory = dcFactory;
    }

    /**
     * Collect development components associated to the given list of activities.
     * 
     * @param activities
     *            list of activities the associated development components are to belooked up
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
     * Calculate the set of development components affected by the given activity.
     * 
     * @param activity
     *            activity to calculate affected development components for.
     * @return set of development components affected by the given activity.
     */
    private Set<DevelopmentComponent> calculateAffectedDevelopmentComponents(final Activity activity) {
        final Set<DevelopmentComponent> components = new HashSet<DevelopmentComponent>();

        try {
            final ActivityResourceParser activityResourceParser = new ActivityResourceParser(this.dcFactory, activity);

            activityResourceParser.parse(this.dtrHttpClient.getContent(this.dtrUrl + activity.getActivityUrl()));

            for (final ActivityResource resource : activity.getResources()) {
                final DevelopmentComponent developmentComponent = resource.getDevelopmentComponent();
                developmentComponent.setNeedsRebuild(true);
                components.add(developmentComponent);
            }
        }
        catch (final ClientProtocolException e) {
            LOG.log(Level.SEVERE, "There was an error calculating the affected development components for the given set of activities.", e);
        }
        catch (final IllegalStateException e) {
            LOG.log(Level.SEVERE, "There was an error calculating the affected development components for the given set of activities.", e);
        }
        catch (final IOException e) {
            LOG.log(Level.SEVERE, "There was an error calculating the affected development components for the given set of activities.", e);
        }

        return components;
    }
}
