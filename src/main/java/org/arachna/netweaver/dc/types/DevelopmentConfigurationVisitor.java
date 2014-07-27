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
    void visit(DevelopmentConfiguration configuration);

    /**
     * Visit a compartment.
     * 
     * @param compartment
     *            the compartment instance to visit.
     */
    void visit(Compartment compartment);

    /**
     * Visit a development component.
     * 
     * @param component
     *            the development component instance to visit.
     */
    void visit(DevelopmentComponent component);
}