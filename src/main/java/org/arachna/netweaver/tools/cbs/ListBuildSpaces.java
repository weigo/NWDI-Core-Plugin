/**
 * 
 */
package org.arachna.netweaver.tools.cbs;

import java.util.Arrays;
import java.util.List;

import org.arachna.netweaver.tools.DIToolDescriptor;

/**
 * Execute a CBS tool 'listbuildspaces' command.
 * 
 * @author Dirk Weigenand
 */
final class ListBuildSpaces extends AbstractCBSToolCommandExecutor {
    /**
     * Create a CBS tool 'listbuildspaces' command using the given URL to connect to the CBS and the authentication information from the
     * {@link DIToolDescriptor}.
     * 
     * @param cbsUrl
     *            URL to connect to the CBS
     * @param diToolDescriptor
     *            authentication information
     */
    protected ListBuildSpaces(String cbsUrl, DIToolDescriptor diToolDescriptor) {
        super(cbsUrl, diToolDescriptor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> executeInternal() {
        return Arrays.asList("listbuildspaces");
    }
}
