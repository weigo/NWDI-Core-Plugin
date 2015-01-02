/**
 *
 */
package org.arachna.netweaver.tools.dc;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.arachna.ant.AntHelper;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.PublicPartReference;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unittests for {@link DCsToSynchronizeCollector}.
 *
 * @author Dirk Weigenand
 */
public class DCsToSynchronizeCollectorTest {
    /**
     *
     */
    private static final String TC_BI_CORE = "tc/bi/core";

    /**
     *
     */
    private static final String SAP_COM = "sap.com";

    /**
     *
     */
    private static final String VENDOR = "example.com";

    /**
     * Instance under test.
     */
    private DCsToSynchronizeCollector collector;

    /**
     * Registry for development components.
     */
    private DevelopmentComponentFactory dcFactory;

    /**
     * Helper class for calculating the path to a DC in the workspace.
     */
    private AntHelper antHelper;

    /**
     * Component to check on.
     */
    private DevelopmentComponent component;

    /**
     * base folder for example SAP DC.
     */
    private File baseDir;

    /**
     * <code>.dcDef</code> file in example component folder.
     */
    private File dcDef;

    /**
     */
    @Before
    public void setUp() {
        antHelper = Mockito.mock(AntHelper.class);

        dcFactory = new DevelopmentComponentFactory();
        collector = new DCsToSynchronizeCollector(dcFactory, antHelper);
        final Compartment compartmentInSourceState = Compartment.create("example.com_EXAMPLE_SC_1", CompartmentState.Source);
        component = dcFactory.create(VENDOR, "dc");
        compartmentInSourceState.add(component);
        final Compartment compartmentInArchiveState = Compartment.create("example.com_EXAMPLE_SC_2", CompartmentState.Archive);
        compartmentInArchiveState.add(dcFactory.create(VENDOR, "dc2"));
        final Compartment sapBuildT = Compartment.create("sap.com_SAPBUILDT_1", CompartmentState.Archive);
        sapBuildT.add(dcFactory.create(SAP_COM, TC_BI_CORE));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.tools.dc.DCsToSynchronizeCollector#visit(org.arachna.netweaver.dc.types.DevelopmentComponent)}.
     */
    @Test
    public final void assertCollectorFindsNoUsedDCsWhenNoDependenciesExist() {
        assertThat(collector.execute(Arrays.asList(component)), empty());
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.tools.dc.DCsToSynchronizeCollector#visit(org.arachna.netweaver.dc.types.DevelopmentComponent)}.
     */
    @Test
    public final void assertCollectorFindsUsedDCsWhithNonSAPDependency() {
        final PublicPartReference ppRef = new PublicPartReference(VENDOR, "dc2");
        component.add(ppRef);

        final List<DevelopmentComponent> dCsToBeSynchronized = collector.execute(Arrays.asList(component));
        assertThat(dCsToBeSynchronized, hasSize(1));
        assertThat(dCsToBeSynchronized.get(0), equalTo(dcFactory.get(ppRef)));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.tools.dc.DCsToSynchronizeCollector#visit(org.arachna.netweaver.dc.types.DevelopmentComponent)}.
     */
    @Test
    public final void assertCollectorFindsUsedDCsWhithSAPDependencyNotOnDisk() {
        final PublicPartReference ppRef = new PublicPartReference(SAP_COM, TC_BI_CORE);
        component.add(ppRef);

        final List<DevelopmentComponent> dCsToBeSynchronized = collector.execute(Arrays.asList(component));
        assertThat(dCsToBeSynchronized, hasSize(1));
        assertThat(dCsToBeSynchronized.get(0), equalTo(dcFactory.get(ppRef)));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.tools.dc.DCsToSynchronizeCollector#visit(org.arachna.netweaver.dc.types.DevelopmentComponent)}.
     *
     * @throws IOException
     */
    @Test
    public final void assertCollectorFindsNoUsedDCsWhithSAPDependencyOnDisk() throws IOException {
        final PublicPartReference ppRef = new PublicPartReference(SAP_COM, TC_BI_CORE);
        component.add(ppRef);

        createTemporaryFiles();

        assertThat(collector.execute(Arrays.asList(component)), empty());

        removeTemporaryFiles();
    }

    /**
     *
     */
    private void removeTemporaryFiles() {
        dcDef.delete();
        baseDir.delete();
    }

    /**
     * @throws IOException
     */
    private void createTemporaryFiles() throws IOException {
        baseDir = new File(System.getProperty("java.io.tmpdir"), Long.toString(System.currentTimeMillis()));
        baseDir.mkdir();
        Mockito.when(antHelper.getBaseLocation(Mockito.any(DevelopmentComponent.class))).thenReturn(baseDir.getAbsolutePath());

        dcDef = new File(baseDir, ".dcdef");
        dcDef.createNewFile();
    }
}
