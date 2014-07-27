/**
 * 
 */
package org.arachna.netweaver.tools.cbs;

import java.util.Arrays;
import java.util.List;

import org.arachna.netweaver.tools.DIToolDescriptor;

/**
 * List compartments using a pre SAP NW CE CBS tool.
 * 
 * @author Sanders Star
 */
final class ListCompartments extends AbstractCBSToolCommandExecutor {

    /**
     * build space name to list DCs from.
     */
    private final String buildSpace;

    /**
     * Create a CBS tool 'listcompartments' command for the given buildspace
     * using the given URL to connect to the CBS and the authentication
     * information from the {@link DIToolDescriptor}.
     * 
     * @param cbsUrl
     *            URL to connect to the CBS
     * @param buildSpace
     *            build space to list DCs from
     * @param diToolDescriptor
     *            authentication information
     */
    ListCompartments(final String cbsUrl, final String buildSpace, final DIToolDescriptor diToolDescriptor) {
        super(cbsUrl, diToolDescriptor);
        this.buildSpace = buildSpace;
    }

    @Override
    protected List<String> executeInternal() {
        return Arrays.asList(String.format("listcompartments -b %s", buildSpace));
    }
}
