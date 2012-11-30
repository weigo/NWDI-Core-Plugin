/**
 * 
 */
package org.arachna.netweaver.tools.cbs;

import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.tools.DIToolDescriptor;

/**
 * Abstract base class for commands listing DCs for a given build space.
 * 
 * @author Dirk Weigenand
 */
abstract class AbstractDCLister extends AbstractCBSToolCommandExecutor {
    /**
     * build space name to list DCs from.
     */
    private final String buildSpace;

    /**
     * Create a new DCLister instance using the given development configuration,
     * DI tool descriptor (for authentication).
     * 
     * @param developmentConfiguration
     *            development configuration to list DCs for.
     * @param diToolDescriptor
     *            DI tool descriptor used for authentication.
     */
    protected AbstractDCLister(final DevelopmentConfiguration developmentConfiguration,
        final DIToolDescriptor diToolDescriptor) {
        super(developmentConfiguration.getCmsUrl(), diToolDescriptor);
        buildSpace = developmentConfiguration.getName();
    }

    /**
     * Returns the build space name to list DCs from.
     * 
     * @return the buildSpace
     */
    protected final String getBuildSpace() {
        return buildSpace;
    }
}
