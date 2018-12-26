/*
 *
 */
package org.arachna.netweaver.dc.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

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
     * Flag to indicate whether this build variant is required to execute prior to activation of activities.
     */
    private final boolean requiredForActivation;

    /**
     * Create a build variant with the given name.
     *
     * @param name
     *            name of build variant.
     * @param requiredForActivation
     *            <code>true</code> when this build variant shall be required for activation, <code>false</code> otherwise.
     */
    public BuildVariant(final String name, final boolean requiredForActivation) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("A build variant must have a name!");
        }

        this.name = name;
        this.requiredForActivation = requiredForActivation;
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
     * Add build option to this build variant.
     *
     * @param option
     *            new build option.
     */
    public void add(final BuildOption option) {
        addBuildOption(option.getName(), option.getValue());
    }

    /**
     * Return the value of the named build option or <code>null</code> if it does not exist.
     *
     * @param name
     *            Name of the build option asked for
     * @return the value of the named build option or <code>null</code> if it does not exist.
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
     * @return the Jdk home path it defined in build options <code>null</code> otherwise.
     */
    public String getJdkHomePath() {
        return buildOptions.get(COM_SAP_JDK_HOME_PATH_KEY);
    }

    /**
     * Indicate whether this build variant is required to execute prior to activation of activities.
     *
     * @return <code>true</code> when this build variant is required for activation, <code>false</code> otherwise.
     */
    public boolean isRequiredForActivation() {
        return requiredForActivation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(name).append(requiredForActivation);
        final List<Map.Entry<String, String>> entries = new ArrayList<Map.Entry<String, String>>(buildOptions.entrySet());

        entries.sort(new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(final Entry<String, String> arg0, final Entry<String, String> arg1) {
                return arg0.getKey().compareTo(arg1.getKey());
            }
        });

        for (final Map.Entry<String, String> buildOption : entries) {
            builder.append(buildOption.getKey()).append(buildOption.getValue());
        }

        return builder.toHashCode();
    }

    @Override
    public String toString() {
        return "BuildVariant [name=" + name + ", requiredForActivation=" + requiredForActivation + ", buildOptions=" + buildOptions + "]";
    }

    /**
     * {@inheritDoc}
     *
     * Compare with other build variant property-wise.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final BuildVariant other = (BuildVariant)obj;

        final int hashCode = hashCode();
        final int hashCode2 = other.hashCode();
        return hashCode == hashCode2;
    }

    /**
     * Merge the build options from the given build varaint into this one (only if it has the same name).
     *
     * @param variant
     *            build variant whose build options are to merge into this.
     */
    public void mergeBuildOptions(final BuildVariant variant) {
        for (final String name : variant.getBuildOptionNames()) {
            addBuildOption(name, variant.getBuildOption(name));
        }
    }
}
