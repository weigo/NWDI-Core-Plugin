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
import org.arachna.netweaver.dc.types.PublicPartReference;
import org.arachna.netweaver.tools.dc.SyncDevelopmentComponentsCommandBuilder;
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
    private SyncDevelopmentComponentsCommandBuilder builder;

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
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        dcFactory = new DevelopmentComponentFactory();

        setUpDevelopmentConfiguration();

        compartment = new Compartment(EXAMPLE_SC, CompartmentState.Source, VENDOR, "", "EXAMPLE_SC");
        config.add(compartment);
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
     * Synchronize DCs of one compartment in source state. Use clean copy.
     * Verify commands for NW 7.0.
     */
    @Test
    public void synchronizeDCsInSourceStateWhithCleanCopyForNW70() {
        builder = createSyncDevelopmentComponentCommandBuilder(true, true);
        final List<String> commands = builder.executeInternal();
        final String expected = String.format("syncalldcs -s %s -m inactive;", compartment.getName());
        assertThat(commands.get(0), equalTo(expected));
    }

    /**
     * Synchronize DCs of one compartment in source state. Use clean copy.
     * Verify commands for NW 7.0.
     */
    @Test
    public void synchronizeEmptyCompartmentInSourceStateWhithoutCleanCopyForNW70ReturnsNoCommands() {
        builder = createSyncDevelopmentComponentCommandBuilder(true, false);
        final List<String> commands = builder.executeInternal();
        assertThat(commands.size(), equalTo(0));
    }

    /**
     * Synchronize DCs of one compartment in source state. Use clean copy.
     * Verify commands for NW 7.0.
     */
    @Test
    public void synchronizeEmptyCompartmentInSourceStateWhithoutCleanCopyForNW70ReturnsOneCommand() {
        builder = createSyncDevelopmentComponentCommandBuilder(true, false);
        final DevelopmentComponent component = dcFactory.create("example.com", "dc1");
        component.setNeedsRebuild(true);
        compartment.add(component);
        final List<String> commands = builder.executeInternal();
        String expected =
            String.format("unsyncdc -s %s -n %s -v %s;", compartment.getName(), component.getName(),
                component.getVendor());
        assertThat(commands.get(0), equalTo(expected));
        expected =
            String.format("syncdc -s %s -n %s -v %s -m inactive -y;", compartment.getName(), component.getName(),
                component.getVendor());
        assertThat(commands.get(1), equalTo(expected));
    }

    @Test
    public void synchronizeComponentsInArchiveStateWhithoutCleanCopyForNW70ReturnsOneCommand() {
        builder = createSyncDevelopmentComponentCommandBuilder(false, false);
        final DevelopmentComponent component = dcFactory.create(VENDOR, "dc1");
        component.setNeedsRebuild(true);
        compartment.add(component);

        final DevelopmentComponent library = dcFactory.create(APACHE_ORG, "commons");
        component.add(new PublicPartReference(library.getVendor(), library.getName()));
        compartment = createCompartment(APACHE_ORG, "COMMONS", CompartmentState.Archive);
        compartment.add(library);
        config.add(compartment);

        final List<String> commands = builder.executeInternal();
        assertThat(commands.size(), equalTo(1));
        final String expected =
            String.format("syncdc -s %s -n %s -v %s -m archive --syncused;", compartment.getName(), library.getName(),
                library.getVendor());
        assertThat(commands.get(0), equalTo(expected));
    }

    @Test
    public void dontSynchronizeBuildPluginsWhithoutCleanCopyForNW70() {
        builder = createSyncDevelopmentComponentCommandBuilder(false, false);
        final DevelopmentComponent component = dcFactory.create(VENDOR, "dc1");
        component.setNeedsRebuild(true);
        compartment.add(component);

        final DevelopmentComponent buildPlugin = dcFactory.create("sap.com", "tc/bi/webDynpro");
        component.add(new PublicPartReference(buildPlugin.getVendor(), buildPlugin.getName()));
        compartment = createCompartment("sap.com", "SAP_BUILDT", CompartmentState.Archive);
        compartment.add(buildPlugin);
        config.add(compartment);

        final List<String> commands = builder.executeInternal();
        assertThat(commands.size(), equalTo(0));
    }

    @Test
    public void synchronizeCompartmentsInArchiveStateWithCleanCopyForNW70OnlySyncsBuildInfraStructureCompartments() {
        builder = createSyncDevelopmentComponentCommandBuilder(false, true);
        final DevelopmentComponent component = dcFactory.create(VENDOR, "dc1");
        component.setNeedsRebuild(true);
        compartment.add(component);

        final Compartment commons = createCompartment(APACHE_ORG, "COMMONS", CompartmentState.Archive);
        config.add(commons);

        final Compartment sapBuildt = createCompartment("sap.com", "SAP_BUILDT", CompartmentState.Archive);
        config.add(sapBuildt);

        final List<String> commands = builder.executeInternal();
        assertThat(commands.size(), equalTo(1));
        assertThat(commands.get(0), equalTo(String.format("syncalldcs -s %s -m archive;", sapBuildt.getName())));
    }

    /**
     * Create a command builder indicating whether to synchronize sources or
     * archives and to synchronize complete compartments.
     * 
     * @return
     */
    protected SyncDevelopmentComponentsCommandBuilder createSyncDevelopmentComponentCommandBuilder(
        final boolean syncSources, final boolean cleanCopy) {
        return new SyncDevelopmentComponentsCommandBuilder(config, dcFactory, syncSources, cleanCopy);
    }

    private Compartment createCompartment(final String vendor, final String name, final CompartmentState state) {
        return new Compartment(String.format("%s_%s_1", vendor, name), state, vendor, "", name);

    }
}
