/**
 *
 */
package org.arachna.netweaver.tools.dc.commands;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.arachna.netweaver.dc.types.BuildVariant;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dc.types.JdkHomeAlias;
import org.arachna.netweaver.hudson.nwdi.ExampleDevelopmentComponentFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link BuildDevelopmentComponentsCommandBuilder}.
 * 
 * @author Dirk Weigenand
 */
public final class BuildDevelopmentComponentsCommandBuilderTest {
    /**
     * Example software component.
     */
    private static final String EXAMPLE_SC = "example.com_EXAMPLE_SC_1";
    /**
     * Registry for development components.
     */
    private DevelopmentComponentFactory dcFactory;

    private DevelopmentConfiguration config;

    /**
     * Set up development configuration, compartment and development components
     * to be used in test.
     */
    @Before
    public void setUp() {
        dcFactory = ExampleDevelopmentComponentFactory.create();
        setUpDevelopmentConfiguration();
    }

    /**
     * Build and return the build commands.
     * 
     * @param components
     *            array of DCs to generate build commands for.
     * 
     * @return the generated build commands.
     */
    protected List<String> createBuildCommands(final DevelopmentComponent[] components) {
        final Compartment compartment =
            new Compartment(EXAMPLE_SC, CompartmentState.Source, "example.com", "", "EXAMPLE_SC");
        config.add(compartment);
        final Collection<DevelopmentComponent> dCs = Arrays.asList(components);
        compartment.add(dCs);

        return new BuildDevelopmentComponentsCommandBuilder(config, dCs).execute();
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.tools.dc.commands.analyzer.dctool.BuildDevelopmentComponentsCommandBuilder#execute()}
     * .
     */
    @Test
    public void testBuildDcCommandGeneration() {
        final List<String> commands =
            createBuildCommands(new DevelopmentComponent[] { dcFactory.get(
                ExampleDevelopmentComponentFactory.EXAMPLE_COM, ExampleDevelopmentComponentFactory.LIB_JETM) });
        final String[] expected =
            new String[] { String.format("builddc -s %s -n %s -v %s -o;", EXAMPLE_SC,
                ExampleDevelopmentComponentFactory.LIB_JETM, ExampleDevelopmentComponentFactory.EXAMPLE_COM) };
        assertThat(commands, hasSize(expected.length));
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
}
