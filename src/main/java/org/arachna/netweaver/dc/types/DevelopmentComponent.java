package org.arachna.netweaver.dc.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
     * shot description of a development component.
     */
    private String caption = "";

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
     * indicates whether this development component is deprecated.
     */
    private boolean isDeprecated;

    /**
     * contains the public parts of this development component if any.
     */
    private final Set<PublicPart> publicParts = new HashSet<PublicPart>();

    /**
     * contains references to development components used by this development component.
     */
    private final Set<PublicPartReference> usedComponents = new HashSet<PublicPartReference>();

    /**
     * build plugin used to build this development component.
     */
    private PublicPartReference buildPlugin;

    /**
     * collection of development components that use this DC.
     */
    private final Set<DevelopmentComponent> usingComponents = new LinkedHashSet<DevelopmentComponent>();

    /**
     * folders containing sources.
     */
    private final Set<String> sourceFolders = new LinkedHashSet<String>();

    /**
     * folders containing test sources.
     */
    private final Set<String> testSourceFolders = new LinkedHashSet<String>();

    /**
     * folders containing resources.
     */
    private final Set<String> resourceFolders = new LinkedHashSet<String>();

    /**
     * folder the class files for this development component were generated to during the last build.
     */
    private String outputFolder = "";

    /**
     * Encoding of source files.
     */
    private String sourceEncoding;

    /**
     * Create an instance of a development component with the given name, vendor and DC type.
     *
     * @param name   name of this development component.
     * @param vendor vendor of this development component.
     * @param type   type of this development component.
     */
    public DevelopmentComponent(final String name, final String vendor, final DevelopmentComponentType type) {
        this.name = name;
        this.vendor = vendor;
        this.type = type;
    }

    /**
     * Create an instance of a development component with the given name, vendor and DC type {@link DevelopmentComponentType#unknown}.
     *
     * @param name   name of this development component.
     * @param vendor vendor of this development component.
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
     * @param reference reference to public part of other development component this DC references.
     */
    public void add(final PublicPartReference reference) {
        usedComponents.add(reference);
    }

    /**
     * Adds the given references to public parts of a used development components.
     *
     * @param references references to public parts of other development components this DC references.
     */
    public void addAll(final Collection<PublicPartReference> references) {
        if (references != null) {
            usedComponents.addAll(references);
        }
    }

    /**
     * Return all references to public parts of other DCs this DC references.
     *
     * @return all public part references to DC this DC uses (depends on).
     */
    public Collection<PublicPartReference> getUsedDevelopmentComponents() {
        return Collections.unmodifiableCollection(usedComponents);
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
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
     * @param compartment the compartment to set
     */
    void setCompartment(final Compartment compartment) {
        this.compartment = compartment;
    }

    /**
     * Add a development component that uses to this development component.
     *
     * @param component the development component to add to those using this DC.
     */
    public void addUsingDC(final DevelopmentComponent component) {
        usingComponents.add(component);
    }

    /**
     * Return all development components that use this development component.
     *
     * @return all development components that use this development component.
     */
    public Collection<DevelopmentComponent> getUsingDevelopmentComponents() {
        return Collections.unmodifiableCollection(usingComponents);
    }

    /**
     * Get public parts of this DC. The returned collection is sorted by {@link PublicPartByNameComparator}.
     *
     * @return the publicParts
     */
    public Collection<PublicPart> getPublicParts() {
        return Collections.unmodifiableCollection(publicParts);
    }

    /**
     * Set the public parts for this development component. Clears the existing public parts and adds those public parts given as argument
     * iff not null.
     *
     * @param publicParts public parts to set.
     */
    public void setPublicParts(final Collection<PublicPart> publicParts) {
        this.publicParts.clear();

        if (publicParts != null) {
            this.publicParts.addAll(publicParts);
        }
    }

    /**
     * Add a public part to this DC.
     *
     * @param publicPart public part to add to this DC
     */
    public void add(final PublicPart publicPart) {
        publicParts.add(publicPart);
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @param vendor the vendor to set
     */
    public void setVendor(final String vendor) {
        this.vendor = vendor;
    }

    /**
     * @param type the type to set
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
     * @param needsRebuild <code>true</code> when this DC needs a rebuild, <code>false</code> otherwise.
     */
    public void setNeedsRebuild(final boolean needsRebuild) {
        this.needsRebuild = needsRebuild;
    }

    /**
     * Returns whether this development component is deprecated.
     *
     * @return <code>true</code> if this development component is deprecated, <code>false</code> otherwise.
     */
    public boolean isDeprecated() {
        return isDeprecated;
    }

    /**
     * Set whether this development component is deprecated.
     *
     * @param isDeprecated <code>true</code> if this development component is deprecated, <code>false</code> otherwise.
     */
    public void setDeprecated(final boolean isDeprecated) {
        this.isDeprecated = isDeprecated;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("DC [vendor=%s, name=%s, type=%s]", vendor, name, type);
    }

    /**
     * @return the sourceFolders
     */
    public Set<String> getSourceFolders() {
        return sourceFolders;
    }

    /**
     * Add the given folderName to the source folders of this development component.
     *
     * @param folderName source folder to add to this development component.
     */
    public void addSourceFolder(final String folderName) {
        sourceFolders.add(folderName);
    }

    /**
     * Set source folders for this development component.
     *
     * @param sourceFolders the source folders to set for this DC. If the given collection is null the DC will have no source folders afterwards.
     */
    public void setSourceFolders(final Set<String> sourceFolders) {
        this.sourceFolders.clear();

        if (sourceFolders != null) {
            this.sourceFolders.addAll(sourceFolders);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{name, type, vendor});
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

        final DevelopmentComponent other = (DevelopmentComponent) obj;

        return Arrays.equals(new Object[]{name, type, vendor}, new Object[]{other.name, other.type, other.vendor});
    }

    /**
     * Sets the folder the class files for this development component were generated to during the last build.
     *
     * @param outputFolder folder the class files for this development component were generated to during the last build
     */
    public void setOutputFolder(final String outputFolder) {
        if (outputFolder != null && !outputFolder.isEmpty()) {
            this.outputFolder = outputFolder;
        } else {
            this.outputFolder = "gen/classes";
        }
    }

    /**
     * Returns the folder the class files for this development component were generated to during the last build.
     *
     * @return the outputFolder folder the class files for this development component were generated to during the last build
     */
    public String getOutputFolder() {
        return outputFolder;
    }

    /**
     * Returns the short description of this development component.
     *
     * @return the short description of this development component
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Sets the short description of this development component.
     *
     * @param caption the short description of this development component
     */
    public void setCaption(final String caption) {
        if (caption == null) {
            this.caption = "";
        } else {
            this.caption = caption;
        }
    }

    /**
     * Determine whether this development component has a runtime reference to the public part given by the {@link PublicPartReference}
     * parameter.
     *
     * @param ppRef public part reference to test
     * @return <code>true</code> when the given public part is referenced at runtim, <code>false</code> otherwise.
     */
    public boolean hasRuntimeReference(final PublicPartReference ppRef) {
        boolean result = false;

        for (final PublicPartReference ref : getUsedDevelopmentComponents()) {
            if (ref.isAtRunTime() && ref.getVendor().equals(ppRef.getVendor()) && ref.getComponentName().equals(ppRef.getComponentName())) {
                result = true;
                break;
            }
        }

        return result;
    }

    /**
     * Set the references to the public parts of other development components used by this component.
     *
     * @param usedComponents references to public parts of development components used by this development components.
     */
    public void setUsedComponents(final Collection<PublicPartReference> usedComponents) {
        this.usedComponents.clear();
        addAll(usedComponents);
    }

    /**
     * Returns the components name as <code>'vendor' + separator + 'name'.replace('/', separator)</code>.
     *
     * @param separator the separator character to use between vendor and component name and also replacing the slashes probably in the component
     *                  name.
     * @return the vendor/name combination normalized using the given separator character.
     */
    public String getNormalizedName(final String separator) {
        return getVendor() + separator + getName().replaceAll("/", separator);
    }

    /**
     * Returns a public part reference to the development component used to build this development component.
     *
     * @return the buildPlugin
     */
    public PublicPartReference getBuildPlugin() {
        return buildPlugin;
    }

    /**
     * @param buildPlugin the buildPlugin to set
     */
    public void setBuildPlugin(final PublicPartReference buildPlugin) {
        this.buildPlugin = buildPlugin;
    }

    /**
     * Set the developement component type from the given type and sub type.
     *
     * @param type    main type of development component.
     * @param subType sub type of development component.
     */
    public void setType(final String type, final String subType) {
        setType(DevelopmentComponentType.fromString(type, subType));
    }

    /**
     * Set the encoding of source files to use.
     *
     * @param sourceEncoding encoding of source files to use.
     */
    public void setSourceEncoding(final String sourceEncoding) {
        this.sourceEncoding = sourceEncoding;
    }

    /**
     * Return the encoding of source files to use for this development component.
     *
     * @return encoding of source files to use for this development component.
     */
    public String getSourceEncoding() {
        return sourceEncoding;
    }

    /**
     * Add the given sourceFolder to the collection of source folders containing unit tests.
     *
     * @param sourceFolder source folder to add to folders containing unit tests.
     */
    public void addTestSourceFolder(final String sourceFolder) {
        testSourceFolders.add(sourceFolder);
    }

    /**
     * Return the collection of folders containing unit tests.
     *
     * @return collection of folders containing unit tests.
     */
    public Set<String> getTestSourceFolders() {
        return testSourceFolders;
    }

    /**
     * Set the folders containing test classes.
     *
     * @param testSourceFolders folders containing test classes.
     */
    public void setTestSourceFolders(final Set<String> testSourceFolders) {
        this.testSourceFolders.clear();

        if (testSourceFolders != null) {
            this.testSourceFolders.addAll(testSourceFolders);
        }
    }

    /**
     * Add the given sourceFolder to the collection of source folders containing resources.
     *
     * @param sourceFolder source folder to add to folders containing resources.
     */
    public void addResourceFolder(final String sourceFolder) {
        resourceFolders.add(sourceFolder);
    }

    /**
     * Return the collection of folders containing resources.
     *
     * @return collection of folders containing resources.
     */
    public Set<String> getResourceFolders() {
        return resourceFolders;
    }

    /**
     * Set the folders containing resources.
     *
     * @param resourceFolders source folders containing resources.
     */
    public void setResourceFolders(final Set<String> resourceFolders) {
        this.resourceFolders.clear();

        if (resourceFolders != null) {
            this.resourceFolders.addAll(resourceFolders);
        }
    }
}
