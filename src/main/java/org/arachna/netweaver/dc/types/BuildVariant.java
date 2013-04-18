package org.arachna.netweaver.dc.types;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * build variant to use for a compartment.
 * 
 * @author Dirk Weigenand
 */
public final class BuildVariant {
    /**
     * constant for JDK to be used to build DCs.
     */
    public static final String COM_SAP_JDK_HOME_PATH_KEY = "com.sap.jdk.home_path_key";

    /**
     * name of this build variant.
     */
    private final String name;

    /**
     * build options to use.
     */
    private final Map<String, String> buildOptions = new HashMap<String, String>();

    /**
     * Create a build variant with the given name.
     * 
     * @param name
     *            name of build variant.
     */
    public BuildVariant(final String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("A build variant must have a name!");
        }

        this.name = name;
    }

    /**
     * Returns the name of this build variant.
     * 
     * @return the name of this build variant.
     */
    public String getName() {
        return name;
    }

    /**
     * Add an build option to this build variant.
     * 
     * @param name
     *            Name of build option
     * @param value
     *            Value of build option
     */
    public void addBuildOption(final String name, final String value) {
        buildOptions.put(name, value);
    }

    /**
     * Return the value of the named build option or <code>null</code> if it
     * does not exist.
     * 
     * @param name
     *            Name of the build option asked for
     * @return the value of the named build option or <code>null</code> if it
     *         does not exist.
     */
    public String getBuildOption(final String name) {
        return buildOptions.get(name);
    }

    /**
     * Returns the names of build options of this BuildVariant.
     * 
     * @return the names of build options of this BuildVariant.
     */
    public Collection<String> getBuildOptionNames() {
        return buildOptions.keySet();
    }

    /**
     * Get the Jdk home path from build options if it exists.
     * 
     * @return the Jdk home path it defined in build options <code>null</code>
     *         otherwise.
     */
    public String getJdkHomePath() {
        return buildOptions.get(COM_SAP_JDK_HOME_PATH_KEY);
    }
}
