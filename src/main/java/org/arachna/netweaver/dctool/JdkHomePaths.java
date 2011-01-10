/**
 *
 */
package org.arachna.netweaver.dctool;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapping of JDKs to their respective installation directory.
 * 
 * @author Dirk Weigenand
 */
public final class JdkHomePaths {
    /**
     * suffix of a jre installation.
     */
    private static final String JRE_SUFFIX = "jre";

    /**
     * system property for 'java.home'.
     */
    private static final String JAVA_HOME = "java.home";

    /**
     * path mappings for installed JDKs.
     */
    private final Map<JdkHomeAlias, String> paths = new HashMap<JdkHomeAlias, String>();

    /**
     * Add a path for the given alias.
     * 
     * @param alias
     *            the JDK alias
     * @param path
     *            the path for the alias.
     */
    public void add(final JdkHomeAlias alias, final String path) {
        this.paths.put(alias, path);
    }

    /**
     * Return the configured path for the given alias.
     * 
     * @param alias
     *            the alias the path for is requested.
     * @return the configured path for the given alias or <code>null</code>.
     */
    public String get(final JdkHomeAlias alias) {
        String path = this.paths.get(alias);

        if (path == null) {
            final String jdkHome = System.getProperty(JAVA_HOME);
            path =
                jdkHome.endsWith(JRE_SUFFIX) ? jdkHome.substring(0, jdkHome.length() - JRE_SUFFIX.length() - 1)
                    : jdkHome;
        }

        return path;
    }
}
