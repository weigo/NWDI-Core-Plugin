/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import hudson.FilePath;
import hudson.model.AbstractBuild;

import java.io.IOException;
import java.io.InputStreamReader;

import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.hudson.nwdi.confdef.ConfDefReader;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Dirk Weigenand
 * 
 */
public final class NWDIBuild extends AbstractBuild<NWDIProject, NWDIBuild> {

    private DevelopmentConfiguration developmentConfiguration;

    protected NWDIBuild(final NWDIProject project) throws IOException {
        super(project);
    }

    @Override
    public void run() {
        // TODO query project for .confdef and create/update it
        // createOrUpdateConfiguration();
        // TODO fire off build...
    }

    /**
     */
    public DevelopmentConfiguration getDevelopmentConfiguration() {
        if (this.developmentConfiguration == null) {
            try {
                final ConfDefReader confdefReader = new ConfDefReader(XMLReaderFactory.createXMLReader());
                this.developmentConfiguration =
                    confdefReader.read(new InputStreamReader(this.getWorkspace().child(".confdef").read()));
            }
            catch (final SAXException e) {
                throw new RuntimeException(e);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return this.developmentConfiguration;
    }

    /**
     * @param workspace
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private FilePath createOrUpdateConfiguration() throws IOException, InterruptedException {
        final DtrConfigCreator configCreator =
            new DtrConfigCreator(this.getWorkspace(), this.getDevelopmentConfiguration(), this.getProject()
                .getCauseOfBlockage().toString());
        final FilePath dtrDirectory = configCreator.execute();
        return dtrDirectory;
    }
}