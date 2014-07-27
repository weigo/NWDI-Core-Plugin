/**
 * 
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import org.apache.commons.lang.StringUtils;

/**
 * Enum for types of portal application (Private)SharingReferences.
 * 
 * @author Dirk Weigenand
 */
enum SharingReferencePrefix {
    /**
     * prefix for a sharing reference to a service.
     */
    Service("service:"),

    /**
     * prefix for a sharing reference to a library.
     */
    Library("library:"),

    /**
     * prefix for a sharing reference to an interface.
     */
    Interface("interface::");

    /**
     * prefix.
     */
    private final String prefix;

    /**
     * Create a new prefix instance.
     * 
     * @param prefix
     *            the prefix to use for an interface, service or library.
     */
    SharingReferencePrefix(final String prefix) {
        this.prefix = "SAPJ2EE::" + StringUtils.trimToEmpty(prefix);
    }

    /**
     * Extract the reference to a service, interface, library or application by
     * removing the corresponding prefix iff it exists.
     * 
     * @param reference
     *            sharing reference from portalapp.xml.
     * @return the cleaned up reference
     */
    public static String getReference(final String reference) {
        for (final SharingReferencePrefix prefix : values()) {
            if (reference.startsWith(prefix.prefix)) {
                return reference.substring(prefix.prefix.length());
            }
        }

        return reference;
    }

    /**
     * Return the prefix for this SharingReferencePrefix.
     * 
     * @return
     */
    public String getPrefix() {
        return prefix;
    }
}