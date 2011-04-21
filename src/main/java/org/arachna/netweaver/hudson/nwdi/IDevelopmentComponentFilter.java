/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import org.arachna.netweaver.dc.types.DevelopmentComponent;

/**
 * Interface for filtering development components.
 *
 * @author Dirk Weigenand
 */
public interface IDevelopmentComponentFilter {
    /**
     * Filter development components.
     *
     * @param component
     *            development component to validate against constraints set in
     *            an implementing filter.
     * @return <code>true</code> when validation against constraints passes,
     *         <code>false</code> otherwise.
     */
    boolean accept(DevelopmentComponent component);
}
