/**
 *
 */
package org.arachna.netweaver.dc.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory/registry for {@link DevelopmentComponent} objects.
 * 
 * @author Dirk Weigenand
 */
public final class DevelopmentComponentFactory {
    /**
     * maps 'vendor:component.name' to development components.
     */
    private final Map<String, DevelopmentComponent> componentMap = new HashMap<String, DevelopmentComponent>();

    /**
     * Default constructor.
     */
    public DevelopmentComponentFactory() {
        super();
    }

    /**
     * Create a new <code>DevelopmentComponentFactory</code> with the given {@link DevelopmentConfiguration}. Register all development
     * components contained within the configuration with the factory.
     * 
     * @param developmentConfiguration
     *            a development configuration to initialize the registry with.
     */
    public DevelopmentComponentFactory(final DevelopmentConfiguration developmentConfiguration) {
        for (final Compartment compartment : developmentConfiguration.getCompartments()) {
            for (final DevelopmentComponent component : compartment.getDevelopmentComponents()) {
                componentMap.put(createComponentKey(component.getName(), component.getVendor()), component);
            }
        }
    }

    /**
     * Create and register a {@link DevelopmentComponent}.
     * 
     * @param vendor
     *            the vendor of the development component to register.
     * @param name
     *            the name of the development component to register.
     * @param type
     *            the type of this development component.
     * @return the newly created or already registered development component
     */
    public DevelopmentComponent create(final String vendor, final String name, final DevelopmentComponentType type) {
        DevelopmentComponent component = this.get(vendor, name);

        if (null == component) {
            component = new DevelopmentComponent(name, vendor, type);
            componentMap.put(createComponentKey(name, vendor), component);
        }

        return component;
    }

    /**
     * Create a development component using the given vendor, DC name and public parts and public part references.
     * 
     * @param vendor
     *            DC vendor
     * @param dcName
     *            DC name
     * @param publicParts
     *            public parts of created DC
     * @param references
     *            references to public parts
     * @return the created DC
     */
    public DevelopmentComponent create(final String vendor, final String dcName, final PublicPart[] publicParts,
        final PublicPartReference[] references) {
        final DevelopmentComponent component = this.create(vendor, dcName);

        for (final PublicPart pp : publicParts) {
            component.add(pp);
        }

        for (final PublicPartReference reference : references) {
            component.add(reference);
        }

        return component;
    }

    /**
     * Create the key for storing a development component in the registry.
     * 
     * @param name
     *            name to use as part of the key.
     * @param vendor
     *            vendor to use as part of the key.
     * @return the concatenation of the given vendor, ':' and the given component name.
     */
    private String createComponentKey(final String name, final String vendor) {
        return vendor + ":" + name;
    }

    /**
     * Create and register a development component. If the development component is already registered the existing object will be returned.
     * 
     * @param vendor
     *            vendor of development component.
     * @param name
     *            name of development component.
     * @return a development component with the given vendor, name and type {@link DevelopmentComponentType#unknown}.
     */
    public DevelopmentComponent create(final String vendor, final String name) {
        return this.create(vendor, name, DevelopmentComponentType.unknown);
    }

    /**
     * Return all registered development components.
     * 
     * @return a collection of all registered development components.
     */
    public Collection<DevelopmentComponent> getAll() {
        final Collection<DevelopmentComponent> components = new ArrayList<DevelopmentComponent>(componentMap.size());
        components.addAll(componentMap.values());

        return components;
    }

    /**
     * Update the using DCs for all registered DCs.
     * 
     * For each registered development component the list of public parts it references will be iterated. The respective development
     * component will be looked up and the currently worked on DC will be added to its using DCs.
     */
    public void updateUsingDCs() {
        for (final DevelopmentComponent component : componentMap.values()) {
            this.updateUsingDCs(component);
        }
    }

    /**
     * Updates the using DCs for the given DC.
     * 
     * @param root
     *            development component whose using DCs are to be updated.
     */
    private void updateUsingDCs(final DevelopmentComponent root) {
        for (final DevelopmentComponent dc : componentMap.values()) {
            final Collection<PublicPartReference> references = dc.getUsedDevelopmentComponents();

            for (final PublicPartReference reference : references) {
                if (root.getVendor().equals(reference.getVendor()) && root.getName().equals(reference.getComponentName())) {
                    root.addUsingDC(dc);

                    if (dc.getUsingDevelopmentComponents().size() == 0) {
                        this.updateUsingDCs(dc);
                    }
                }
            }
        }
    }

    /**
     * Return the development component matching the given vendor and component name.
     * 
     * @param vendor
     *            vendor of development component.
     * @param name
     *            name of development component.
     * @return the development component asked for or <code>null</code> if it is not registered.
     */
    public DevelopmentComponent get(final String vendor, final String name) {
        return componentMap.get(createComponentKey(name, vendor));
    }

    /**
     * Return the development component matching the given {@link PublicPartReference}.
     * 
     * @param ppRef
     *            reference to a development components public part.
     * @return the development component asked for or <code>null</code> if it is not registered.
     */
    public DevelopmentComponent get(final PublicPartReference ppRef) {
        return componentMap.get(createComponentKey(ppRef.getComponentName(), ppRef.getVendor()));
    }

    /**
     * Remove a development component from this registry.
     * 
     * @param component
     *            the development component to be removed from this registry.
     */
    public void remove(final DevelopmentComponent component) {
        componentMap.remove(createComponentKey(component.getName(), component.getVendor()));
        final Compartment compartment = component.getCompartment();

        if (compartment != null) {
            compartment.remove(component);
        }
    }
}
