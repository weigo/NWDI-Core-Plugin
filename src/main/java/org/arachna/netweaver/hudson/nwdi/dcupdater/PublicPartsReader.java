/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.arachna.netweaver.dc.types.PublicPart;
import org.arachna.xml.XmlReaderHelper;

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
     * Read public parts.
     * 
     * @return list of public parts read
     */
    List<PublicPart> read() {
        final List<PublicPart> publicParts = new ArrayList<PublicPart>();

        if (this.publicPartsLocation.exists()) {
            PublicPartReader reader;
            PublicPart part;
            FileReader ppFileReader = null;

            for (final File definition : this.publicPartsLocation.listFiles(new PublicPartFileFilter())) {
                try {
                    reader = new PublicPartReader();
                    ppFileReader = new FileReader(definition);
                    new XmlReaderHelper(reader).parse(ppFileReader);
                    part = reader.getPublicPart();

                    if (part != null) {
                        publicParts.add(part);
                    }
                }
                catch (final Exception e) {
                    // FIXME: fix exception handling.
                    throw new RuntimeException(e);
                }
                finally {
                    if (ppFileReader != null) {
                        try {
                            ppFileReader.close();
                        }
                        catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
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
        /**
         * Accept files ending on '.pp'.
         * 
         * {@inheritDoc}
         */
        public boolean accept(final File pathname) {
            return pathname.isFile() && pathname.getName().endsWith(".pp");
        }
    }
}
