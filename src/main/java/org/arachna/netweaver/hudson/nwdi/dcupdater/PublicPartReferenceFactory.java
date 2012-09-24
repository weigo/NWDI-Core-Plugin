/**
 * 
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import java.util.HashMap;
import java.util.Map;

import org.arachna.netweaver.dc.types.PublicPartReference;

/**
 * Factory for creating public part references.
 * 
 * @author Dirk Weigenand
 */
final class PublicPartReferenceFactory {
    /**
     * vendor sap.com.
     */
    private static final String SAP_COM = "sap.com";

    /**
     * String dividing vendor from component name.
     */
    private static final String FORWARD_SLASH = "/";

    /**
     * blacklist for DCs missing the vendor.
     */
    private final Map<String, String> blackList = new HashMap<String, String>();

    /**
     * Create a new instance of <code>PublicPartReferenceFactory</code> and
     * initialize blacklist for known DCs missing a vendor.
     */
    PublicPartReferenceFactory() {
        blackList.put("tc/wd/wslib", SAP_COM);
        blackList.put("tc/kmc/bc.uwl/api", SAP_COM);
        blackList.put("tc/sec/wssec/service", SAP_COM);
    }

    /**
     * Get the index where the vendor prefix ends.
     * 
     * @param reference
     *            public part reference read from configuration file.
     * @return index where the vendor prefix ends.
     */
    private int getVendorSeparationIndex(final String reference) {
        final int tildeIndex = reference.indexOf("~");
        final int slashIndex = reference.indexOf(FORWARD_SLASH);
        final boolean slashIsVendorSeparatorCharacter =
            slashIndex > -1 && slashIndex == reference.lastIndexOf(FORWARD_SLASH);
        int index = -1;

        if (slashIsVendorSeparatorCharacter) {
            index = slashIndex;
        }
        else if (tildeIndex == -1 || slashIndex == -1) {
            index = Math.max(tildeIndex, slashIndex);
        }
        else {
            index = Math.min(tildeIndex, slashIndex);
        }

        return index;
    }

    /**
     * Create a {@link PublicPartReference} and add it to the internal
     * collection of public part references read from the configuration file.
     * 
     * @param reference
     *            public part references read from the configuration file.
     */
    PublicPartReference create(final String reference) {
        PublicPartReference ppReference = null;
        final String vendor = getVendor(reference);

        if (vendor != null) {
            ppReference = new PublicPartReference(vendor, getLibrary(reference));
            ppReference.setAtRunTime(true);
        }
        else if (getVendorSeparationIndex(reference) > -1) {
            ppReference = new PublicPartReference(vendor, getLibrary(reference));
            ppReference.setAtRunTime(true);
        }

        return ppReference;
    }

    private String getLibrary(final String reference) {
        final String vendor = blackList.get(reference);
        String library = reference;

        if (vendor == null) {
            final int index = getVendorSeparationIndex(reference);
            library = reference.substring(index + 1).replace('~', '/');
        }

        return library;
    }

    private String getVendor(final String reference) {
        String vendor = blackList.get(reference);

        if (vendor == null) {
            final int index = getVendorSeparationIndex(reference);
            vendor = index > -1 ? reference.substring(0, index) : null;
        }

        return vendor;
    }
}
