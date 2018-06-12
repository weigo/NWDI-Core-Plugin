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
     */
    @Override
    public int compare(final DevelopmentComponent component1, final DevelopmentComponent component2) {
        return component1.getName().compareTo(component2.getName());
    }
}
