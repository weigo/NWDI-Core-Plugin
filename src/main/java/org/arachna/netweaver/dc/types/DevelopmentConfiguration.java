/**
 *
 */
package org.arachna.netweaver.dc.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A class representing an NWDI development configuration description, i.e. the name of the development configuration and the compartments
 * contained therein.
 *
 * @author Dirk Weigenand
 */
public final class DevelopmentConfiguration {
    /**
     * Location of this development configuration in the file system.
     */
    private String location;

    /**
     * name of this development configuration.
     */
    private final String name;

    /**
     * description of this development configuration.
     */
    private String description;

    /**
     * short description of this development configuration.
     */
    private String caption;

    /**
     * URL to CMS server.
     */
    private String cmsUrl;

    /**
     * workspace name of this development configuration.
     */
    private final String workspace;

    /**
     * map 'scName' to compartment.
     */
    private final Map<String, Compartment> compartmentMap = new HashMap<String, Compartment>();

    /**
     * the build variant to use for this development configuration.
     */
    private BuildVariant buildVariant;

    /**
     * URL to build server.
     */
    private String buildServer;

    /**
     * version of this development configuration.
     */
    private String version = "";

    /**
     * Create a development configuration with the given name.
     *
     * @param name name of the development configuration to create.
     */
    public DevelopmentConfiguration(final String name) {
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException("The name of a development configuration must not be null or empty!");
        }

        this.name = name;
        workspace = name.substring(getBeginIndexOfWorkspace(name), getEndIndexOfWorkspace(name));
    }

    /**
     * Find the start index of the workspace name in the given development configuration name.
     *
     * @param name development configuration name.
     * @return the start index of the workspace name in the given development configuration name.
     */
    private int getBeginIndexOfWorkspace(final String name) {
        final int firstIndexOfUnderScore = name.indexOf('_');

        return firstIndexOfUnderScore == -1 ? 0 : firstIndexOfUnderScore + 1;
    }

    /**
     * Find the end index of the workspace name in the given development configuration name.
     *
     * @param name development configuration name.
     * @return the end index of the workspace name in the given development configuration name.
     */
    private int getEndIndexOfWorkspace(final String name) {
        final int lastIndexOfUnderScore = name.lastIndexOf('_');

        return lastIndexOfUnderScore == -1 ? name.length() : lastIndexOfUnderScore;
    }

    /**
     * Add a compartment to this development configuration.
     *
     * @param compartment compartment to add.
     */
    public void add(final Compartment compartment) {
        if (compartment != null) {
            compartment.setDevelopmentConfiguration(this);
            compartmentMap.put(compartment.getName(), compartment);
        }
    }

    /**
     * Add all given compartments to this development configuration.
     *
     * @param compartments compartments to add to this development configuration.
     */
    public void addAll(final List<Compartment> compartments) {
        for (final Compartment compartment : compartments) {
            add(compartment);
        }
    }

    /**
     * Get the name of this development configuration.
     *
     * @return the name of this development configuration.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the compartments contained in this development configuration.
     *
     * @return the compartments
     */
    public Collection<Compartment> getCompartments() {
        return Collections.unmodifiableCollection(compartmentMap.values());
    }

    /**
     * Returns the compartments filtered by the given {@link CompartmentState}.
     *
     * @param state <code>CompartmentState</code> that should be filtered by.
     * @return compartments filtered by the given {@link CompartmentState}.
     */
    public Collection<Compartment> getCompartments(final CompartmentState state) {
        final Collection<Compartment> compartments = new ArrayList<Compartment>();

        for (final Compartment compartment : compartmentMap.values()) {
            if (state.equals(compartment.getState())) {
                compartments.add(compartment);
            }
        }

        return compartments;
    }

    /**
     * Get a compartment by name.
     *
     * @param name name of compartment
     * @return the compartment found or <code>null</code>.
     */
    public Compartment getCompartment(final String name) {
        return compartmentMap.get(name);
    }

    /**
     * Get the description of this development configuration.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of this development configuration.
     *
     * @param description the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Get the short description of this development configuration.
     *
     * @return the caption
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Set the short description of this development configuration.
     *
     * @param caption the caption to set
     */
    public void setCaption(final String caption) {
        this.caption = caption;
    }

    /**
     * Get the location in the file system of this development configuration.
     *
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Set the location in the file system of this development configuration.
     *
     * @param location the location to set
     */
    public void setLocation(final String location) {
        this.location = location;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("DevelopmentConfiguration [name=%s, caption=%s]", name, caption);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return name.hashCode();
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

        final DevelopmentConfiguration other = (DevelopmentConfiguration) obj;

        return name.equals(other.name);
    }

    /**
     * @return the buildVariant
     */
    public BuildVariant getBuildVariant() {
        return buildVariant;
    }

    /**
     * @param buildVariant the buildVariant to set
     */
    public void setBuildVariant(final BuildVariant buildVariant) {
        this.buildVariant = buildVariant;
    }

    /**
     * Return the URL to the CMS server.
     *
     * @return the cmsUrl
     */
    public String getCmsUrl() {
        return cmsUrl;
    }

    /**
     * Set URL to CMS server.
     *
     * @param cmsUrl the cmsUrl to set
     */
    public void setCmsUrl(final String cmsUrl) {
        this.cmsUrl = cmsUrl;
    }

    /**
     * @return the buildServer
     */
    public String getBuildServer() {
        return buildServer;
    }

    /**
     * @param buildServer the buildServer to set
     */
    public void setBuildServer(final String buildServer) {
        this.buildServer = buildServer;
    }

    /**
     * Return the name of the DTR workspace for this development configuration.
     *
     * @return the name of the DTR workspace for this development configuration.
     */
    public String getWorkspace() {
        return workspace;
    }

    /**
     * Returns the URL for the DTR this development configuration is stored on. The returned string is empty when no compartment in source
     * state is contained in this development configuration.
     *
     * @return URL for the DTR this development configuration is stored on.
     */
    public String getDtrServerUrl() {
        String dtrServerUrl = "";

        for (final Compartment compartment : this.getCompartments(CompartmentState.Source)) {
            if (compartment.getDtrUrl() != null && compartment.getDtrUrl().endsWith("dtr")) {
                dtrServerUrl = compartment.getDtrUrl();
                break;
            }
        }

        return dtrServerUrl;
    }

    /**
     * Get the {@link JdkHomeAlias} associated with the {@link BuildVariant} of this development configuration.
     *
     * @return the JdkHomeAlias associated with the build variant of this development configuration iff there is one defined and a {@link
     * BuildVariant#COM_SAP_JDK_HOME_PATH_KEY} build option is defined.
     * <br>
     * Returns the alias matching the running JVMs version otherwise.
     */
    public JdkHomeAlias getJdkHomeAlias() {
        final BuildVariant buildVariant = getBuildVariant();
        JdkHomeAlias alias = null;

        if (buildVariant != null) {
            alias = JdkHomeAlias.fromString(buildVariant.getJdkHomePath());
        }

        if (alias == null) {
            alias = JdkHomeAlias.fromJavaVersion();
        }

        return alias;
    }

    /**
     * Returns the source version to use for generating javadoc documentation.
     * <p>
     * Uses the {@link JdkHomeAlias} defined in the development configuration. If there is no alias defined use the JDK version the ant task
     * is run with.
     *
     * @return java source version to use generating javadoc documentation.
     */
    public String getSourceVersion() {
        final JdkHomeAlias alias = getJdkHomeAlias();
        String sourceVersion;

        if (alias != null) {
            sourceVersion = alias.getSourceVersion();
        } else {
            final String[] versionParts = System.getProperty("java.version").replace('_', '.').split("\\.");
            sourceVersion = String.format("%s.%s", versionParts[0], versionParts[1]);
        }

        return sourceVersion;
    }

    /**
     * Accept a visitor of this development configuration. Iterate over its compartments and development components and call the respective
     * visit method.
     *
     * @param visitor visitor for this development configuration.
     */
    public void accept(final DevelopmentConfigurationVisitor visitor) {
        visitor.visit(this);

        for (final Compartment compartment : this.getCompartments()) {
            visitor.visit(compartment);

            for (final DevelopmentComponent component : compartment.getDevelopmentComponents()) {
                visitor.visit(component);
            }
        }
    }

    /**
     * Return the version of this development configuration.
     *
     * @return version of this development configuration.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Set the version of this development configuration.
     *
     * @param version the version to set
     */
    public void setVersion(final String version) {
        this.version = version;
    }

    /**
     * Set the needsRebuild property on all development components in source state if a clean build was requested.
     *
     * @param calculator
     *            Determines whether a development component needs to be rebuilt.
     */
    public void setNeedsRebuild(final NeedsRebuildCalculator calculator) {
        for (final Compartment compartment : getCompartments(CompartmentState.Source)) {
            for (final DevelopmentComponent component : compartment.getDevelopmentComponents()) {
                component.setNeedsRebuild(calculator.needsRebuild(component));
            }
        }
    }
}
