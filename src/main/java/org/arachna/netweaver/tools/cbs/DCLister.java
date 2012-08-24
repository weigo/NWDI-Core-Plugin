/**
 * 
 */
package org.arachna.netweaver.tools.cbs;

import java.util.Arrays;
import java.util.List;

import org.arachna.netweaver.tools.DIToolCommandBuilder;
import org.arachna.netweaver.tools.DIToolDescriptor;

/**
 * {@link DIToolCommandBuilder} for listing DCs using the <code>cbstool</code>.
 * 
 * @author Dirk Weigenand
 */
final class DCLister extends AbstractCBSToolCommandExecutor {
    /**
     * build space name to list DCs from.
     */
    private final String buildSpace;

    /**
     * Create a DC lister using the given URL to connect to the CBS, the build space to list DCs from and the authentication information
     * from the {@link DIToolDescriptor}.
     * 
     * @param cbsUrl
     *            URL to connect to the CBS
     * @param buildSpace
     *            build space to list DCs from
     * @param diToolDescriptor
     *            authentication information
     */
    DCLister(final String cbsUrl, final String buildSpace, final DIToolDescriptor diToolDescriptor) {
        super(cbsUrl, diToolDescriptor);
        this.buildSpace = buildSpace;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> executeInternal() {
        return Arrays.asList(String.format("listdcs -b %s -m all", buildSpace));
    }
}
