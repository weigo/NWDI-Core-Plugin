/**
 *
 */
package org.arachna.netweaver.tools.dc;

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
 * JUnit tests for {@link SyncDevelopmentComponentsInArchiveStateCommandBuilder}.
 *
 * @author Dirk Weigenand
 */
public class SyncDevelopmentComponentsInSourceStateCommandBuilderTest {
    /**
     *
     */
    private static final String APACHE_ORG = "apache.org";

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
    private SyncDevelopmentComponentsInSourceStateCommandBuilder builder;

    /**
     * development configuration to use throughout test.
     */
    private DevelopmentConfiguration config;

    /**
     * compartment to use throughout test.
     */
    private Compartment compartment;

    private DevelopmentComponentFactory dcFactory;

    /**
     */
    @Before
    public void setUp() {
        dcFactory = new DevelopmentComponentFactory();

        setUpDevelopmentConfiguration();

        compartment = Compartment.create(EXAMPLE_SC, CompartmentState.Source);
        config.add(compartment);
    }

    /**
     *
     */
    protected void setUpDevelopmentConfiguration() {
        config = new DevelopmentConfiguration("DI0_Example_D");

        final BuildVariant buildVariant = new BuildVariant("default", true);
        buildVariant.addBuildOption(BuildVariant.COM_SAP_JDK_HOME_PATH_KEY, JdkHomeAlias.Jdk131Home.toString());

        config.setBuildVariant(buildVariant);
    }

    /**
     * Synchronize DCs of one compartment in source state. Use clean copy. Verify commands for NW 7.0.
     */
    @Test
    public void synchronizeDCsInSourceStateWhithCleanCopyForNW70() {
        builder = createSyncDevelopmentComponentCommandBuilder(true);
        final List<String> commands = builder.executeInternal();
        final String expected = String.format("syncalldcs -s %s -m inactive;", compartment.getName());
        assertThat(commands.get(0), equalTo(expected));
    }

    /**
     * Synchronize DCs of one compartment in source state. Use clean copy. Verify commands for NW 7.0.
     */
    @Test
    public void synchronizeEmptyCompartmentInSourceStateWhithoutCleanCopyForNW70ReturnsNoCommands() {
        builder = createSyncDevelopmentComponentCommandBuilder(false);
        final List<String> commands = builder.executeInternal();
        assertThat(commands.size(), equalTo(0));
    }

    /**
     * Synchronize DCs of one compartment in source state. Use clean copy. Verify commands for NW 7.0.
     */
    @Test
    public void synchronizeEmptyCompartmentInSourceStateWhithoutCleanCopyForNW70ReturnsOneCommand() {
        builder = createSyncDevelopmentComponentCommandBuilder(false);
        final DevelopmentComponent component = dcFactory.create("example.com", "dc1");
        component.setNeedsRebuild(true);
        compartment.add(component);
        final List<String> commands = builder.executeInternal();
        String expected = String.format("unsyncdc -s %s -n %s -v %s;", compartment.getName(), component.getName(), component.getVendor());
        assertThat(commands.get(0), equalTo(expected));
        expected =
            String.format("syncdc -s %s -n %s -v %s -m inactive -y;", compartment.getName(), component.getName(), component.getVendor());
        assertThat(commands.get(1), equalTo(expected));
    }

    /**
     * Create a command builder indicating whether to synchronize sources or archives and to synchronize complete compartments.
     *
     * @return
     */
    protected SyncDevelopmentComponentsInSourceStateCommandBuilder createSyncDevelopmentComponentCommandBuilder(final boolean cleanCopy) {
        return new SyncDevelopmentComponentsInSourceStateCommandBuilder(config, cleanCopy);
    }
}
