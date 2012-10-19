/**
 * 
 */
package org.arachna.netweaver.dc.types;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Filter development components by their type.
 * 
 * @author Dirk Weigenand
 */
public final class DevelopmentComponentByTypeFilter implements IDevelopmentComponentFilter {
    /**
     * List of types of development components that will make this filter accept
     * a DC.
     */
    private final Collection<DevelopmentComponentType> dcTypes = new LinkedHashSet<DevelopmentComponentType>();

    /**
     * Create an instance of a development component filter using the given DC
     * types as filter criteria.
     * 
     * @param types
     *            development component types to use as filter criteria.
     */
    public DevelopmentComponentByTypeFilter(final DevelopmentComponentType... types) {
        for (final DevelopmentComponentType type : types) {
            dcTypes.add(type);
        }
    }

    /**
     * Accept the given development component only when its type matches one of
     * the types given when this filter instance was created.
     * 
     * @param component
     *            the component to validate against the filter criteria
     *            (development component types).
     * 
     * @return <code>true</code> when one of the development component types of
     *         this filter matches the type of the given component.
     *         <code>false</code> otherwise.
     */
    @Override
    public boolean accept(final DevelopmentComponent component) {
        return dcTypes.contains(component.getType());
    }
}
