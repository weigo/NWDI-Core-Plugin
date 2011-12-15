/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arachna.netweaver.dc.types.PublicPartReference;
import org.arachna.xml.AbstractDefaultHandler;
import org.arachna.xml.XmlReaderHelper;
import org.xml.sax.SAXException;

/**
 * Base class for reading configuration files of development components apart of
 * <code>.dcdef</code>.
 * 
 * @author Dirk Weigenand
 */
abstract class AbstractComponentConfigurationReader extends AbstractDefaultHandler implements
    ComponentConfigurationReader {
    /**
     * Factory for public part references from references read from WebDynpro
     * project properties or portal applications config files.
     */
    private PublicPartReferenceFactory ppRefFactory = new PublicPartReferenceFactory();

    /**
     * message for IO errors.
     */
    private static final String IO_EXCEPTION_MESSAGE = "There was a problem reading %s.";

    /**
     * message regarding SAX parse(r) errors.
     */
    private static final String SAX_EXCEPTION_MESSAGE = "There was a problem parsing %s.";

    /**
     * public part references.
     */
    private final Set<PublicPartReference> references = new HashSet<PublicPartReference>();

    /**
     * base directory of development component.
     */
    private final String componentBase;

    /**
     * Create an instance of an
     * <code>AbstractComponentConfigurationReader</code>.
     * 
     * @param componentBase
     *            base directory of development component.
     */
    protected AbstractComponentConfigurationReader(final String componentBase) {
        super();
        this.componentBase = componentBase;
    }

    /**
     * {@inheritDoc}
     */
    public final Set<PublicPartReference> read() {
        this.references.clear();
        FileReader input = null;

        try {
            final File source = new File(this.componentBase + File.separatorChar + this.getConfigurationLocation());

            if (source.exists()) {
                input = new FileReader(source);
                new XmlReaderHelper(this).parse(input);
            }
        }
        catch (final IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
                String.format(IO_EXCEPTION_MESSAGE, this.getConfigurationLocation()), e);
        }
        catch (final SAXException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
                String.format(SAX_EXCEPTION_MESSAGE, this.getConfigurationLocation()), e);
        }
        finally {
            if (input != null) {
                try {
                    input.close();
                }
                catch (IOException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
                        String.format(IO_EXCEPTION_MESSAGE, this.getConfigurationLocation()), e);
                }
            }
        }

        return this.references;
    }

    /**
     * Create a {@link PublicPartReference} and add it to the internal
     * collection of public part references read from the configuration file.
     * 
     * @param reference
     *            public part references read from the configuration file.
     */
    protected final void addPublicPartReference(final String reference) {
        final PublicPartReference ppReference = ppRefFactory.create(reference);

        if (ppReference != null) {
            this.references.add(ppReference);
        }
    }

    /**
     * Return the location of the configuration file to parse.
     * 
     * @return location of the configuration file to parse.
     */
    protected abstract String getConfigurationLocation();
}
