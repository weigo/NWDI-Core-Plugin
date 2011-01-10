package org.arachna.netweaver.dc.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A NetWeaver development component.
 * 
 * @author Dirk Weigenand
 */
public final class DevelopmentComponent {
    /**
     * name of development component.
     */
    private String name;

    /**
     * vendor name.
     */
    private String vendor;

    /**
     * type of this development component.
     */
    private DevelopmentComponentType type;

    /**
     * compartment this development component belongs to.
     */
    private Compartment compartment;

    /**
     * description of development component.
     */
    private String description = "";

    /**
     * determines whether this DC has to be rebuilt.
     */
    private boolean needsRebuild;

    /**
     * contains the public parts of this development component if any.
     */
    private final Set<PublicPart> publicParts = new HashSet<PublicPart>();

    /**
     * contains references to development components used by this development
     * component.
     */
    private final Set<PublicPartReference> usedComponents = new HashSet<PublicPartReference>();

    /**
     * collection of development components that use this DC.
     */
    private final Set<DevelopmentComponent> usingComponents = new HashSet<DevelopmentComponent>();

    /**
     * folders containing sources.
     */
    private final Set<String> sourceFolders = new HashSet<String>();

    /**
     * Create an instance of a development component with the given name, vendor
     * and DC type.
     * 
     * @param name
     *            name of this development component.
     * @param vendor
     *            vendor of this development component.
     * @param type
     *            type of this development component.
     */
    public DevelopmentComponent(final String name, final String vendor, final DevelopmentComponentType type) {
        this.name = name;
        this.vendor = vendor;
        this.type = type;
    }

    /**
     * Create an instance of a development component with the given name, vendor
     * and DC type {@link DevelopmentComponentType#unknown}.
     * 
     * @param name
     *            name of this development component.
     * @param vendor
     *            vendor of this development component.
     */
    public DevelopmentComponent(final String name, final String vendor) {
        this(name, vendor, DevelopmentComponentType.unknown);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the vendor
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * @return the type
     */
    public DevelopmentComponentType getType() {
        return type;
    }

    /**
     * Adds a reference to a public part of a used development component.
     * 
     * @param reference
     *            reference to public part of other development component this
     *            DC references.
     */
    public void add(final PublicPartReference reference) {
        this.usedComponents.add(reference);
    }

    /**
     * Adds the given references to public parts of a used development
     * components.
     * 
     * @param references
     *            references to public parts of other development components
     *            this DC references.
     */
    public void addAll(final Set<PublicPartReference> references) {
        this.usedComponents.addAll(references);
    }

    /**
     * Return all references to public parts of other DCs this DC references.
     * 
     * @return all public part references to DC this DC uses (depends on).
     */
    public Collection<PublicPartReference> getUsedDevelopmentComponents() {
        final List<PublicPartReference> references = new ArrayList<PublicPartReference>(this.usedComponents.size());
        references.addAll(this.usedComponents);
        Collections.sort(references, new PublicPartReferenceComparator());

        return references;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return the compartment
     */
    public Compartment getCompartment() {
        return compartment;
    }

    /**
     * @param compartment
     *            the compartment to set
     */
    void setCompartment(final Compartment compartment) {
        this.compartment = compartment;
    }

    /**
     * Add a development component that uses to this development component.
     * 
     * @param component
     *            the development component to add to those using this DC.
     */
    public void addUsingDC(final DevelopmentComponent component) {
        this.usingComponents.add(component);
    }

    /**
     * Return all development components that use this development component.
     * 
     * @return all development components that use this development component.
     */
    public Collection<DevelopmentComponent> getUsingDevelopmentComponents() {
        final List<DevelopmentComponent> usingDCs = new ArrayList<DevelopmentComponent>(this.usingComponents.size());
        usingDCs.addAll(this.usingComponents);
        Collections.sort(usingDCs, new DevelopmentComponentByNameComparator());

        return usingDCs;
    }

    /**
     * Get public parts of this DC. The returned collection is sorted by
     * {@link PublicPartByNameComparator}.
     * 
     * @return the publicParts
     */
    public Collection<PublicPart> getPublicParts() {
        final List<PublicPart> parts = new ArrayList<PublicPart>(this.publicParts.size());
        parts.addAll(this.publicParts);
        Collections.sort(parts, new PublicPartByNameComparator());

        return parts;
    }

    /**
     * Add a public part to this DC.
     * 
     * @param publicPart
     *            public part to add to this DC
     */
    public void add(final PublicPart publicPart) {
        this.publicParts.add(publicPart);
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @param vendor
     *            the vendor to set
     */
    public void setVendor(final String vendor) {
        this.vendor = vendor;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(final DevelopmentComponentType type) {
        this.type = type;
    }

    /**
     * Determines whether this DC needs a rebuild.
     * 
     * @return the needsRebuild
     */
    public boolean isNeedsRebuild() {
        return needsRebuild;
    }

    /**
     * Set whether this DC needs a rebuild.
     * 
     * @param needsRebuild
     *            <code>true</code> when this DC needs a rebuild,
     *            <code>false</code> otherwise.
     */
    public void setNeedsRebuild(final boolean needsRebuild) {
        this.needsRebuild = needsRebuild;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format(
            "DevelopmentComponent [compartment=%s, vendor=%s, name=%s, publicParts=%s, type=%s, description=%s]",
            this.compartment, this.vendor, this.name, this.publicParts, this.type, this.description);
    }

    /**
     * @return the sourceFolders
     */
    public Set<String> getSourceFolders() {
        return sourceFolders;
    }

    /**
     * Add the given folderName to the source folders of this development
     * component.
     * 
     * @param folderName
     *            source folder to add to this development component.
     */
    public void addSourceFolder(final String folderName) {
        this.sourceFolders.add(folderName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] { this.compartment, this.name, this.type, this.vendor });
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
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

        final DevelopmentComponent other = (DevelopmentComponent)obj;

        return Arrays.equals(new Object[] { this.compartment, this.name, this.type, this.vendor }, new Object[] {
            other.compartment, other.name, other.type, other.vendor });
    }
}
