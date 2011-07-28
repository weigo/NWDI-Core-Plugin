/**
 *
 */
package org.arachna.netweaver.dctool.commands;

import org.arachna.netweaver.dctool.DCToolDescriptor;

/**
 * Command builder for DC toll loadconfig commands.
 *
 * @author Dirk Weigenand
 */
final class LoadConfigCommandBuilderV71 extends LoadConfigCommandBuilder implements DCToolCommandBuilder {
    /**
     * constant for 'loadconfig' command.
     */
    private static final String LOAD_CONFIG_COMMAND = "loadconfig -u %s -p %s -v \"%s\" -l \"%s\"";

    /**
     * @param developmentConfiguration
     */
    public LoadConfigCommandBuilderV71(DCToolDescriptor dcToolDescriptor) {
        super(dcToolDescriptor);
    }

    /**
     * @return
     */
    protected String getTimingCommand() {
        return "timing on";
    }

    /**
     * @return
     */
    protected String getLoadConfigCommand() {
        return String.format(LOAD_CONFIG_COMMAND, getDcToolDescriptor().getUser(), getDcToolDescriptor().getPassword(),
            DCToolDescriptor.DTR_FOLDER, DCToolDescriptor.DTC_FOLDER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getExitCommand() {
        throw new UnsupportedOperationException();
    }
}