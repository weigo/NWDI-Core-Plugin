/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import hudson.Util;

import org.kohsuke.stapler.StaplerRequest;

/**
 * @author Dirk Weigenand
 * 
 */
final class ParameterHelper {
    /**
     * {@link StaplerRequest} to operate on.
     */
    final StaplerRequest request;

    /**
     * Create an instance of {@link ParameterHelper} with the given
     * {@link StaplerRequest}.
     * 
     * @param request
     *            {@link StaplerRequest} to operate on.
     */
    ParameterHelper(final StaplerRequest request) {
        this.request = request;
    }

    /**
     * Returns the value matching the given parameter name.
     * 
     * If the parameter is not contained in the {@link StaplerRequest}, an empty
     * string is returned.
     * 
     * @param name
     *            parameter name
     * @return value matching the given parameter name.
     */
    String getParameter(final String name) {
        return Util.fixNull(this.request.getParameter(name));
    }
}
