/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test for {@link ActivityDetailParser}.
 * 
 * @author Dirk Weigenand
 */
public class ActivityDetailParserTest {
    /**
     * activity to update.
     */
    private Activity activity;

    /**
     * parser under test.
     */
    private ActivityDetailParser parser;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        this.activity =
            new Activity(null, null, null, Calendar.getInstance().getTime());
        this.parser = new ActivityDetailParser(activity);
        this.parser.parse(this.getClass().getResourceAsStream("ResourceDetails.htm"));
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        this.activity = null;
    }

    /**
     * Test method for {@link org.arachna.netweaver.hudson.dtr.browser.ActivityDetailParser#parse(java.io.InputStream)} .
     */
    @Test
    public final void testParseLongDescription() {
        assertThat(this.activity.getDescription(), is(equalTo("LongDescription")));
    }
}
