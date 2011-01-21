/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit tests for {@link ResourceDetailsParser}.
 * 
 * @author G526521
 */
public class ResourceDetailsParserTest {
    /**
     * {@link ActivityResource} that shall be updated from the
     * resourceDetailsPage.
     */
    private ActivityResource resource;

    /**
     * parse for details page.
     */
    private ResourceDetailsParser parser;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        this.resource =
            new ActivityResource(new Activity("", new Principal(""), "", Calendar.getInstance().getTime()),
                new DevelopmentComponent("", ""), "", "");
        this.parser = new ResourceDetailsParser(this.resource);
    }

    /**
     *
     */
    private InputStream getResourceDetailsPage(final String pageName) {
        return this.getClass().getResourceAsStream(pageName);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        this.resource = null;
        this.parser = null;
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.dtr.browser.ResourceDetailsParser#parse(java.io.InputStream)}
     * .
     */
    @Test
    public final void testParseDeleteStateIsFalse() {
        this.parser.parse(this.getResourceDetailsPage("ResourceDetails1.html"));

        assertThat(this.resource.isDeleted(), is(Boolean.FALSE));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.dtr.browser.ResourceDetailsParser#parse(java.io.InputStream)}
     * .
     */
    @Test
    public final void testParseDeleteStateIsTrue() {
        this.parser.parse(this.getResourceDetailsPage("ResourceDetails2.html"));

        assertThat(this.resource.isDeleted(), is(Boolean.TRUE));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.dtr.browser.ResourceDetailsParser#parse(java.io.InputStream)}
     * .
     * 
     * @throws ParseException
     */
    @Test
    public final void testParseCreationDate() throws ParseException {
        this.parser.parse(this.getResourceDetailsPage("ResourceDetails1.html"));

        final SimpleDateFormat format = new SimpleDateFormat(ActivityListParser.ACTIVITY_DATE_FORMAT);
        final Date creationDate = format.parse("12.01.2011 11:58:31 GMT");
        assertThat(this.resource.getCreationDate(), is(equalTo(creationDate)));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.dtr.browser.ResourceDetailsParser#parse(java.io.InputStream)}
     * .
     */
    @Test
    public final void testParseLastModified() throws ParseException {
        this.parser.parse(this.getResourceDetailsPage("ResourceDetails1.html"));
        final SimpleDateFormat format = new SimpleDateFormat(ActivityListParser.ACTIVITY_DATE_FORMAT);
        final Date lastModified = format.parse("12.01.2011 12:02:33 GMT");

        assertThat(this.resource.getLastModified(), is(equalTo(lastModified)));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.dtr.browser.ResourceDetailsParser#parse(java.io.InputStream)}
     * .
     */
    @Test
    public final void testParseSequenceNumber() {
        this.parser.parse(this.getResourceDetailsPage("ResourceDetails1.html"));

        assertThat(this.resource.getSequenceNumber(), is(equalTo(Integer.valueOf(1))));
    }
}
