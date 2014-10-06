/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.PublicPartReference;

/**
 * Sort a collection of development components topologically wrt. their dependency relations.
 *
 * @author Dirk Weigenand
 */
public class TopoSort {
    /**
     * registry/factory for development components.
     */
    private final DevelopmentComponentFactory dcFactory;

    /**
     * Logger to console.
     */
    private final PrintStream logger;

    /**
     * Create an instance of the TopoSort class using the given registry/factory for development components.
     *
     * @param dcFactory
     *            registry/factory for development components.
     */
    public TopoSort(final DevelopmentComponentFactory dcFactory, final PrintStream logger) {
        this.dcFactory = dcFactory;
        this.logger = logger;
    }

    /**
     * Sort the development components to be built topologically (determine build their order).
     *
     * @return the result of the topological sort containing the development components in build order. The result object will also contain
     *         circular dependencies between the components.
     */
    public TopoSortResult sort(final Collection<DevelopmentComponent> components) {
        final TopoSortResult topoSortResult = new TopoSortResult();
        final Map<String, ComponentWrapper> itemMap = getComponentsToRebuild(components);
        topoSort(topoSortResult, itemMap, new ArrayList<ComponentWrapper>(itemMap.values()));

        return topoSortResult;
    }

    /**
     * Do a topological sort wrt. to dependencies on the given list of items (wrapper around {@link DevelopmentComponent} to facilitate the
     * removal of dependencies for the purpose of sorting). Store the result of the sort in the {@link TopoSortResult}. If there are still
     * items in the list when no progress can be made on the sort, extract circular dependencies and report those in the given
     * {@link TopoSortResult} parameter.
     *
     * @param topoSortResult
     *            Collector object for results of the topological sort and circular dependencies.
     * @param itemMap
     *            a map of all items for reference.
     * @param items
     *            list of items to be sorted topologically.
     */
    private void topoSort(final TopoSortResult topoSortResult, final Map<String, ComponentWrapper> itemMap,
        final List<ComponentWrapper> items) {
        final ListIterator<ComponentWrapper> iterator = items.listIterator();
        final int oldSize = topoSortResult.getDevelopmentComponents().size();

        while (iterator.hasNext()) {
            final ComponentWrapper item = iterator.next();

            if (!item.hasUsedDCs()) {
                final DevelopmentComponent component = item.getComponent();
                topoSortResult.add(component);
                iterator.remove();

                for (final DevelopmentComponent usingDC : item.getUsingDCs()) {
                    itemMap.get(getComponentName(usingDC)).removeUsedDC(component);
                }
            }
        }

        if (oldSize == topoSortResult.getDevelopmentComponents().size()) {
            findCircularDependencies(topoSortResult, itemMap, items);
        }
        else {
            if (!items.isEmpty()) {
                topoSort(topoSortResult, itemMap, items);
            }
        }
    }

    /**
     * Create key for item map from vendor and component name.
     *
     * @param usingDC
     *            development component to create key from.
     * @return vendor + ":" + name of development component.
     */
    private String getComponentName(final DevelopmentComponent usingDC) {
        return usingDC.getVendor() + ":" + usingDC.getName();
    }

    /**
     * Find circular dependencies in the given list of items.
     *
     * @param topoSortResult
     *            collector object to report the found circular dependencies to.
     * @param itemMap
     *            map of names to item for looking up items by name.
     * @param items
     *            list of items to inspect for circular dependencies.
     */
    private void findCircularDependencies(final TopoSortResult topoSortResult, final Map<String, ComponentWrapper> itemMap,
        final List<ComponentWrapper> items) {
        for (final ComponentWrapper item : items) {
            findRecursivelyInUsedDCs(0, item.getComponent(), item, itemMap, topoSortResult, new HashSet<DevelopmentComponent>());
        }
    }

    /**
     * Inspect the given item whether the given component is contained in the used DCs of the item. Skip the visit if the given item has
     * been visited or the component is marked as having a circular dependency already .
     *
     * @param component
     *            development component that is to be checked for having circular dependencies.
     * @param parent
     *            development component (wrapper) that is to be searched for usage relations with the given component.
     * @param itemMap
     *            name -> item map for lookup of items by name.
     * @param topoSortResult
     *            result collector to report circular dependencies to.
     * @param visitedParents
     *            collector object to avoid multiple visits on items (and infinite recursion aka StackOverflowError).
     */
    private void findRecursivelyInUsedDCs(final int depth, final DevelopmentComponent component, final ComponentWrapper parent,
        final Map<String, ComponentWrapper> itemMap, final TopoSortResult topoSortResult,
        final Collection<DevelopmentComponent> visitedParents) {
        // System.err.println(String.format("%d: %s:%s %s:%s", depth, component.getVendor(), component.getName(), parent.getComponent()
        // .getVendor(), parent.getComponent().getName()));

        if (topoSortResult.hasCircularDependency(component) || visitedParents.contains(parent.getComponent())) {
            return;
        }

        visitedParents.add(parent.getComponent());

        if (parent.getUsedDCs().contains(component)) {
            topoSortResult.add(component, parent.getComponent());
        }

        for (final DevelopmentComponent usedDC : parent.getUsedDCs()) {
            final String componentName = getComponentName(usedDC);
            final ComponentWrapper dependency = itemMap.get(componentName);

            if (dependency != null) {
                // dependency = new ComponentWrapper(usedDC, createListOfUsedDCs(usedDC));
                findRecursivelyInUsedDCs(depth + 1, component, dependency, itemMap, topoSortResult, visitedParents);
            }
        }
    }

    /**
     * Calculate development components that will need to be rebuilt and build a mapping of name to item wrapper.
     *
     * @param componentsToRebuild
     *            list of components that should be rebuilt.
     * @return mapping of name to item wrapper of components (and components depending on them) need to be rebuilt.
     */
    private Map<String, ComponentWrapper> getComponentsToRebuild(final Collection<DevelopmentComponent> componentsToRebuild) {
        final Map<String, ComponentWrapper> components = new HashMap<String, ComponentWrapper>();

        for (final DevelopmentComponent dc : componentsToRebuild) {
            calculateDevelopmentComponentsThatNeedRebuilding(components, dc);
        }

        return components;
    }

    /**
     * Update the <code>needsRebuild</code> property recursively for the given components using DCs.
     *
     * @param components
     *            map of components processed so far.
     * @param component
     *            the development component whose using DCs have to be set up as needing a rebuild.
     */
    private void calculateDevelopmentComponentsThatNeedRebuilding(final Map<String, ComponentWrapper> components,
        final DevelopmentComponent component) {
        if (component.getCompartment() != null && !components.containsKey(getComponentName(component))) {
            component.setNeedsRebuild(true);
            final ComponentWrapper i = new ComponentWrapper(component, createListOfUsedDCs(component));
            components.put(i.getName(), i);

            for (final DevelopmentComponent usingDC : i.getUsingDCs()) {
                calculateDevelopmentComponentsThatNeedRebuilding(components, usingDC);
            }
        }
    }

    /**
     * Create a collection of used DCs from the public part references of the given component.
     *
     * @param component
     *            the component whose referenced development components are requested.
     * @return collection of used DCs.
     */
    private Collection<DevelopmentComponent> createListOfUsedDCs(final DevelopmentComponent component) {
        final Collection<DevelopmentComponent> usedDCs = new HashSet<DevelopmentComponent>(component.getUsedDevelopmentComponents().size());

        for (final PublicPartReference reference : component.getUsedDevelopmentComponents()) {
            final DevelopmentComponent e = dcFactory.get(reference);

            if (e != null && e.getCompartment().isSourceState()) {
                usedDCs.add(e);
            }
        }

        return usedDCs;
    }

    /**
     * Wrapper around a development component (used for performance reasons).
     *
     * @author Dirk Weigenand
     */
    private static final class ComponentWrapper implements Comparable<ComponentWrapper> {
        /**
         * Combination of vendor and component name.
         */
        private final String name;

        /**
         * the component itself.
         */
        private final DevelopmentComponent component;

        /**
         * components using this component.
         */
        private final Collection<DevelopmentComponent> usingDCs = new HashSet<DevelopmentComponent>();

        /**
         * components used by this component.
         */
        private final Collection<DevelopmentComponent> usedDCs = new HashSet<DevelopmentComponent>();

        /**
         * Create a new wrapper instance around the given component.
         *
         * @param component
         *            the component to wrap.
         * @param usedDCs
         *            components used by this DC so that they don't need to be recomputed from the public part reference.
         */
        ComponentWrapper(final DevelopmentComponent component, final Collection<DevelopmentComponent> usedDCs) {
            this.component = component;
            name = component.getVendor() + ":" + component.getName();

            usingDCs.addAll(component.getUsingDevelopmentComponents());
            this.usedDCs.addAll(usedDCs);
        }

        /**
         * @return the usedDCs
         */
        public boolean hasUsedDCs() {
            return !usedDCs.isEmpty();
        }

        /**
         *
         * @param usedDC
         */
        public void removeUsedDC(final DevelopmentComponent usedDC) {
            if (!usedDCs.remove(usedDC)) {
                System.err.println(String.format("Could not remove %s:%s from %s!", usedDC.getVendor(), usedDC.getName(), getName()));
            }
        }

        /**
         * @return the usedDCs
         */
        public Collection<DevelopmentComponent> getUsingDCs() {
            return component.getUsingDevelopmentComponents();
        }

        /**
         * @return the component
         */
        public DevelopmentComponent getComponent() {
            return component;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        @Override
        public int hashCode() {
            return component == null ? 0 : component.hashCode();
        }

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
            final ComponentWrapper other = (ComponentWrapper)obj;
            if (component == null) {
                if (other.component != null) {
                    return false;
                }
            }

            return !component.equals(other.component);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "Item [name=" + name + "]\n";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compareTo(final ComponentWrapper o) {
            return getName().compareTo(o.getName());
        }

        /**
         * @return the usedDCs
         */
        public Collection<DevelopmentComponent> getUsedDCs() {
            return usedDCs;
        }
    }
}
