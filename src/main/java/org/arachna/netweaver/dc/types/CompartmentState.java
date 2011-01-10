/**
 *
 */
package org.arachna.netweaver.dc.types;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration for statuus of a {@link Compartment} objects.
 * 
 * @author Dirk Weigenand
 */
public enum CompartmentState {
    /**
     * initialize states.
     */
    Source("source state"), Archive("archive state");

    /**
     * Map for acceptable Compartment states.
     */
    private static final Map<String, CompartmentState> STATES = new HashMap<String, CompartmentState>();

    static {
        for (final CompartmentState type : values()) {
            STATES.put(type.toString(), type);
        }
    }

    /**
     * Status.
     */
    private String state;

    /**
     * constructor with state name.
     * 
     * @param state
     *            name of compartment state
     */
    CompartmentState(final String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return this.state;
    }

    /**
     * Factory method for CompartmentStates. Throws an
     * {@link IllegalArgumentException} when the given state name cannot be
     * found.
     * 
     * @param stateName
     *            name of state to instantiate
     * @return CompartmentState for given stateName
     */
    public static CompartmentState fromString(final String stateName) {
        final CompartmentState state = STATES.get(stateName);

        if (state == null) {
            throw new IllegalArgumentException("Illegal component state: '" + stateName + "'!");
        }

        return state;
    }
}
