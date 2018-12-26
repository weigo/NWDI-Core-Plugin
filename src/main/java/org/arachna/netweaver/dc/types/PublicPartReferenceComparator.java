/**
 *
 */
package org.arachna.netweaver.dc.types;

import java.io.Serializable;
import java.util.Comparator;

/**
 * {@link Comparator} for {@link PublicPartReference} objects. Compares them by
 * name.
 * 
 * @author Dirk Weigenand
 */
public final class PublicPartReferenceComparator implements Comparator<PublicPartReference>, Serializable {

    /**
     * UID for serialization.
     */
    private static final long serialVersionUID = 8975003703117837636L;

    /**
     * Compare two {@link PublicPartReference} objects wrt. their vendor,
     * referenced component and public part name (in that order).
     * 
     * @param o1
     *            first reference for comparison
     * @param o2
     *            second reference for comparison
     * @return {@link Comparator#compare(Object, Object)}
     */
    public int compare(final PublicPartReference o1, final PublicPartReference o2) {
        int result = o1.getVendor().compareTo(o2.getVendor());

        if (result == 0) {
            result = o1.getComponentName().compareTo(o2.getComponentName());

            if (result == 0) {
                result = o1.getName().compareTo(o2.getName());
            }
        }

        return result;
    }
}
