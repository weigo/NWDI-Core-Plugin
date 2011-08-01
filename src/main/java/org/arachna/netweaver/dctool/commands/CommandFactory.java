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
 * @author g526521
 */
public final class CommandFactory {
    /**
     * Creates a DC tool command builder for building synchronizing development
     * components commands.
     * 
     * @param configuration
     *            development configuration to synchronize.
     * @return
     */
    public DCToolCommandBuilder createSyncDevelopmentComponentsCommandBuilder(DevelopmentConfiguration configuration) {
        JdkHomeAlias alias = configuration.getJdkHomeAlias();
        DCToolCommandBuilder builder = null;

        if (JdkHomeAlias.Jdk131Home.equals(alias) || JdkHomeAlias.Jdk142Home.equals(alias)) {
            builder =
                new SyncDevelopmentComponentsCommandBuilder(configuration,
                    SyncDcCommandTemplate.SyncDcCommandTemplateV70);
        }
        else if (JdkHomeAlias.Jdk150Home.equals(alias) || JdkHomeAlias.Jdk160Home.equals(alias)) {
            builder =
                new SyncDevelopmentComponentsCommandBuilder(configuration,
                    SyncDcCommandTemplate.SyncDcCommandTemplateV71);
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
     * @param logger
     * @return
     */
    public DCToolCommandBuilder createBuildDevelopmentComponentsCommandBuilder(DevelopmentConfiguration configuration,
        Collection<DevelopmentComponent> affectedComponents) {
        JdkHomeAlias alias = configuration.getJdkHomeAlias();
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
     * @return
     */
    public DCToolCommandBuilder createListDevelopmentComponentsCommandBuilder(DevelopmentConfiguration configuration) {
        JdkHomeAlias alias = configuration.getJdkHomeAlias();
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
    public DCToolCommandBuilder createLoadConfigCommandBuilder(DevelopmentConfiguration configuration,
        DCToolDescriptor dcToolDescriptor) {
        JdkHomeAlias alias = configuration.getJdkHomeAlias();
        DCToolCommandBuilder builder = null;

        if (JdkHomeAlias.Jdk131Home.equals(alias) || JdkHomeAlias.Jdk142Home.equals(alias)) {
            builder = new LoadConfigCommandBuilderV70(dcToolDescriptor);
        }
        else if (JdkHomeAlias.Jdk150Home.equals(alias) || JdkHomeAlias.Jdk160Home.equals(alias)) {
            builder = new LoadConfigCommandBuilderV71(dcToolDescriptor);
        }
        else {
            throw new RuntimeException("Cannot map configuration.getJdkHomeAlias() onto a DCToolCommandBuilder.");
        }

        return builder;
    }

    public AbstractDcToolOutputReader getListDcCommandResultReader(Reader output,
        DevelopmentComponentFactory dcFactory, DevelopmentConfiguration configuration) {
        JdkHomeAlias alias = configuration.getJdkHomeAlias();
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
