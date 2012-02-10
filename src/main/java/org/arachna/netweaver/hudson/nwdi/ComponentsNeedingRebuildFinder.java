/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.arachna.netweaver.dc.types.DevelopmentComponent;

/**
 * Calculate transitive hull for development components that need rebuilding.
 * 
 * @author Dirk Weigenand
 */
public final class ComponentsNeedingRebuildFinder {
    /**
     * Set of components needing a rebuild.
     */
    private final Set<DevelopmentComponent> componentsNeedRebuilding = new HashSet<DevelopmentComponent>();

    /**
     * From the given collection of development components find any dependent
     * development components that have to be rebuilt, i.e. calculate the
     * transitive hull of the given DCs wrt. to rebuilding.
     * 
     * @param base
     *            a collection of development components that are associated to
     *            activities since the last build.
     * @return a collection of development components needing a rebuild.
     */
    public Collection<DevelopmentComponent> calculateDevelopmentComponentsThatNeedRebuilding(
        final Collection<DevelopmentComponent> base) {
        this.componentsNeedRebuilding.clear();

        for (final DevelopmentComponent component : base) {
            calculateDevelopmentComponentsThatNeedRebuilding(component);
        }

        return this.componentsNeedRebuilding;
    }

    /**
     * Update the <code>needsRebuild</code> property recursively for the given
     * components using DCs.
     * 
     * @param component
     *            the development component whose using DCs have to be set up as
     *            needing a rebuild.
     */
    private void calculateDevelopmentComponentsThatNeedRebuilding(final DevelopmentComponent component) {
        if (!this.componentsNeedRebuilding.contains(component)) {
            this.componentsNeedRebuilding.add(component);

            for (final DevelopmentComponent usingDC : component.getUsingDevelopmentComponents()) {
                calculateDevelopmentComponentsThatNeedRebuilding(usingDC);
            }
        }
    }
}
