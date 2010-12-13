/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.arachna.netweaver.dc.types.PublicPart;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Reader für Public Parts.
 * 
 * @author Dirk Weigenand
 */
final class PublicPartsReader {
    /**
     * location of public part descriptors.
     */
    private final File publicPartsLocation;

    /**
     * create a reader for public parts.
     * 
     * @param componentLocation
     *            base folder for public part descriptors.
     */
    public PublicPartsReader(final String componentLocation) {
        this.publicPartsLocation = new File(String.format("%s%cdef", componentLocation, File.separatorChar));
    }

    /**
     * read in public parts.
     * 
     * @return list of public parts read
     */
    List<PublicPart> read() {
        final List<PublicPart> publicParts = new ArrayList<PublicPart>();
        XMLReader xmlReader;
        PublicPartReader reader;
        PublicPart part;

        if (this.publicPartsLocation.exists()) {
            for (final File definition : this.publicPartsLocation.listFiles(new PublicPartFileFilter())) {
                try {
                    xmlReader = XMLReaderFactory.createXMLReader();
                    reader = new PublicPartReader(xmlReader);
                    xmlReader.setContentHandler(reader);
                    xmlReader.parse(new InputSource(new FileReader(definition)));
                    part = reader.getPublicPart();

                    if (part != null) {
                        publicParts.add(part);
                    }
                }
                catch (final Exception e) {
                    // FIXME: fix exception handling.
                    e.printStackTrace();
                }
            }
        }

        return publicParts;
    }

    /**
     * Filter for filtering out public part configuration files.
     * 
     * @author Dirk Weigenand
     */
    private static final class PublicPartFileFilter implements FileFilter {
        public boolean accept(final File pathname) {
            return pathname.isFile() && pathname.getName().endsWith(".pp");
        }
    }
}
