/**
 *
 */
package org.arachna.netweaver.dc.types;

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
        return state;
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
        for (final CompartmentState state : values()) {
            if (state.state.equals(stateName)) {
                return state;
            }
        }

        throw new IllegalArgumentException("Illegal component state: '" + stateName + "'!");
    }
}
