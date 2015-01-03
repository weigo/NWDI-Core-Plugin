/**
 * 
 */
package org.arachna.netweaver.hudson.nwdi.changelog;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

import java.io.InputStreamReader;
import java.util.Calendar;

import org.arachna.netweaver.hudson.nwdi.changelog.DtrChangeLogEntry.Action;
import org.arachna.netweaver.hudson.nwdi.changelog.DtrChangeLogEntry.Item;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link DtrChangeLogParser}.
 * 
 * @author Dirk Weigenand
 */
public class DtrChangeLogParserTest {
    /**
     * Instance under test.
     */
    private DtrChangeLogParser parser;

    /**
     * Resulting set of changes.
     */
    private DtrChangeLogSet changeSet;

    /**
     * Set up fixture.
     */
    @Before
    public void setUp() {
        parser = new DtrChangeLogParser();
        changeSet = new DtrChangeLogSet(null);
        parser.parse(
            changeSet,
            new InputStreamReader(this.getClass().getResourceAsStream(
                "/org/arachna/netweaver/hudson/nwdi/changelog/changelog.xml")));
    }

    /**
     * Tear down fixture.
     */
    @After
    public void tearDown() {
        parser = null;
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.changelog.DtrChangeLogParser#parse(hudson.model.AbstractBuild, java.io.File)}
     * .
     */
    @Test
    public final void assertParseChangeSetSetsCorrectActivityUrl() {
        final DtrChangeLogEntry entry = changeSet.iterator().next();
        assertThat(
            entry.getActivityUrl(),
            equalTo("/dtr/system-tools/reports/ResourceDetails?path=/act/default_w_XMPL_example_2e_org_UNIT_TEST_SUPPORT"
                + "_dev_inactive_u_weigo_t_2012_11_11_22_41_24_GMT_ebf9a8ae-2c50-11e2-8933-00e04da4ba37&technical=false"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.changelog.DtrChangeLogParser#parse(hudson.model.AbstractBuild, java.io.File)}
     * .
     */
    @Test
    public final void assertParseChangeSetSetsCorrectActivityDate() {
        final DtrChangeLogEntry entry = changeSet.iterator().next();
        final Calendar expectedCheckinTime = Calendar.getInstance();
        expectedCheckinTime.set(Calendar.MILLISECOND, 0);
        expectedCheckinTime.set(2012, Calendar.NOVEMBER, 11, 23, 46, 14);
        assertThat(entry.getCheckInTime(), equalTo(expectedCheckinTime.getTime()));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.changelog.DtrChangeLogParser#parse(hudson.model.AbstractBuild, java.io.File)}
     * .
     */
    @Test
    public final void assertParseChangeSetSetsCorrectUser() {
        final DtrChangeLogEntry entry = changeSet.iterator().next();
        assertThat(entry.getUser(), equalTo("weigo"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.changelog.DtrChangeLogParser#parse(hudson.model.AbstractBuild, java.io.File)}
     * .
     */
    @Test
    public final void assertParseChangeSetSetsCorrectComment() {
        final DtrChangeLogEntry entry = changeSet.iterator().next();
        assertThat(entry.getMsg(), equalTo("add hamcrest-library-1.3.jar to hamcrest DC"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.changelog.DtrChangeLogParser#parse(hudson.model.AbstractBuild, java.io.File)}
     * .
     */
    @Test
    public final void assertParseChangeSetSetsCorrectDescription() {
        final DtrChangeLogEntry entry = changeSet.iterator().next();
        assertThat(entry.getDescription(), equalTo("description"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.changelog.DtrChangeLogParser#parse(hudson.model.AbstractBuild, java.io.File)}
     * .
     */
    @Test
    public final void assertParseChangeSetAddsItems() {
        final DtrChangeLogEntry entry = changeSet.iterator().next();

        assertThat(
            entry.getItems(),
            containsInAnyOrder(new Item("hamcrest.org/hamcrest/comp_/libraries/hamcrest-library-1.3.jar", Action.ADD),
                new Item("hamcrest.org/hamcrest/comp_/libraries", Action.ADD)));
    }
}
