package org.arachna.netweaver.hudson.dtr.browser;

/**
 * Interface for filters on instances of {@link Activity}.
 * 
 * @author Dirk Weigenand
 */
interface ActivityFilter {
    /**
     * Test whether the given {@link Activity} passes a certain test implemented by implementors of this interface.
     * 
     * @param activity
     *            activity to test
     * @return <code>true</code> when the activity passes the test, <code>false</code> otherwise.
     */
    boolean accept(Activity activity);
}
