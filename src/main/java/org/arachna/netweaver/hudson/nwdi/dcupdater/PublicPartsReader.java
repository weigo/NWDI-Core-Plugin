/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arachna.netweaver.dc.types.PublicPart;
import org.arachna.xml.DigesterHelper;

/**
 * Reader for Public Parts.
 * 
 * @author Dirk Weigenand
 */
final class PublicPartsReader {
    /**
     * Logger.
     */
    private final Logger logger = Logger.getLogger(PublicPartsReader.class.getName());

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
        publicPartsLocation = new File(String.format("%s/def", componentLocation));
    }

    /**
     * Read public parts.
     * 
     * @return list of public parts read
     */
    List<PublicPart> read() {
        final List<PublicPart> publicParts = new ArrayList<PublicPart>();

        if (publicPartsLocation.exists()) {
            final DigesterHelper<PublicPart> digesterHelper = new DigesterHelper<PublicPart>(new PublicPartRulesModuleProducer());

            for (final File definition : publicPartsLocation.listFiles(new PublicPartFileFilter())) {
                try {
                    publicParts.add(digesterHelper.execute(new FileReader(definition)));
                }
                catch (final FileNotFoundException e) {
                    logger.log(Level.WARNING, e.getLocalizedMessage(), e);
                }
                catch (final IllegalStateException ise) {
                    logger.log(Level.WARNING, ise.getLocalizedMessage(), ise);
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
