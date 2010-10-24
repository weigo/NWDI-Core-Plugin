/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * A parser for {@link ActivityResource}s. Parses a DTR HTML report of a specific activity and returns the found resources.
 * 
 * @author Dirk Weigenand
 */
public final class ActivityResourceParser {
    /**
     * XPath expression to extract resources.
     */
    private static final String XPATH =
            "//a[starts-with(@href, '/dtr/system-tools/reports/ResourceDetails?technical=false&path=/vh/')]/text()";

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
    private final Pattern resourcePathPattern = Pattern.compile("^\\/.*?\\/_comp(.*?)$");

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
     *            registry to use for creating/getting development components changed in the given activity.
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

    /**
     * Parses a DTR HTML report of a specific activity and updates the activity with the found resources.
     * 
     * @param dtrActivityReportPage
     *            HTML report containing the activities resources.
     */
    public void parse(final InputStream dtrActivityReportPage) {
        final Document document = JTidyHelper.getDocument(dtrActivityReportPage);

        try {
            final DOMXPath xPath = new DOMXPath(XPATH);

            for (final Object returnValue : xPath.selectNodes(document)) {
                this.addResource((Node)returnValue);
            }
        }
        catch (final JaxenException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a {@link ActivityResource} from the given <code>Node</code>.
     * 
     * @param node
     *            node to extract the <code>ActivityResource</code> from.
     * @return the extracted <code>ActivityResource</code> or <code>null</code>.
     */
    private void addResource(final Node node) {
        final String resourcePath = node.getNodeValue();

        if (this.isResourcePathDevelopmentComponentResource(resourcePath)) {
            this.activity.add(new ActivityResource(this.activity, this.developmentComponentFactory.create(
                    this.getDevelopmentComponentName(resourcePath), this.getVendor(resourcePath)), this.getResourcePath(resourcePath)));
        }
    }

    /**
     * Checks whether the given resource path belongs to a development component (i.e. it starts with '/DCs').
     * 
     * @param resourcePath
     *            resource path to check
     * @return <code>true</code> iff the given resource path starts with '/DCs', <code>false</code> otherwise.
     */
    private boolean isResourcePathDevelopmentComponentResource(final String resourcePath) {
        return resourcePath != null && this.developmentComponentNamePattern.matcher(resourcePath).matches();
    }

    /**
     * Return the vendor encoded in the given resource path.
     * 
     * @param resourcePath
     *            path to resource the vendor should be extracted from.
     * @return vendor encoded in the given resource path.
     */
    private String getVendor(final String resourcePath) {
        return this.extractPatternMatch(this.vendorPattern, resourcePath);
    }

    /**
     * Return the development component name encoded in the given resource path.
     * 
     * @param resourcePath
     *            path to resource the development component name should be extracted from.
     * @return development component name encoded in the given resource path.
     */
    private String getDevelopmentComponentName(final String resourcePath) {
        return extractPatternMatch(this.developmentComponentNamePattern, resourcePath);
    }

    /**
     * Return the path to the respective resource encoded in the given resource path.
     * 
     * @param resourcePath
     *            path to resource the resource name should be extracted from.
     * @return path to the respective resource encoded in the given resource path.
     */
    private String getResourcePath(final String resourcePath) {
        return extractPatternMatch(this.resourcePathPattern, resourcePath);
    }

    /**
     * Extract the match from the resource path using the given pattern.
     * 
     * @param pattern
     *            pattern to use matching
     * @param resourcePath
     *            resource path used matching the pattern. * @return the found match
     */
    private String extractPatternMatch(final Pattern pattern, final String resourcePath) {
        final Matcher matcher = pattern.matcher(resourcePath);
        matcher.find();

        return matcher.group(1);
    }
}
