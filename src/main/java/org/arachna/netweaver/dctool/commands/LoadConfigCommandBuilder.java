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
class LoadConfigCommandBuilder implements DCToolCommandBuilder {
    /**
     * descriptor for DC tool configuration.
     */
    private final DCToolDescriptor dcToolDescriptor;

    /**
     * Template for the different versions of NetWeaver dctool.
     */
    private final LoadConfigTemplate template;

    /**
     * 
     * @param dcToolDescriptor
     * @param template
     */
    public LoadConfigCommandBuilder(final DCToolDescriptor dcToolDescriptor, final LoadConfigTemplate template) {
        this.dcToolDescriptor = dcToolDescriptor;
        this.template = template;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<String> execute() {
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
     * Get the command for loading a development configuration.
     * 
     * @return command for loading a development configuration.
     */
    private String getLoadConfigCommand() {
        return String.format(this.template.getLoadConfigCommand(), getDcToolDescriptor().getUser(),
            getDcToolDescriptor().getPassword(), DCToolDescriptor.DTR_FOLDER, DCToolDescriptor.DTC_FOLDER);
    }

    /**
     * Get the command to enable timing of dc tool commands.
     * 
     * @return command to enable timing of dc tool commands.
     */
    private String getTimingCommand() {
        return this.template.getTimingCommand();
    }
}
