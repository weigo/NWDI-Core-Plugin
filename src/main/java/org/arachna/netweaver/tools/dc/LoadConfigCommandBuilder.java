/**
 *
 */
package org.arachna.netweaver.tools.dc;

import java.util.ArrayList;
import java.util.List;

import org.arachna.netweaver.hudson.nwdi.NWDIConfigFolder;
import org.arachna.netweaver.tools.DIToolCommandBuilder;
import org.arachna.netweaver.tools.DIToolDescriptor;

/**
 * Command builder for DC toll loadconfig commands.
 * 
 * @author Dirk Weigenand
 */
class LoadConfigCommandBuilder implements DIToolCommandBuilder {
    /**
     * descriptor for DC tool configuration.
     */
    private final DIToolDescriptor dcToolDescriptor;

    /**
     * Template for the different versions of NetWeaver dctool.
     */
    private final LoadConfigTemplate template;

    /**
     * 
     * @param dcToolDescriptor
     * @param template
     */
    LoadConfigCommandBuilder(final DIToolDescriptor dcToolDescriptor, final LoadConfigTemplate template) {
        this.dcToolDescriptor = dcToolDescriptor;
        this.template = template;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<String> execute() {
        final List<String> commands = new ArrayList<String>();
        commands.add(template.getSpoolCommand());
        commands.add(template.getTracefileCommand());
        commands.add(template.getTimingCommand());
        commands.add(getLoadConfigCommand());

        return commands;
    }

    /**
     * Get the command for loading a development configuration.
     * 
     * @return command for loading a development configuration.
     */
    String getLoadConfigCommand() {
        return String.format(template.getLoadConfigCommand(), dcToolDescriptor.getUser(), dcToolDescriptor.getPassword(),
            NWDIConfigFolder.DTR.getName(), NWDIConfigFolder.DTC.getName());
    }

    /**
     * Get the command to enable timing of dc tool commands.
     * 
     * @return command to enable timing of dc tool commands.
     */
    String getTimingCommand() {
        return template.getTimingCommand();
    }

    /**
     * Return the exit command to use for the respective NetWeaver environment.
     * 
     * @return exit command.
     */
    String getExitCommand() {
        return template.getExitCommand();
    }
}
