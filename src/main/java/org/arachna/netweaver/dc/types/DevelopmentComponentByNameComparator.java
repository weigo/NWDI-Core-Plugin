/**
 *
 */
package org.arachna.netweaver.dc.types;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Compare two development components lexicographically by name.
 * 
 * @author Dirk Weigenand
 */
public final class DevelopmentComponentByNameComparator implements Comparator<DevelopmentComponent>, Serializable {

    /**
     * UID for serialization.
     */
    private static final long serialVersionUID = 7243723420903771935L;

    /**
     * Compare two development components lexicographically with respect to
     * their names.
     * 
     * @param component1
     *            the first development component to use in comparison.
     * @param component2
     *            the second development component to use in comparison.
     * @return <code>-1</code> if the name of <code>component1</code> is
     *         lexicographically smaller than that of <code>component2</code>,
     *         <code>0</code> if both are equal and <code>1</code> if the name
     *         of <code>component1</code> is lexicographically greater than that
     *         of <code>component2</code>.
     */
    public int compare(final DevelopmentComponent component1, final DevelopmentComponent component2) {
        return StringComparatorResultMapper.mapResultToSpecificationValues(component1.getName().compareTo(component2.getName()));
    }
}
