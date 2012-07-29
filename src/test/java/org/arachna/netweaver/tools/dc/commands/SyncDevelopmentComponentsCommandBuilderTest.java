/**
 * 
 */
package org.arachna.netweaver.tools.dc.commands;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.arachna.netweaver.dc.types.BuildVariant;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dc.types.JdkHomeAlias;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit tests for {@link SyncDevelopmentComponentsCommandBuilder}.
 * 
 * @author Dirk Weigenand
 */
public class SyncDevelopmentComponentsCommandBuilderTest {
    /**
     * 
     */
    private static final String VENDOR = "example.com";

    /**
     * Example software component.
     */
    private static final String EXAMPLE_SC = "example.com_EXAMPLE_SC_1";

    /**
     * dctool command builder under test.
     */
    private SyncDevelopmentComponentsCommandBuilder builder;

    /**
     * development configuration to use throughout test.
     */
    private DevelopmentConfiguration config;

    /**
     * compartment to use throughout test.
     */
    private Compartment compartment;

    private DevelopmentComponent component;

    private DevelopmentComponentFactory dcFactory;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        dcFactory = new DevelopmentComponentFactory();

        setUpDevelopmentConfiguration();

        compartment = new Compartment(EXAMPLE_SC, CompartmentState.Source, VENDOR, "", "EXAMPLE_SC");
        config.add(compartment);

        component = dcFactory.create(VENDOR, "dc/example1");
        compartment.add(component);
    }

    /**
     * 
     */
    protected void setUpDevelopmentConfiguration() {
        config = new DevelopmentConfiguration("DI0_Example_D");

        final BuildVariant buildVariant = new BuildVariant("default");
        buildVariant.addBuildOption(DevelopmentConfiguration.COM_SAP_JDK_HOME_PATH_KEY,
            JdkHomeAlias.Jdk131Home.toString());

        config.setBuildVariant(buildVariant);
    }

    /**
     * Synchronize DCs of one
     */
    @Test
    public void synchronizeDCsInSourceStateWhithCleanCopy() {
        builder = createSyncDevelopmentComponentCommandBuilder(true, true);
        final List<String> commands = builder.executeInternal();
        final String expected = String.format("syncalldcs -s %s -m inactive;", compartment.getName());
        assertThat(commands.get(0), equalTo(expected));
    }

    /**
     * Create a command builder indicating whether to synchronize sources or
     * archives and to synchronize complete compartments.
     * 
     * @return
     */
    protected SyncDevelopmentComponentsCommandBuilder createSyncDevelopmentComponentCommandBuilder(
        final boolean syncSources, final boolean cleanCopy) {
        return new SyncDevelopmentComponentsCommandBuilder(config, dcFactory, SyncDcCommandTemplate.create(config
            .getJdkHomeAlias()), syncSources, cleanCopy);
    }
}
