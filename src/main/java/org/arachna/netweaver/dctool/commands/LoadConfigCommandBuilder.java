/**
 *
 */
package org.arachna.netweaver.dctool.commands;

import java.util.ArrayList;
import java.util.List;

import org.arachna.netweaver.dctool.DCToolDescriptor;

/**
 * Command builder for DC toll loadconfig commands.
 *
 * @author Dirk Weigenand
 */
abstract class LoadConfigCommandBuilder extends AbstractDCToolCommandBuilder implements DCToolCommandBuilder {
    /**
     * descriptor for DC tool configuration.
     */
    private final DCToolDescriptor dcToolDescriptor;

    /**
     * @param developmentConfiguration
     */
    public LoadConfigCommandBuilder(DCToolDescriptor dcToolDescriptor) {
        super(null);
        this.dcToolDescriptor = dcToolDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final List<String> executeInternal() {
        List<String> commands = new ArrayList<String>();

        commands.add(getTimingCommand());
        commands.add(getLoadConfigCommand());

        return commands;
    }

    /**
     * @return the dcToolDescriptor
     */
    protected final DCToolDescriptor getDcToolDescriptor() {
        return dcToolDescriptor;
    }

    /**
     * Get the command to enable timing of dc tool commands.
     *
     * @return command to enable timing of dc tool commands.
     */
    protected abstract String getTimingCommand();

    /**
     * Get the command for loading a development configuration.
     *
     * @return command for loading a development configuration.
     */
    protected abstract String getLoadConfigCommand();
}
