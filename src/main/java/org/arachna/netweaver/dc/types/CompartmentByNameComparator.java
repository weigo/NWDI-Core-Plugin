/**
 *
 */
package org.arachna.netweaver.dc.types;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator for {@link Compartment}s. Compares them lexicographically by name.
 * 
 * @author Dirk Weigenand
 */
public final class CompartmentByNameComparator implements Comparator<Compartment>, Serializable {

    /**
     * UID for serialization.
     */
    private static final long serialVersionUID = 4165208906771704837L;

    /**
     * Compare two compartments lexicographically wrt. to their names.
     */
    @Override
    public int compare(final Compartment compartment1, final Compartment compartment2) {
        int result = 0;

        if (compartment1 != null && compartment2 != null) {
            result = compartment1.getName().compareTo(compartment2.getName());
        }
        else if (compartment1 != null) {
            result = 1;
        }
        else if (compartment2 != null) {
            result = -1;
        }

        return result;
    }
}
