/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import hudson.model.AbstractBuild;

import java.io.IOException;

/**
 * @author Dirk Weigenand
 * 
 */
public final class NWDIBuild extends AbstractBuild<NWDIProject, NWDIBuild> {

    protected NWDIBuild(final NWDIProject project) throws IOException {
        super(project);
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }
}
