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
    public void visit(final DevelopmentConfiguration configuration) {
    }

    /**
     * {@inheritDoc}
     */
    public void visit(final Compartment compartment) {
    }

    /**
     * {@inheritDoc}
     */
    public void visit(final DevelopmentComponent component) {
    }
}
