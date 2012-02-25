/**
 * 
 */
package org.arachna.netweaver.dc.types;

/**
 * Base class for visitors of a development configuration.
 * 
 * @author Dirk Weigenand
 */
public abstract class AbstractDevelopmentConfigurationVisitor implements DevelopmentConfigurationVisitor {
    /**
     * {@inheritDoc}
     */
    public void visitDevelopmentConfiguration(final DevelopmentConfiguration configuration) {
    }

    /**
     * {@inheritDoc}
     */
    public void visitCompartment(final Compartment compartment) {
    }

    /**
     * {@inheritDoc}
     */
    public void visitDevelopmentComponent(final DevelopmentComponent component) {
    }
}
