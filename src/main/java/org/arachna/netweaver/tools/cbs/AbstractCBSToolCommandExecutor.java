/**
 * 
 */
package org.arachna.netweaver.tools.cbs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.arachna.netweaver.tools.DIToolCommandBuilder;
import org.arachna.netweaver.tools.DIToolDescriptor;

/**
 * Base class for CBS tool command builders.
 * 
 * @author Dirk Weigenand
 */
abstract class AbstractCBSToolCommandExecutor implements DIToolCommandBuilder {
    /**
     * URL to connect to CBS.
     */
    private final String cbsUrl;

    /**
     * Authentication information.
     */
    private final DIToolDescriptor diToolDescriptor;

    /**
     * Create a new command builder using the given CBS url and descriptor instance for DI tools (for authentication with the NWDI).
     * 
     * @param cbsUrl
     *            URL to CBS.
     * @param diToolDescriptor
     *            descriptor with user and password for NWDI.
     */
    protected AbstractCBSToolCommandExecutor(final String cbsUrl, final DIToolDescriptor diToolDescriptor) {
        this.cbsUrl = cbsUrl;
        this.diToolDescriptor = diToolDescriptor;
    }

    /**
     * Return the concrete CBS tool command to be executed. Connecting to the CBS and timing and tracing commands are added in the base
     * class.
     * 
     * @return a list of CBS tool commands to be executed.
     */
    protected abstract List<String> executeInternal();

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> execute() {
        final List<String> commands = new ArrayList<String>(Arrays.asList("timing on", "tracefile tracefile.txt", "spool spool.txt"));

        commands.add(String.format("connect -cbsurl %s -u %s -p %s", cbsUrl, diToolDescriptor.getUser(),
            diToolDescriptor.getPassword()));
        commands.addAll(executeInternal());
        commands.add("exit");

        return commands;
    }
}
