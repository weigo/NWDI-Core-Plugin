/**
 *
 */
package org.arachna.netweaver.dc.types;

/**
 * A reference to a compartment.
 * 
 * @author G526521
 */
public final class CompartmentReference {
    /**
     * Name of referenced compartment.
     */
    private final String name;

    /**
     * Vendor of referenced compartment.
     */
    private String vendor;

    /**
     * Create an instance of a <code>CompartmentReference</code>.
     * 
     * @param name
     *            name of referenced compartment.
     * @param vendor
     *            vendor of referenced compartment.
     */
    public CompartmentReference(final String name, final String vendor) {
        this.name = name;
    }

    /**
     * Return the name of the referenced compartment.
     * 
     * @return the name of the referenced compartment.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Return the vendor of the referenced compartment.
     * 
     * @return the vendor of the referenced compartment.
     */
    public String getVendor() {
        return this.vendor;
    }
}
