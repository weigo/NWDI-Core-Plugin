/**
 * 
 */
package org.arachna.netweaver.tools.cbs;

import java.util.Arrays;
import java.util.List;

import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.tools.DIToolCommandBuilder;
import org.arachna.netweaver.tools.DIToolDescriptor;

/**
 * {@link DIToolCommandBuilder} for listing DCs using the <code>cbstool</code>.
 * 
 * @author Dirk Weigenand
 */
final class DCLister extends AbstractDCLister {
    /**
     * Create a new DCLister instance using the given development configuration,
     * DI tool descriptor (for authentication).
     * 
     * @param developmentConfiguration
     *            development configuration to list DCs for.
     * @param diToolDescriptor
     *            DI tool descriptor used for authentication.
     */
    DCLister(final DevelopmentConfiguration developmentConfiguration, final DIToolDescriptor diToolDescriptor) {
        super(developmentConfiguration, diToolDescriptor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> executeInternal() {
        return Arrays.asList(String.format("listdcs -b %s -m all", getBuildSpace()));
    }
}
