/**
 * 
 */
package org.arachna.netweaver.dctool.commands;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
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

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        config = new DevelopmentConfiguration("DI0_Example_D");
        compartment = new Compartment(EXAMPLE_SC, CompartmentState.Source, VENDOR, "", "EXAMPLE_SC");
        config.add(compartment);
        component = new DevelopmentComponent("dc/example1", VENDOR);
        compartment.add(component);
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dctool.commands.SyncDevelopmentComponentsCommandBuilder#synchronizeDCsNeedingRebuild()}
     * .
     */
    @Test
    public final void testSynchronizeDCsNeedingRebuildV70() {
        builder = new SyncDevelopmentComponentsCommandBuilder(config, SyncDcCommandTemplate.SyncDcCommandTemplateV70);
        compartment.setState(CompartmentState.Source);
        component.setNeedsRebuild(true);
        Collection<String> commands = builder.synchronizeDCsNeedingRebuild();
        assertThat(commands, hasSize(2));
        assertThat(commands,
            hasItem("syncdc -s example.com_EXAMPLE_SC_1 -n dc/example1 -v example.com -m inactive -y;"));
        assertThat(commands, hasItem("unsyncdc -s example.com_EXAMPLE_SC_1 -n dc/example1 -v example.com;"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dctool.commands.SyncDevelopmentComponentsCommandBuilder#synchronizeDCsNeedingRebuild()}
     * .
     */
    @Test
    public final void testSynchronizeDCsNeedingRebuildV71() {
        builder = new SyncDevelopmentComponentsCommandBuilder(config, SyncDcCommandTemplate.SyncDcCommandTemplateV71);
        compartment.setState(CompartmentState.Source);
        component.setNeedsRebuild(true);
        Collection<String> commands = builder.synchronizeDCsNeedingRebuild();
        assertThat(commands, hasSize(2));
        assertThat(commands, hasItem("syncdc -c example.com_EXAMPLE_SC_1 -n dc/example1 -v example.com -m inactive -f"));
        assertThat(commands, hasItem("unsyncdc -c example.com_EXAMPLE_SC_1 -n dc/example1 -v example.com"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dctool.commands.SyncDevelopmentComponentsCommandBuilder#synchronizeCompartmentsInArchiveMode(java.util.List)}
     * .
     */
    @Test
    public final void testSynchronizeCompartmentsInArchiveMode() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dctool.commands.SyncDevelopmentComponentsCommandBuilder#createSyncDcsInArchiveModeCommand(org.arachna.netweaver.dc.types.Compartment)}
     * .
     */
    @Test
    public final void testCreateSyncDcsInArchiveModeCommandV70() {
        builder = new SyncDevelopmentComponentsCommandBuilder(config, SyncDcCommandTemplate.SyncDcCommandTemplateV70);
        assertThat(builder.createSyncDcsInArchiveModeCommand(compartment),
            is(equalTo("syncalldcs -s example.com_EXAMPLE_SC_1 -m archive;")));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dctool.commands.SyncDevelopmentComponentsCommandBuilder#createSyncDcsInArchiveModeCommand(org.arachna.netweaver.dc.types.Compartment)}
     * .
     */
    @Test
    public final void testCreateSyncDcsInArchiveModeCommandV71() {
        builder = new SyncDevelopmentComponentsCommandBuilder(config, SyncDcCommandTemplate.SyncDcCommandTemplateV71);
        assertThat(builder.createSyncDcsInArchiveModeCommand(compartment),
            is(equalTo("syncalldcs -c example.com_EXAMPLE_SC_1 -m archive")));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dctool.commands.SyncDevelopmentComponentsCommandBuilder#createSyncInactiveDCCommand(org.arachna.netweaver.dc.types.DevelopmentComponent)}
     * .
     */
    @Test
    public final void testCreateSyncInactiveDCCommandV70() {
        builder = new SyncDevelopmentComponentsCommandBuilder(config, SyncDcCommandTemplate.SyncDcCommandTemplateV70);
        assertThat(builder.createSyncInactiveDCCommand(component),
            is(equalTo("syncdc -s example.com_EXAMPLE_SC_1 -n dc/example1 -v example.com -m inactive -y;")));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dctool.commands.SyncDevelopmentComponentsCommandBuilder#createSyncInactiveDCCommand(org.arachna.netweaver.dc.types.DevelopmentComponent)}
     * .
     */
    @Test
    public final void testCreateSyncInactiveDCCommandV71() {
        builder = new SyncDevelopmentComponentsCommandBuilder(config, SyncDcCommandTemplate.SyncDcCommandTemplateV71);
        assertThat(builder.createSyncInactiveDCCommand(component),
            is(equalTo("syncdc -c example.com_EXAMPLE_SC_1 -n dc/example1 -v example.com -m inactive -f")));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dctool.commands.SyncDevelopmentComponentsCommandBuilder#createUnsyncDCCommand(org.arachna.netweaver.dc.types.DevelopmentComponent)}
     * .
     */
    @Test
    public final void testCreateUnsyncDCCommandV70() {
        builder = new SyncDevelopmentComponentsCommandBuilder(config, SyncDcCommandTemplate.SyncDcCommandTemplateV70);
        assertThat(builder.createUnsyncDCCommand(component),
            is(equalTo("unsyncdc -s example.com_EXAMPLE_SC_1 -n dc/example1 -v example.com;")));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dctool.commands.SyncDevelopmentComponentsCommandBuilder#createUnsyncDCCommand(org.arachna.netweaver.dc.types.DevelopmentComponent)}
     * .
     */
    @Test
    public final void testCreateUnsyncDCCommandV71() {
        builder = new SyncDevelopmentComponentsCommandBuilder(config, SyncDcCommandTemplate.SyncDcCommandTemplateV71);
        assertThat(builder.createUnsyncDCCommand(component),
            is(equalTo("unsyncdc -c example.com_EXAMPLE_SC_1 -n dc/example1 -v example.com")));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dctool.commands.SyncDevelopmentComponentsCommandBuilder#getExitCommand(org.arachna.netweaver.dc.types.DevelopmentComponent)}
     * .
     */
    @Test
    public final void testGetExitCommandV70() {
        builder = new SyncDevelopmentComponentsCommandBuilder(config, SyncDcCommandTemplate.SyncDcCommandTemplateV70);
        assertThat(builder.getExitCommand(), is(equalTo("exit;")));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dctool.commands.SyncDevelopmentComponentsCommandBuilder#getExitCommand(org.arachna.netweaver.dc.types.DevelopmentComponent)}
     * .
     */
    @Test
    public final void testGetExitCommandV71() {
        builder = new SyncDevelopmentComponentsCommandBuilder(config, SyncDcCommandTemplate.SyncDcCommandTemplateV71);
        assertThat(builder.getExitCommand(), is(equalTo("exit")));
    }
}
