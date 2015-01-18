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
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit tests for {@link SyncDevelopmentComponentsInArchiveStateCommandBuilder}.
 *
 * @author Dirk Weigenand
 */
public class SyncDevelopmentComponentsInArchiveStateCommandBuilderTest {
    /**
     *
     */
    private static final String SYNCDC_IN_ARCHIVE_MODE_TEMPLATE = "syncdc -s %s -n %s -v %s -m archive --syncused;";

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
    private SyncDevelopmentComponentsInArchiveStateCommandBuilder builder;

    /**
     * registry for development components.
     */
    private DevelopmentComponentFactory dcFactory;

    /**
     * example development component.
     */
    private DevelopmentComponent component;

    /**
     */
    @Before
    public void setUp() {
        dcFactory = new DevelopmentComponentFactory();

        final Compartment compartment = Compartment.create(EXAMPLE_SC, CompartmentState.Archive);
        final DevelopmentComponent component = dcFactory.create(VENDOR, "dc1");
        compartment.add(component);
        final AntHelper antHelper = new AntHelper("", dcFactory);
        builder =
            new SyncDevelopmentComponentsInArchiveStateCommandBuilder(createDevelopmentConfiguration(), dcFactory, antHelper,
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
        final DevelopmentComponent usedDC = dcFactory.get(VENDOR, "dc1");
        assertThat(commands.size(), equalTo(1));
        final String expected = String.format(SYNCDC_IN_ARCHIVE_MODE_TEMPLATE, EXAMPLE_SC, usedDC.getName(), usedDC.getVendor());
        assertThat(commands.get(0), equalTo(expected));
    }
}
