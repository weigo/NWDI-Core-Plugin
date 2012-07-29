/**
 * 
 */
package org.arachna.xml;

import java.io.IOException;
import java.io.StringReader;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * {@link EntityResolver} to avoid hitting the net for URLs that can't be
 * resolved anyway.
 * 
 * @author Dirk Weigenand
 */
public final class NullEntityResolver implements EntityResolver {
    /**
     * {@inheritDoc}
     */
    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
        return new InputSource(new StringReader(""));
    }
}
