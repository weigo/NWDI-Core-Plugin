/**
 *
 */
package org.arachna.netweaver.tools.dc;

import java.util.LinkedList;
import java.util.List;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;

/**
 * DC tool command builder for synchronizing development components in source state.
 *
 * @author Dirk Weigenand
 */
public class SyncDevelopmentComponentsInSourceStateCommandBuilder extends AbstractDCToolCommandBuilder {
    /**
     * Flag to indicate whether development components should be synchronized regardless of its {@see DevelopmentComponent#isNeedsRebuild()}
     * state.
     */
    private final boolean cleanCopy;

    /**
     * provider of templates for (un-)sync DC commands.
     */
    private final SyncDcCommandTemplate template;

    /**
     * create a builder for development component listing and syncing commands.
     *
     * @param developmentConfiguration
     *            development configuration to synchronize development components for.
     * @param cleanCopy
     *            indicate whether a clean copy of the workspace is needed.
     */
    SyncDevelopmentComponentsInSourceStateCommandBuilder(final DevelopmentConfiguration developmentConfiguration, final boolean cleanCopy) {
        super(developmentConfiguration);
        this.cleanCopy = cleanCopy;
        template = SyncDcCommandTemplate.create(developmentConfiguration.getJdkHomeAlias());
    }

    /**
     * Create commands for synchronizing DCs that need to be rebuilt.
     *
     * @return collection of commands for synchronizing DCs that need to be rebuilt.
     */
    @Override
    protected List<String> executeInternal() {
        final DevelopmentConfiguration developmentConfiguration = getDevelopmentConfiguration();
        final List<String> commands = new LinkedList<String>();

        for (final Compartment compartment : developmentConfiguration.getCompartments(CompartmentState.Source)) {
            if (cleanCopy) {
                commands.add(template.createSyncDcsInInActiveModeCommand(compartment));
            }
            else {
                for (final DevelopmentComponent component : compartment.getDevelopmentComponents()) {
                    if (component.isNeedsRebuild()) {
                        commands.add(template.createUnsyncDCCommand(component));
                        commands.add(template.createSyncInactiveDCCommand(component));
                    }
                }
            }
        }

        return commands;
    }
}
