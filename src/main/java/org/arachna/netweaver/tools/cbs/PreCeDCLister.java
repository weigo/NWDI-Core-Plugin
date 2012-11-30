/**
 * 
 */
package org.arachna.netweaver.tools.cbs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.tools.DIToolDescriptor;

/**
 * A command to list DCs for a given build space for pre CE versions of the CBS
 * tool.
 * 
 * @author Dirk Weigenand
 */
final class PreCeDCLister extends AbstractDCLister {
    /**
     * Use this development configuration to determine which compartments to
     * list DCs for.
     */
    private final DevelopmentConfiguration developmentConfiguration;

    /**
     * Create a new DCLister instance using the given development configuration,
     * DI tool descriptor (for authentication).
     * 
     * @param developmentConfiguration
     *            development configuration to list DCs for.
     * @param diToolDescriptor
     *            DI tool descriptor used for authentication.
     */
    PreCeDCLister(final DevelopmentConfiguration developmentConfiguration, final DIToolDescriptor diToolDescriptor) {
        super(developmentConfiguration, diToolDescriptor);
        this.developmentConfiguration = developmentConfiguration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> executeInternal() {
        final Collection<Compartment> compartments = developmentConfiguration.getCompartments();
        final List<String> commands = new ArrayList<String>(compartments.size());

        for (final Compartment compartment : compartments) {
            commands.add(String.format("listdcs -b %s -s %s -m all", getBuildSpace(), compartment.getName()));
        }

        return commands;
    }
}
