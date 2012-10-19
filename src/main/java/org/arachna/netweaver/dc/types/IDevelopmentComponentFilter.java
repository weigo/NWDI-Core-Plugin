/**
 * 
 */
package org.arachna.netweaver.dc.types;

/**
 * Interface for generic filter for development components.
 * 
 * @author Dirk Weigenand
 */
public interface IDevelopmentComponentFilter {
    /**
     * Inspect a development component whether it meets some criteria to be
     * determined by implementors of this interface.
     * 
     * @param component
     *            development component to be inspected.
     * @return <code>true</code> when the criteria of the implemented filter are
     *         met, <code>false</code> otherwise.
     */
    boolean accept(DevelopmentComponent component);
}
