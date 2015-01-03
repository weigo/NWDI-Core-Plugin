/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.arachna.netweaver.dc.types.DevelopmentComponent;

/**
 * Result of a topological sort on development components to be built.
 *
 * @author Dirk Weigenand
 */
public class TopoSortResult {
    /**
     * development components in build order.
     */
    private final Collection<DevelopmentComponent> components = new LinkedList<DevelopmentComponent>();

    /**
     * list of components with circular dependencies.
     */
    private final Collection<CircularDependency> circularDependencies = new LinkedList<CircularDependency>();

    /**
     * Add the given development component to list of development components in build order.
     *
     * @param component
     *            development component to add.
     */
    public void add(final DevelopmentComponent component) {
        components.add(component);
    }

    /**
     * Get the collection of development components in build order.
     *
     * @return collection of development components in build order.
     */
    public Collection<DevelopmentComponent> getDevelopmentComponents() {
        return Collections.unmodifiableCollection(components);
    }

    /**
     * Add the given pair of development components as circular dependency to this <code>TopoSortResult</code>.
     *
     * @param component
     *            development component to be added as having a circular dependency.
     * @param dependency
     *            development component that is a circular dependency to the first argument
     */
    public void add(final DevelopmentComponent component, final DevelopmentComponent dependency) {
        circularDependencies.add(new CircularDependency(component, dependency));
    }

    /**
     * Get the list of circular dependencies contained in this <code>TopoSortResult</code>.
     *
     * @return list of circular dependencies contained in this <code>TopoSortResult</code>.
     */
    public Collection<CircularDependency> getCircularDependencies() {
        return Collections.unmodifiableCollection(circularDependencies);
    }

    /**
     * A circular dependency.
     *
     * @author Dirk Weigenand
     */
    public static class CircularDependency {
        /**
         * development component having a circular dependency.
         */
        private final DevelopmentComponent component;

        /**
         * development component being a circular dependency of the other DC.
         */
        private final DevelopmentComponent dependency;

        /**
         * Create an instance of a <code>CircularDependency</code>.
         *
         * @param component
         *            development component having a circular dependency.
         * @param dependency
         *            development component being a circular dependency of the other DC.
         */
        public CircularDependency(final DevelopmentComponent component, final DevelopmentComponent dependency) {
            this.component = component;
            this.dependency = dependency;
        }

        /**
         * @return the component
         */
        public DevelopmentComponent getComponent() {
            return component;
        }

        /**
         * @return the dependency
         */
        public DevelopmentComponent getDependency() {
            return dependency;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (component == null ? 0 : component.hashCode());
            result = prime * result + (dependency == null ? 0 : dependency.hashCode());
            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final CircularDependency other = (CircularDependency)obj;
            if (component == null) {
                if (other.component != null) {
                    return false;
                }
            }
            else if (!component.equals(other.component)) {
                return false;
            }
            if (dependency == null) {
                if (other.dependency != null) {
                    return false;
                }
            }
            else if (!dependency.equals(other.dependency)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "CircularDependency [component=" + component + ", dependency=" + dependency + "]";
        }
    }

    /**
     * Determine whether the given component is already recorded as having a circular dependency.
     *
     * @param component
     *            development component to check for having circular dependencies.
     * @return <code>true</code>, when a circular dependency for the given component has already be recorded, <code>false</code> otherwise.
     */
    public boolean hasCircularDependency(final DevelopmentComponent component) {
        for (final CircularDependency dependency : circularDependencies) {
            if (dependency.component.equals(component)) {
                return true;
            }
        }

        return false;
    }
}
