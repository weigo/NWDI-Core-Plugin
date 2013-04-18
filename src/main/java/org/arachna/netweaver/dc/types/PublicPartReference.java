/**
 *
 */
package org.arachna.netweaver.dc.types;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

/**
 * Reference to a development components public part.
 * 
 * @author Dirk Weigenand
 */
public final class PublicPartReference {
    /**
     * name of development component this <code>PublicPartReference</code>
     * references.
     */
    private final String componentName;

    /**
     * vendor of referenced development components.
     */
    private final String vendor;

    /**
     * name of referenced public part.
     */
    private final String name;

    /**
     * kind of reference: at build time.
     */
    private boolean atBuildTime;

    /**
     * kind of reference: at run time.
     */
    private boolean atRunTime;

    /**
     * kind of reference: at deploy time.
     */
    private boolean atDeployTime;

    /**
     * Creates a <code>PublicPartReference</code> with the given vendor,
     * development component name and public part name.
     * 
     * @param vendor
     *            development component vendor.
     * @param componentName
     *            development component name.
     * @param name
     *            public part name.
     */
    public PublicPartReference(final String vendor, final String componentName, final String name) {
        validate(vendor, "vendor");
        validate(vendor, "componentName");

        this.vendor = vendor;
        this.componentName = componentName;
        this.name = name;
    }

    /**
     * Creates a <code>PublicPartReference</code> with the given vendor,
     * development component name and an empty public part name.
     * 
     * @param vendor
     *            development component vendor.
     * @param componentName
     *            development component name.
     */
    public PublicPartReference(final String vendor, final String componentName) {
        this(vendor, componentName, "");
    }

    /**
     * Validate the given <code>argument</code> that it is not <code>null</code>
     * or empty. Throws an {@link IllegalArgumentException} if it is.
     * 
     * @param argument
     *            the value to validate.
     * @param argumentName
     *            the name to use in the error message.
     */
    protected void validate(final String argument, final String argumentName) {
        if (argument == null || argument.trim().length() == 0) {
            throw new IllegalArgumentException(String.format("'%s' must not be null or empty!", argumentName));
        }
    }

    /**
     * @return the componentName
     */
    public String getComponentName() {
        return componentName;
    }

    /**
     * @return the vendor
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * @return the buildTime
     */
    public boolean isAtBuildTime() {
        return atBuildTime;
    }

    /**
     * @param atBuildTime
     *            the buildTime to set
     */
    public void setAtBuildTime(final boolean atBuildTime) {
        this.atBuildTime = atBuildTime;
    }

    /**
     * @return the runTime
     */
    public boolean isAtRunTime() {
        return atRunTime;
    }

    /**
     * @param atRunTime
     *            the runTime to set
     */
    public void setAtRunTime(final boolean atRunTime) {
        this.atRunTime = atRunTime;
    }

    /**
     * @return the deployTime
     */
    public boolean isAtDeployTime() {
        return atDeployTime;
    }

    /**
     * @param atDeployTime
     *            the deployTime to set
     */
    public void setAtDeployTime(final boolean atDeployTime) {
        this.atDeployTime = atDeployTime;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        final StringBuilder msg = new StringBuilder();
        msg.append(String.format("%s:%s", getVendor(), getComponentName()));

        if (!StringUtils.isEmpty(getName())) {
            msg.append(":").append(getName());
        }

        if (isAtBuildTime()) {
            msg.append(", at build time");
        }

        if (isAtRunTime()) {
            msg.append(", at run time");
        }

        if (isAtDeployTime()) {
            msg.append(", at deploy time");
        }

        return msg.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] { componentName, name, vendor });
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final PublicPartReference other = (PublicPartReference)obj;

        return hashCode() == other.hashCode();
    }
}
