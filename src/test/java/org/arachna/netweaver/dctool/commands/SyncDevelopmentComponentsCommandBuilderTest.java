/**
 * 
 */
package org.arachna.netweaver.dctool.commands;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dctool.commands.SyncDcCommandTemplate.CompartmentsReader;
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
     * {@link org.arachna.netweaver.dctool.commands.SyncDevelopmentComponentsCommandBuilder#executeInternal()}
     * .
     */
    @Test
    public final void testExecuteInternalV70() {
        builder =
            new SyncDevelopmentComponentsCommandBuilder(config, SyncDcCommandTemplate.SyncDcCommandTemplateV70, false);
        compartment.setState(CompartmentState.Source);
        component.setNeedsRebuild(true);
        config.add(new Compartment("example.com_EXAMPLE_SC_2", CompartmentState.Archive, VENDOR, "", "EXAMPLE_SC_2"));

        final String commands[] = builder.executeInternal().toArray(new String[0]);
        assertThat(commands.length, is(equalTo(4)));
        assertThat(commands[0], is(equalTo("unsyncdc -s example.com_EXAMPLE_SC_1 -n dc/example1 -v example.com;")));
        assertThat(commands[1],
            is(equalTo("syncdc -s example.com_EXAMPLE_SC_1 -n dc/example1 -v example.com -m inactive -y;")));
        assertThat(commands[2], is(equalTo("syncalldcs -s example.com_EXAMPLE_SC_2 -m archive;")));
        assertThat(commands[3], is(equalTo("exit;")));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dctool.commands.SyncDevelopmentComponentsCommandBuilder#executeInternal()}
     * .
     */
    @Test
    public final void testExecuteInternalV71() {
        builder =
            new SyncDevelopmentComponentsCommandBuilder(config, SyncDcCommandTemplate.SyncDcCommandTemplateV71, false);
        compartment.setState(CompartmentState.Source);
        component.setNeedsRebuild(true);
        config.add(new Compartment("example.com_EXAMPLE_SC_2", CompartmentState.Archive, VENDOR, "", "EXAMPLE_SC_2"));

        final String commands[] = builder.executeInternal().toArray(new String[0]);
        assertThat(commands.length, is(equalTo(4)));
        assertThat(commands[0], is(equalTo("unsyncdc -c example.com_EXAMPLE_SC_1 -n dc/example1 -v example.com")));
        assertThat(commands[1],
            is(equalTo("syncdc -c example.com_EXAMPLE_SC_1 -n dc/example1 -v example.com -m inactive -f")));
        assertThat(commands[2], is(equalTo("syncalldcs -c example.com_EXAMPLE_SC_2 -m archive")));
        assertThat(commands[3], is(equalTo("exit")));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dctool.commands.SyncDevelopmentComponentsCommandBuilder#synchronizeDCsNeedingRebuild70()}
     * .
     */
    @Test
    public final void testSynchronizeDCsNeedingRebuildV70() {
        builder =
            new SyncDevelopmentComponentsCommandBuilder(config, SyncDcCommandTemplate.SyncDcCommandTemplateV70, false);
        compartment.setState(CompartmentState.Source);
        component.setNeedsRebuild(true);
        final String commands[] = builder.synchronizeDCsNeedingRebuild().toArray(new String[0]);
        assertThat(commands.length, is(equalTo(2)));
        assertThat(commands[0], is(equalTo("unsyncdc -s example.com_EXAMPLE_SC_1 -n dc/example1 -v example.com;")));
        assertThat(commands[1],
            is(equalTo("syncdc -s example.com_EXAMPLE_SC_1 -n dc/example1 -v example.com -m inactive -y;")));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dctool.commands.SyncDevelopmentComponentsCommandBuilder#synchronizeDCsNeedingRebuild70()}
     * .
     */
    @Test
    public final void testSynchronizeDCsNeedingRebuildWithOneDCNeedingRebuildAndOneUnTouchedV70() {
        builder =
            new SyncDevelopmentComponentsCommandBuilder(config, SyncDcCommandTemplate.SyncDcCommandTemplateV70, false);
        compartment.setState(CompartmentState.Source);
        component.setNeedsRebuild(true);
        compartment.add(new DevelopmentComponent("dc/example2", VENDOR));

        final String commands[] = builder.synchronizeDCsNeedingRebuild().toArray(new String[0]);
        assertThat(commands.length, is(equalTo(2)));
        assertThat(commands[0], is(equalTo("unsyncdc -s example.com_EXAMPLE_SC_1 -n dc/example1 -v example.com;")));
        assertThat(commands[1],
            is(equalTo("syncdc -s example.com_EXAMPLE_SC_1 -n dc/example1 -v example.com -m inactive -y;")));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dctool.commands.SyncDevelopmentComponentsCommandBuilder#synchronizeDCsNeedingRebuild71()}
     * .
     */
    @Test
    public final void testSynchronizeDCsNeedingRebuildV71() {
        builder =
            new SyncDevelopmentComponentsCommandBuilder(config, SyncDcCommandTemplate.SyncDcCommandTemplateV71, false);
        compartment.setState(CompartmentState.Source);
        component.setNeedsRebuild(true);
        final String commands[] = builder.synchronizeDCsNeedingRebuild().toArray(new String[0]);
        assertThat(commands.length, is(equalTo(2)));
        assertThat(commands[0], is(equalTo("unsyncdc -c example.com_EXAMPLE_SC_1 -n dc/example1 -v example.com")));
        assertThat(commands[1],
            is(equalTo("syncdc -c example.com_EXAMPLE_SC_1 -n dc/example1 -v example.com -m inactive -f")));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dctool.commands.SyncDevelopmentComponentsCommandBuilder#synchronizeCompartmentsInArchiveMode70(java.util.List)}
     * .
     * 
     * @throws IOException
     */
    @Test
    public final void testSynchronizeCompartmentsInArchiveModeV70InCleanCopyMode() throws IOException {
        builder =
            new SyncDevelopmentComponentsCommandBuilder(config, SyncDcCommandTemplate.SyncDcCommandTemplateV70, true);
        compartment.setState(CompartmentState.Archive);
        config.add(new Compartment("example.com_EXAMPLE_SC_2", CompartmentState.Archive, VENDOR, "", "EXAMPLE_SC_2"));
        addSapCompartments("SoftwareComponents70");
        final Collection<String> commands = builder.synchronizeCompartmentsInArchiveMode();
        assertThat(commands, hasSize(config.getCompartments().size()));
        assertThat(commands, hasItem("syncalldcs -s example.com_EXAMPLE_SC_1 -m archive;"));
        assertThat(commands, hasItem("syncalldcs -s example.com_EXAMPLE_SC_2 -m archive;"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dctool.commands.SyncDevelopmentComponentsCommandBuilder#synchronizeCompartmentsInArchiveMode71()}
     * .
     * 
     * @throws IOException
     */
    @Test
    public final void testSynchronizeCompartmentsInArchiveModeV71InCleanCopyMode() throws IOException {
        builder =
            new SyncDevelopmentComponentsCommandBuilder(config, SyncDcCommandTemplate.SyncDcCommandTemplateV71, true);
        compartment.setState(CompartmentState.Archive);
        config.add(new Compartment("example.com_EXAMPLE_SC_2", CompartmentState.Archive, VENDOR, "", "EXAMPLE_SC_2"));
        addSapCompartments("SoftwareComponents73");
        final Collection<String> commands = builder.synchronizeCompartmentsInArchiveMode();
        assertThat(commands, hasSize(config.getCompartments().size()));
        assertThat(commands, hasItem("syncalldcs -c example.com_EXAMPLE_SC_1 -m archive"));
        assertThat(commands, hasItem("syncalldcs -c example.com_EXAMPLE_SC_2 -m archive"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dctool.commands.SyncDevelopmentComponentsCommandBuilder#synchronizeCompartmentsInArchiveMode70(java.util.List)}
     * .
     * 
     * Standard mode means that no fresh checkout of the workspace was
     * requested. This should ignore the standard compartments provided by SAP.
     * 
     * @throws IOException
     */
    @Test
    public final void testSynchronizeCompartmentsInArchiveModeV70InStandardMode() throws IOException {
        builder =
            new SyncDevelopmentComponentsCommandBuilder(config, SyncDcCommandTemplate.SyncDcCommandTemplateV70, false);
        compartment.setState(CompartmentState.Archive);
        config.add(new Compartment("example.com_EXAMPLE_SC_2", CompartmentState.Archive, VENDOR, "", "EXAMPLE_SC_2"));

        addSapCompartments("SoftwareComponents70");

        final Collection<String> commands = builder.synchronizeCompartmentsInArchiveMode();
        assertThat(commands, hasSize(2));
        assertThat(commands, hasItem("syncalldcs -s example.com_EXAMPLE_SC_1 -m archive;"));
        assertThat(commands, hasItem("syncalldcs -s example.com_EXAMPLE_SC_2 -m archive;"));
    }

    /**
     * @param compartmentDirectory
     * @throws IOException
     */
    private void addSapCompartments(final String compartmentDirectory) throws IOException {
        final Collection<String> compartments = new LinkedList<String>();
        compartments.addAll(new CompartmentsReader().read("/org/arachna/netweaver/dctool/commands/"
            + compartmentDirectory));

        for (final String compartment : compartments) {
            config.add(new Compartment(compartment, CompartmentState.Archive, "sap.com", "", compartment));
        }
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dctool.commands.SyncDevelopmentComponentsCommandBuilder#synchronizeCompartmentsInArchiveMode71()}
     * . Standard mode means that no fresh checkout of the workspace was
     * requested. This should ignore the standard compartments provided by SAP.
     * 
     * @throws IOException
     */
    @Test
    public final void testSynchronizeCompartmentsInArchiveModeV71InStandardMode() throws IOException {
        builder =
            new SyncDevelopmentComponentsCommandBuilder(config, SyncDcCommandTemplate.SyncDcCommandTemplateV71, false);
        compartment.setState(CompartmentState.Archive);
        config.add(new Compartment("example.com_EXAMPLE_SC_2", CompartmentState.Archive, VENDOR, "", "EXAMPLE_SC_2"));
        addSapCompartments("SoftwareComponents73");

        final Collection<String> commands = builder.synchronizeCompartmentsInArchiveMode();
        assertThat(commands, hasSize(2));
        assertThat(commands, hasItem("syncalldcs -c example.com_EXAMPLE_SC_1 -m archive"));
        assertThat(commands, hasItem("syncalldcs -c example.com_EXAMPLE_SC_2 -m archive"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dctool.commands.SyncDevelopmentComponentsCommandBuilder#createSyncDcsInArchiveModeCommand(org.arachna.netweaver.dc.types.Compartment)}
     * .
     */
    @Test
    public final void testCreateSyncDcsInArchiveModeCommandV70() {
        builder =
            new SyncDevelopmentComponentsCommandBuilder(config, SyncDcCommandTemplate.SyncDcCommandTemplateV70, false);
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
        builder =
            new SyncDevelopmentComponentsCommandBuilder(config, SyncDcCommandTemplate.SyncDcCommandTemplateV71, false);
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
        builder =
            new SyncDevelopmentComponentsCommandBuilder(config, SyncDcCommandTemplate.SyncDcCommandTemplateV70, false);
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
        builder =
            new SyncDevelopmentComponentsCommandBuilder(config, SyncDcCommandTemplate.SyncDcCommandTemplateV71, false);
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
        builder =
            new SyncDevelopmentComponentsCommandBuilder(config, SyncDcCommandTemplate.SyncDcCommandTemplateV70, false);
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
        builder =
            new SyncDevelopmentComponentsCommandBuilder(config, SyncDcCommandTemplate.SyncDcCommandTemplateV71, false);
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
        builder =
            new SyncDevelopmentComponentsCommandBuilder(config, SyncDcCommandTemplate.SyncDcCommandTemplateV70, false);
        assertThat(builder.getExitCommand(), is(equalTo("exit;")));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dctool.commands.SyncDevelopmentComponentsCommandBuilder#getExitCommand(org.arachna.netweaver.dc.types.DevelopmentComponent)}
     * .
     */
    @Test
    public final void testGetExitCommandV71() {
        builder =
            new SyncDevelopmentComponentsCommandBuilder(config, SyncDcCommandTemplate.SyncDcCommandTemplateV71, false);
        assertThat(builder.getExitCommand(), is(equalTo("exit")));
    }
}
