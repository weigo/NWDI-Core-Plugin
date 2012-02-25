package org.arachna.netweaver.dc.types;

/**
 * A visitor of a {@link DevelopmentConfiguration} and its compartments and
 * development components.
 * 
 * @author Dirk Weigenand
 */
public interface DevelopmentConfigurationVisitor {
    /**
     * Visit a development configuration.
     * 
     * @param configuration
     *            the configuration instance to visit.
     */
    void visitDevelopmentConfiguration(DevelopmentConfiguration configuration);

    /**
     * Visit a compartment.
     * 
     * @param compartment
     *            the compartment instance to visit.
     */
    void visitCompartment(Compartment compartment);

    /**
     * Visit a development component.
     * 
     * @param component
     *            the development component instance to visit.
     */
    void visitDevelopmentComponent(DevelopmentComponent component);
}