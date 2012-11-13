/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.w3c.dom.Node;

/**
 * A parser for {@link ActivityResource}s. Parses a DTR HTML report of a
 * specific activity and returns the found resources.
 * 
 * @author Dirk Weigenand
 */
public final class ActivityResourceParser extends AbstractResourceParser {
    /**
     * XPath expression to extract resources.
     */
    private static final String XPATH =
        "//a[starts-with(@href, '/dtr/system-tools/reports/ResourceDetails?') and contains(@href, 'path=/vh/')]";

    /**
     * Pattern matching the resources ID.
     */
    private final Pattern resourceIdPattern = Pattern.compile("^.*?\\/vh\\/(.*?)$");

    /**
     * Pattern matching the vendor.
     */
    private final Pattern vendorPattern = Pattern.compile("^\\/DCs\\/(.*?)\\/.*?$");

    /**
     * Pattern matching the development component name.
     */
    private final Pattern developmentComponentNamePattern = Pattern.compile("^\\/DCs\\/.*?\\/(.*?)\\/_comp\\/.*?$");

    /**
     * Pattern matching the development component name.
     */
    private final Pattern resourcePathPattern = Pattern.compile("^\\/.*?\\/_comp\\/(.*?)$");

    /**
     * Registry for development components.
     */
    private final DevelopmentComponentFactory developmentComponentFactory;

    /**
     * Activity to associate parsed resources with.
     */
    private final Activity activity;

    /**
     * Create an instance of an <code>ActivityResourceParser</code>.
     * 
     * @param developmentComponentFactory
     *            registry to use for creating/getting development components
     *            changed in the given activity.
     * @param activity
     *            activity to associate with the parsed resources.
     */
    public ActivityResourceParser(final DevelopmentComponentFactory developmentComponentFactory, final Activity activity) {
        if (developmentComponentFactory == null) {
            throw new IllegalArgumentException("developmentcomponentFactory must not be null!");
        }

        if (activity == null) {
            throw new IllegalArgumentException("Activity must not be null!");
        }

        this.developmentComponentFactory = developmentComponentFactory;
        this.activity = activity;
    }

    @Override
    String getXPath() {
        return XPATH;
    }

    /**
     * Parses a DTR HTML report of a specific activity and updates the activity
     * with the found resources.
     * 
     * @param nodes
     *            DOM nodes representing the extracted resources.
     */
    @Override
    public void parseInternal(final List<Object> nodes) {
        for (final Object returnValue : nodes) {
            addResource((Node)returnValue);
        }
    }

    /**
     * Create a {@link ActivityResource} from the given <code>Node</code>.
     * 
     * @param node
     *            node to extract the <code>ActivityResource</code> from.
     */
    private void addResource(final Node node) {
        final String resourcePath = node.getChildNodes().item(0).getNodeValue();

        if (isResourcePathDevelopmentComponentResource(resourcePath)) {
            final DevelopmentComponent component =
                developmentComponentFactory.create(getVendor(resourcePath), getDevelopmentComponentName(resourcePath));
            final ActivityResource resource =
                new ActivityResource(activity, component, getResourcePath(resourcePath), getResourceId(node
                    .getAttributes().getNamedItem("href").getNodeValue()));
            activity.add(resource);
        }
    }

    /**
     * Checks whether the given resource path belongs to a development component
     * (i.e. it starts with '/DCs').
     * 
     * @param resourcePath
     *            resource path to check
     * @return <code>true</code> iff the given resource path starts with '/DCs',
     *         <code>false</code> otherwise.
     */
    private boolean isResourcePathDevelopmentComponentResource(final String resourcePath) {
        return resourcePath != null && developmentComponentNamePattern.matcher(resourcePath).matches();
    }

    /**
     * Return the vendor encoded in the given resource path.
     * 
     * @param resourcePath
     *            path to resource the vendor should be extracted from.
     * @return vendor encoded in the given resource path.
     */
    private String getVendor(final String resourcePath) {
        return extractPatternMatch(vendorPattern, resourcePath);
    }

    /**
     * Return the development component name encoded in the given resource path.
     * 
     * @param resourcePath
     *            path to resource the development component name should be
     *            extracted from.
     * @return development component name encoded in the given resource path.
     */
    private String getDevelopmentComponentName(final String resourcePath) {
        return extractPatternMatch(developmentComponentNamePattern, resourcePath);
    }

    /**
     * Return the path to the respective resource encoded in the given resource
     * path.
     * 
     * @param resourcePath
     *            path to resource the resource name should be extracted from.
     * @return path to the respective resource encoded in the given resource
     *         path.
     */
    private String getResourcePath(final String resourcePath) {
        return extractPatternMatch(resourcePathPattern, resourcePath);
    }

    /**
     * Gets the ID of this resource from the given resource path.
     * 
     * @param href
     *            the resource path in the report.
     * @return the resource ID
     */
    private String getResourceId(final String href) {
        return extractPatternMatch(resourceIdPattern, href);
    }

    /**
     * Extract the match from the resource path using the given pattern.
     * 
     * @param pattern
     *            pattern to use matching
     * @param resourcePath
     *            resource path used matching the pattern.
     * @return the found match
     */
    private String extractPatternMatch(final Pattern pattern, final String resourcePath) {
        final Matcher matcher = pattern.matcher(resourcePath);
        matcher.find();

        return matcher.group(1);
    }
}
