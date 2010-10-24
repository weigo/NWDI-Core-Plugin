/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

/**
 * Principal.
 * 
 * @author G526521
 */
public class Principal {
    private final String user;

    /**
     * Create an instance of a principal with the given user.
     * 
     * @param user
     *            the users name for this principal.
     */
    public Principal(final String user) {
        this.user = user;
    }

    /**
     * user name for this principal.
     * 
     * @return the user user name for this principal
     */
    public String getUser() {
        return user;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Principal [user=" + user + "]";
    }
}
