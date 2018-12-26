/**
 *
 */
package org.arachna.netweaver.tools.dc;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.arachna.ant.AntHelper;
import org.arachna.netweaver.dc.types.BuildVariant;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dc.types.JdkHomeAlias;
import org.arachna.netweaver.dc.types.PublicPartReference;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit tests for {@link SyncDevelopmentComponentsInArchiveStateCommandBuilder}.
 *
 * @author Dirk Weigenand
 */
public class SyncDevelopmentComponentsInArchiveStateCommandBuilderTest {
    /**
     * Name of development component used at build time.
     */
    private static final String BUILD_TIME_DC = "build_time_dc";

    /**
     * Example Vendor.
     */
    private static final String VENDOR = "example.com";

    /**
     * SAP Vendor.
     */
    private static final String SAP_VENDOR = "sap.com";

    /**
     * Example software component.
     */
    private static final String EXAMPLE_SC = "example.com_EXAMPLE_SC_1";

    /**
     * Example SC with build time dependencies.
     */
    private static final String BUILD_DEPS_SC = "sap.com_BUILD_DEPS_SC_1";

    /**
     * dctool command builder under test.
     */
    private SyncDevelopmentComponentsInArchiveStateCommandBuilder builder;

    /**
     * registry for development components.
     */
    private DevelopmentComponentFactory dcFactory;

    /**
     */
    @Before
    public void setUp() {
        dcFactory = new DevelopmentComponentFactory();

        Compartment compartment = Compartment.create(EXAMPLE_SC, CompartmentState.Source);
        final DevelopmentComponent component = dcFactory.create(VENDOR, "dc1");
        compartment.add(component);

        final DevelopmentConfiguration developmentConfiguration = createDevelopmentConfiguration();
        developmentConfiguration.add(compartment);
        final AntHelper antHelper = new AntHelper("", dcFactory);

        compartment = Compartment.create(BUILD_DEPS_SC, CompartmentState.Archive);
        final DevelopmentComponent buildTimeDC = dcFactory.create(SAP_VENDOR, BUILD_TIME_DC);
        compartment.add(buildTimeDC);
        component.add(new PublicPartReference(SAP_VENDOR, BUILD_TIME_DC));
        developmentConfiguration.add(compartment);

        builder = new SyncDevelopmentComponentsInArchiveStateCommandBuilder(developmentConfiguration, dcFactory, antHelper,
            Arrays.asList(component));
    }

    /**
     *
     */
    private DevelopmentConfiguration createDevelopmentConfiguration() {
        final DevelopmentConfiguration config = new DevelopmentConfiguration("DI0_Example_D");

        final BuildVariant buildVariant = new BuildVariant("default", true);
        buildVariant.addBuildOption(BuildVariant.COM_SAP_JDK_HOME_PATH_KEY, JdkHomeAlias.Jdk131Home.toString());

        config.setBuildVariant(buildVariant);

        return config;
    }

    @Test
    public void assertSynchronizerSynchronizesDCsInArchiveModeWhenDependencyIsFromSAPAndNotOnDisk() {
        final List<String> commands = builder.executeInternal();
        final DevelopmentComponent usedDC = dcFactory.get(SAP_VENDOR, BUILD_TIME_DC);
        assertThat(commands.size(), equalTo(1));
        final String expected = SyncDcCommandTemplate.V70.createSyncArchiveDCCommand(usedDC);
        assertThat(commands.get(0), equalTo(expected));
    }
}
