/**
 *
 */
package org.arachna.netweaver.dctool.commands;

import java.io.Reader;
import java.util.Collection;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dctool.DCToolDescriptor;
import org.arachna.netweaver.dctool.JdkHomeAlias;

/**
 * Factory for creation of DC tool commands.
 * 
 * Encapsulates knowledge which command to create depending on the build variant
 * used in a development configuration. This influences the DC tool version to
 * be used.
 * 
 * @author Dirk Weigenand
 */
public final class CommandFactory {
    /**
     * Creates a DC tool command builder for building synchronizing development
     * components commands.
     * 
     * @param configuration
     *            development configuration to synchronize.
     * @param cleanCopy
     *            indicate that the current build should operate on a fresh copy
     *            from the DTR.
     * @return a command builder for creating 'syncdc' commands.
     */
    public DCToolCommandBuilder createSyncDevelopmentComponentsCommandBuilder(
        final DevelopmentConfiguration configuration, final boolean cleanCopy) {
        final JdkHomeAlias alias = configuration.getJdkHomeAlias();
        DCToolCommandBuilder builder = null;

        if (JdkHomeAlias.Jdk131Home.equals(alias) || JdkHomeAlias.Jdk142Home.equals(alias)) {
            builder =
                new SyncDevelopmentComponentsCommandBuilder(configuration,
                    SyncDcCommandTemplate.SyncDcCommandTemplateV70, cleanCopy);
        }
        else if (JdkHomeAlias.Jdk150Home.equals(alias) || JdkHomeAlias.Jdk160Home.equals(alias)) {
            builder =
                new SyncDevelopmentComponentsCommandBuilder(configuration,
                    SyncDcCommandTemplate.SyncDcCommandTemplateV71, cleanCopy);
        }
        else {
            throw new RuntimeException("Cannot map configuration.getJdkHomeAlias() onto a DCToolCommandBuilder.");
        }

        return builder;
    }

    /**
     * Creates a DC tool command builder for building build development
     * components commands.
     * 
     * @param configuration
     *            development configuration to synchronize.
     * @param affectedComponents
     *            collections of development components to build.
     * @return a command builder for creating 'builddc' commands.
     */
    public DCToolCommandBuilder createBuildDevelopmentComponentsCommandBuilder(
        final DevelopmentConfiguration configuration, final Collection<DevelopmentComponent> affectedComponents) {
        final JdkHomeAlias alias = configuration.getJdkHomeAlias();
        DCToolCommandBuilder builder = null;

        if (JdkHomeAlias.Jdk131Home.equals(alias) || JdkHomeAlias.Jdk142Home.equals(alias)) {
            builder = new BuildDevelopmentComponentsCommandBuilderV70(configuration, affectedComponents);
        }
        else if (JdkHomeAlias.Jdk150Home.equals(alias) || JdkHomeAlias.Jdk160Home.equals(alias)) {
            builder = new BuildDevelopmentComponentsCommandBuilderV71(configuration, affectedComponents);
        }
        else {
            throw new RuntimeException("Cannot map configuration.getJdkHomeAlias() onto a DCToolCommandBuilder.");
        }

        return builder;
    }

    /**
     * Creates a DC tool command builder for building build development
     * components commands.
     * 
     * @param configuration
     *            development configuration to list contained DCs for.
     * @return a command builder for creating 'listdc' commands.
     */
    public DCToolCommandBuilder createListDevelopmentComponentsCommandBuilder(
        final DevelopmentConfiguration configuration) {
        final JdkHomeAlias alias = configuration.getJdkHomeAlias();
        DCToolCommandBuilder builder = null;

        if (JdkHomeAlias.Jdk131Home.equals(alias) || JdkHomeAlias.Jdk142Home.equals(alias)) {
            builder = new ListDcCommandBuilderV70(configuration);
        }
        else if (JdkHomeAlias.Jdk150Home.equals(alias) || JdkHomeAlias.Jdk160Home.equals(alias)) {
            builder = new ListDcCommandBuilderV71(configuration);
        }
        else {
            throw new RuntimeException("Cannot map configuration.getJdkHomeAlias() onto a DCToolCommandBuilder.");
        }

        return builder;
    }

    /**
     * Creates a DC tool command builder for building build development
     * components commands.
     * 
     * @param configuration
     *            development configuration to list contained DCs for.
     * @return
     */
    public DCToolCommandBuilder createLoadConfigCommandBuilder(final DevelopmentConfiguration configuration,
        final DCToolDescriptor dcToolDescriptor) {
        final JdkHomeAlias alias = configuration.getJdkHomeAlias();
        DCToolCommandBuilder builder = null;

        if (JdkHomeAlias.Jdk131Home.equals(alias) || JdkHomeAlias.Jdk142Home.equals(alias)) {
            builder = new LoadConfigCommandBuilder(dcToolDescriptor, LoadConfigTemplate.V70);
        }
        else if (JdkHomeAlias.Jdk150Home.equals(alias) || JdkHomeAlias.Jdk160Home.equals(alias)) {
            builder = new LoadConfigCommandBuilder(dcToolDescriptor, LoadConfigTemplate.V71);
        }
        else {
            throw new RuntimeException("Cannot map configuration.getJdkHomeAlias() onto a DCToolCommandBuilder.");
        }

        return builder;
    }

    public AbstractDcToolOutputReader getListDcCommandResultReader(final Reader output,
        final DevelopmentComponentFactory dcFactory, final DevelopmentConfiguration configuration) {
        final JdkHomeAlias alias = configuration.getJdkHomeAlias();
        AbstractDcToolOutputReader reader = null;

        if (JdkHomeAlias.Jdk131Home.equals(alias) || JdkHomeAlias.Jdk142Home.equals(alias)) {
            reader = new DevelopmentComponentsReader70(output, dcFactory, configuration);
        }
        else if (JdkHomeAlias.Jdk150Home.equals(alias) || JdkHomeAlias.Jdk160Home.equals(alias)) {
            reader = new DevelopmentComponentsReader71(output, dcFactory, configuration);
        }
        else {
            throw new RuntimeException("Cannot map configuration.getJdkHomeAlias() onto a DCToolCommandBuilder.");
        }

        return reader;
    }
}
