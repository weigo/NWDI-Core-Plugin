/**
 *
 */
package org.arachna.netweaver.tools.dc.commands;

import java.util.List;

import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.tools.DIToolCommandBuilder;

/**
 * Base class for DC tool command builders.
 * 
 * @author Dirk Weigenand
 */
public abstract class AbstractDCToolCommandBuilder implements DIToolCommandBuilder {
    /**
     * development configuration to list compartments/development components from.
     */
    private final DevelopmentConfiguration developmentConfiguration;

    /**
     * create a builder for DC Tool commands.
     * 
     * @param developmentConfiguration
     *            development configuration to use.
     */
    protected AbstractDCToolCommandBuilder(final DevelopmentConfiguration developmentConfiguration) {
        assert developmentConfiguration != null;
        this.developmentConfiguration = developmentConfiguration;
    }

    /**
     * Returns the development configuration of this DC tool command builder.
     * 
     * @return the developmentConfiguration
     */
    protected final DevelopmentConfiguration getDevelopmentConfiguration() {
        return this.developmentConfiguration;
    }

    /**
     * Create list of DC tool commands.
     * 
     * @return list of DC tool commands.
     */
    @Override
    public final List<String> execute() {
        return this.executeInternal();
    }

    /**
     * Implemented by sub classes to generate the DC Tool commands specific to this DC tool command builder.
     * 
     * @return DC Tool commands specific to this DC tool command builder.
     */
    protected abstract List<String> executeInternal();
}
