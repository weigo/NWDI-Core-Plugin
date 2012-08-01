package org.arachna.netweaver.dc.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Represents a compartment/software component of a {@link DevelopmentConfiguration} in the NetWeaver development infrastructure.
 * 
 * @author Dirk Weigenand
 */
public final class Compartment {
    /**
     * Error message for empty or <code>null</code> arguments.
     */
    private static final String NULL_OR_EMPTY_ARGUMENT_MESSAGE = "The '%s' argument must not be null or empty!";

    /**
     * name of compartment.
     */
    private final String name;

    /**
     * short description of the compartment.
     */
    private String caption;

    /**
     * vendor of the compartment.
     */
    private String vendor;

    /**
     * name of software component.
     */
    private String softwareComponent;

    /**
     * associated {@link DevelopmentConfiguration}.
     */
    private DevelopmentConfiguration developmentConfiguration;

    /**
     * Compartments that are used by this compartment.
     */
    private final Set<Compartment> usedCompartments = new HashSet<Compartment>();

    /**
     * {@link DevelopmentComponent}s contained in this compartment.
     */
    private final Set<DevelopmentComponent> components = new HashSet<DevelopmentComponent>();

    /**
     * Type of compartment {@link CompartmentState}.
     */
    private CompartmentState state = CompartmentState.Source;

    /**
     * URL of DTR server.
     */
    private String dtrUrl;

    /**
     * location of software component in DTR.
     */
    private String inactiveLocation;

    /**
     * Create a new Compartment instance with the given name and {@link CompartmentState}.
     * 
     * @param name
     *            name of compartment.
     * @param state
     *            state of compartment.
     * @param vendor
     *            vendor of compartment.
     * @param caption
     *            caption of compartment.
     * @param softwareComponent
     *            name of software component this compartment represents.
     */
    public Compartment(final String name, final CompartmentState state, final String vendor, final String caption,
        final String softwareComponent) {
        validateString(name, "name");
        validateString(vendor, "vendor");
        validateString(softwareComponent, "softwareComponent");

        if (state == null) {
            throw new IllegalArgumentException("CompartmentState must not be null!");
        }

        this.name = name;
        this.state = state;
        this.vendor = vendor;
        this.caption = caption;
        this.softwareComponent = softwareComponent;
    }

    /**
     * Validate the given string and check that it is neither <code>null</code> nor empty. Throw an {@link IllegalArgumentException}
     * otherwise.
     * 
     * @param argument
     *            the argument to validate.
     * @param argumentName
     *            the name to use in the message in case the argument didn't meet our expectations.
     */
    private void validateString(final String argument, final String argumentName) {
        if (argument == null || argument.trim().length() == 0) {
            final IllegalArgumentException iae =
                new IllegalArgumentException(String.format(NULL_OR_EMPTY_ARGUMENT_MESSAGE, argumentName));
            iae.fillInStackTrace();
            throw iae;
        }
    }

    /**
     * Add a compartment to this compartments dependencies.
     * 
     * @param compartment
     *            used compartment.
     */
    public void add(final Compartment compartment) {
        if (compartment != null) {
            this.usedCompartments.add(compartment);
        }
    }

    /**
     * Set the compartments used by this compartment.
     * 
     * @param usedCompartments
     *            the compartments needed to build development components contained in this compartment.
     */
    public void set(final Collection<Compartment> usedCompartments) {
        this.usedCompartments.clear();

        if (usedCompartments != null) {
            this.usedCompartments.addAll(usedCompartments);
        }
    }

    /**
     * Add all given compartments to the dependencies of this compartment.
     * 
     * @param compartments
     *            used compartments.
     */
    public void addCompartments(final List<Compartment> compartments) {
        for (final Compartment compartment : compartments) {
            this.add(compartment);
        }
    }

    /**
     * Add a {@link DevelopmentComponent} to this compartment.
     * 
     * @param component
     *            development component to add.
     */
    public void add(final DevelopmentComponent component) {
        component.setCompartment(this);
        this.components.add(component);
    }

    /**
     * Remove a {@link DevelopmentComponent} from this compartment.
     * 
     * @param component
     *            development component to remove.
     */
    public void remove(final DevelopmentComponent component) {
        if (this.components.remove(component)) {
            component.setCompartment(null);
        }

        for (DevelopmentComponent dc : this.components) {
            if (dc.equals(component)) {
                this.components.remove(component);
                System.err.println(String.format("Removing component %s from %s.", component, this));
            }
        }
    }

    /**
     * Add all given {@link DevelopmentComponent}s to this compartment.
     * 
     * @param components
     *            Entwicklungskomponenten in diesem Compartment
     */
    public void add(final Collection<DevelopmentComponent> components) {
        for (final DevelopmentComponent component : components) {
            this.add(component);
        }
    }

    /**
     * Return the collection of compartments used by this compartment.
     * 
     * @return collection of compartments used by this compartment.
     */
    public Collection<Compartment> getUsedCompartments() {
        return Collections.unmodifiableCollection(this.usedCompartments);
    }

    /**
     * Return the name of this compartment.
     * 
     * @return the name of this compartment.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Return the {@link DevelopmentConfiguration} this compartment is associated with.
     * 
     * @return the developmentConfiguration this compartment is associated with.
     */
    public DevelopmentConfiguration getDevelopmentConfiguration() {
        return this.developmentConfiguration;
    }

    /**
     * Associate the given {@link DevelopmentConfiguration} with this compartment.
     * 
     * @param developmentConfiguration
     *            the developmentConfiguration to associate this compartment with.
     */
    void setDevelopmentConfiguration(final DevelopmentConfiguration developmentConfiguration) {
        this.developmentConfiguration = developmentConfiguration;
    }

    /**
     * Return whether this compartment is of state {@link CompartmentState#Archive}.
     * 
     * @return <code>true</code> if this compartment has state {@link CompartmentState#Archive}, <code>false</code> otherwise.
     */
    public boolean isArchiveState() {
        return CompartmentState.Archive.equals(this.state);
    }

    /**
     * Return whether this compartment is of state {@link CompartmentState#Source}.
     * 
     * @return <code>true</code> if this compartment has state {@link CompartmentState#Source}, <code>false</code> otherwise.
     */
    public boolean isSourceState() {
        return CompartmentState.Source.equals(this.state);
    }

    /**
     * Set the {@link CompartmentState} this compartment is in.
     * 
     * @param state
     *            the state to set
     */
    public void setState(final CompartmentState state) {
        this.state = state;
    }

    /**
     * The short description of this compartment.
     * 
     * @return the caption
     */
    public String getCaption() {
        return this.caption;
    }

    /**
     * Set the short description of this compartment.
     * 
     * @param caption
     *            the caption to set
     */
    void setCaption(final String caption) {
        this.caption = caption;
    }

    /**
     * @return the vendor
     */
    public String getVendor() {
        return this.vendor;
    }

    /**
     * @param vendor
     *            the vendor to set
     */
    void setVendor(final String vendor) {
        this.vendor = vendor;
    }

    /**
     * @return the softwareComponent
     */
    public String getSoftwareComponent() {
        return this.softwareComponent;
    }

    /**
     * @param softwareComponent
     *            the softwareComponent to set
     */
    void setSoftwareComponent(final String softwareComponent) {
        this.softwareComponent = softwareComponent;
    }

    /**
     * Return the {@link DevelopmentComponent}s contained in this compartment.
     * 
     * @return the components
     */
    public Collection<DevelopmentComponent> getDevelopmentComponents() {
        return Collections.unmodifiableCollection(this.components);
    }

    /**
     * Get development components filtered by the given type.
     * 
     * @param dcType
     *            the {@link DevelopmentComponentType} to be filtered with.
     * @return a collection of development components matching the given development component type.
     */
    public Collection<DevelopmentComponent> getDevelopmentComponents(DevelopmentComponentType dcType) {
        if (dcType == null) {
            throw new IllegalArgumentException("DevelopmentComponentType to be filtered with must not be null!");
        }

        Collection<DevelopmentComponent> matchingDCs = new LinkedList<DevelopmentComponent>();

        for (DevelopmentComponent component : this.components) {
            if (dcType.equals(component.getType())) {
                matchingDCs.add(component);
            }
        }

        return matchingDCs;
    }

    /**
     * Get {@link CompartmentState} of this component.
     * 
     * @return the state
     */
    public CompartmentState getState() {
        return this.state;
    }

    /**
     * @return the dtrUrl
     */
    public String getDtrUrl() {
        return this.dtrUrl;
    }

    /**
     * @param dtrUrl
     *            the dtrUrl to set
     */
    public void setDtrUrl(final String dtrUrl) {
        this.dtrUrl = dtrUrl;
    }

    /**
     * @return the inactiveLocation
     */
    public String getInactiveLocation() {
        return this.inactiveLocation;
    }

    /**
     * @param inactiveLocation
     *            the inactiveLocation to set
     */
    public void setInactiveLocation(final String inactiveLocation) {
        this.inactiveLocation = inactiveLocation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Compartment [developmentConfiguration=" + developmentConfiguration + ", name=" + name
            + ", softwareComponent=" + softwareComponent + ", state=" + state + ", vendor=" + vendor + "]";
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] { this.developmentConfiguration, this.name, this.softwareComponent,
            this.vendor });
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        boolean result = this == obj;

        if (!result && obj != null) {
            result = getClass() == obj.getClass();

            if (result) {
                final Compartment other = (Compartment)obj;

                result =
                    Arrays.equals(new Object[] { this.developmentConfiguration, this.softwareComponent, this.name,
                        this.vendor }, new Object[] { other.developmentConfiguration, other.softwareComponent,
                        other.name, other.vendor });
            }
        }

        return result;
    }

    /**
     * Return the description of the first development component of type {@see DevelopmentComponentType.SoftwareComponentDescription}.
     * 
     * The description will be empty if no such DC could be found or the found DC contains no description (shame on you).
     * 
     * @return description of the software component.
     */
    public String getDescription() {
        String description = "";

        Iterator<DevelopmentComponent> scDescriptions =
            this.getDevelopmentComponents(DevelopmentComponentType.SoftwareComponentDescription).iterator();

        if (scDescriptions.hasNext()) {
            description = scDescriptions.next().getDescription();
        }

        return description;
    }

    /**
     * Factory method for compartments.
     * 
     * @param vendor
     *            vendor of software component
     * @param name
     *            software component name
     * @param state
     *            {@see CompartmentState#Archive} and {@see CompartmentState#Source}.
     * @param caption
     *            short description
     * @return new compartment.
     */
    public static Compartment create(String vendor, String name, CompartmentState state, String caption) {
        return new Compartment(String.format("%s_%s_1", vendor, name), state, vendor, caption, name);

    }
}
