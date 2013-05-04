/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import java.io.IOException;
import java.io.Reader;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.digester3.binder.DigesterLoader;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.xml.sax.SAXException;

/**
 * Base class for reading configuration files of development components apart of
 * <code>.dcdef</code>.
 * 
 * @author Dirk Weigenand
 */
abstract class AbstractComponentConfigurationReader implements ComponentConfigurationReader {
    /**
     * Update the given development component from the given configuration file.
     * 
     * @param component
     *            development component to update from given reader.
     * @param reader
     *            reader object for reading the configuration file.
     */
    public void execute(final DevelopmentComponent component, final Reader reader) {
        try {
            final DigesterLoader digesterLoader = DigesterLoader.newLoader(getRulesModule());
            final Digester digester = digesterLoader.newDigester();
            digester.push(component);
            digester.parse(reader);
        }
        catch (final SAXException e) {
            throw new RuntimeException(e);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                reader.close();
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Create a digester rules module.
     * 
     * @return an implementation of {@link AbstractRulesModule}.
     */
    abstract AbstractRulesModule getRulesModule();
}
