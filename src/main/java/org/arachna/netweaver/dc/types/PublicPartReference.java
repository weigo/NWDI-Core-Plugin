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
public class PublicPartReference {
    /**
     * name of development component this <code>PublicPartReference</code> references.
     */
    private final String componentName;

    /**
     * vendor of referenced development components.
     */
    private final String vendor;

    /**
     * name of referenced public part.
     */
    private String name;

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
     * hash code of this public part reference.
     */
    private final int hashCode;

    /**
     * Creates a <code>PublicPartReference</code> with the given vendor, development component name and public part name.
     *
     * @param vendor
     *            development component vendor.
     * @param componentName
     *            development component name.
     * @param name
     *            public part name.
     */
    public PublicPartReference(final String vendor, final String componentName, final String name) {
        this.vendor = vendor;
        this.componentName = componentName;
        this.name = name;
        hashCode = Arrays.hashCode(new Object[] { componentName, getName(), vendor });
    }

    /**
     * Creates a <code>PublicPartReference</code> with the given vendor, development component name and an empty public part name.
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
    public void setAtBuildTime(final Boolean atBuildTime) {
        this.atBuildTime = atBuildTime;
    }

    /**
     * Set property at build time to true.
     */
    public void setAtBuildTime() {
        atBuildTime = true;
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
    public void setAtRunTime(final Boolean atRunTime) {
        this.atRunTime = atRunTime;
    }

    /**
     * Set property at run time to true.
     */
    public void setAtRunTime() {
        atRunTime = true;
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

    /**
     * Set the name of public part this reference points to.
     *
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
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

    @Override
    public int hashCode() {
        return hashCode;
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

        return hashCode() == obj.hashCode();
    }

    /**
     * Check whether this public part reference is a reference to the given development component.
     *
     * @param component
     *            the development component to check
     * @return <code>true</code>, when this public part reference is a reference to the given development component, <code>false</code>
     *         otherwise.
     */
    public boolean references(final DevelopmentComponent component) {
        return getVendor().equals(component.getVendor()) && getComponentName().equals(component.getName());
    }
}
