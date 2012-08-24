/**
 * 
 */
package org.arachna.netweaver.tools.cbs;

import java.util.Arrays;
import java.util.List;

import org.arachna.netweaver.tools.DIToolDescriptor;

/**
 * CBS tool command executor for downloading the <code>.confdef</code> of a given build space.
 * 
 * @author Dirk Weigenand
 */
public final class DownloadConfig extends AbstractCBSToolCommandExecutor {
    /**
     * name of build space to download development configuration for.
     */
    private final String buildSpace;

    /**
     * name of file to write development configuration into.
     */
    private final String targetFileName;

    /**
     * Create an instance of a CBS tool download development configuration command executor.
     * 
     * @param cbsUrl
     *            URL to CBS
     * @param diToolDescriptor
     *            descriptor with credentials.
     * @param buildSpace
     *            build space to download development configuration for.
     * @param targetFileName
     *            file name to write development configuration into.
     */
    protected DownloadConfig(String cbsUrl, DIToolDescriptor diToolDescriptor, String buildSpace, String targetFileName) {
        super(cbsUrl, diToolDescriptor);
        this.buildSpace = buildSpace;
        this.targetFileName = targetFileName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> executeInternal() {
        return Arrays.asList(String.format("downloadconfig -b %s -f %s", buildSpace, targetFileName));
    }
}
