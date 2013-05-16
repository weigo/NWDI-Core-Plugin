/**
 * 
 */
package org.arachna.xml;

import java.io.IOException;
import java.io.Reader;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.binder.DigesterLoader;
import org.xml.sax.SAXException;

/**
 * Helper class for parsing XML files using Digester3.
 * 
 * @param <T>
 *            type that should be returned by the parsing process.
 * @author Dirk Weigenand
 */
public final class DigesterHelper<T> {
    /**
     * producer for <code>RulesModule</code> instances that should be used to
     * control the parsing process.
     */
    private final RulesModuleProducer rulesProducer;

    /**
     * Create an instance of <code>DigesterHelper</code> using the given
     * {@link RulesModuleProducer}.
     * 
     * @param rulesProducer
     *            producer for <code>RulesModule</code> instances to guide the
     *            parsing process.
     */
    public DigesterHelper(final RulesModuleProducer rulesProducer) {
        this.rulesProducer = rulesProducer;
    }

    /**
     * Update the given development component from the given configuration file.
     * 
     * @param reader
     *            reader object for reading the configuration file.
     * @return <T> an object of type T parsed from the given configuration file.
     */
    public T execute(final Reader reader) {
        try {
            return createDigester().<T> parse(reader);
        }
        catch (final SAXException e) {
            throw new IllegalStateException(e);
        }
        catch (final IOException e) {
            throw new IllegalStateException(e);
        }
        finally {
            try {
                reader.close();
            }
            catch (final IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * Parse the given configuration file and return the parsed configuration
     * object of type T.
     * 
     * @param reader
     *            reader object for reading the configuration file.
     * @param updatee
     *            the object that should be updated from the given configuration
     *            file.
     * @return <T> an object of type T parsed from the given configuration file.
     */
    public T update(final Reader reader, final T updatee) {
        try {
            final Digester digester = createDigester();
            digester.push(updatee);

            return digester.<T> parse(reader);
        }
        catch (final SAXException e) {
            throw new IllegalStateException(e);
        }
        catch (final IOException e) {
            throw new IllegalStateException(e);
        }
        finally {
            try {
                reader.close();
            }
            catch (final IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * Create a <code>Digester</code> using the <code>RulesModule</code> from
     * the <code>rulesProducer</code>.
     * 
     * @return new Digester instance.
     */
    protected Digester createDigester() {
        final DigesterLoader digesterLoader = DigesterLoader.newLoader(rulesProducer.getRulesModule());
        return digesterLoader.newDigester();
    }
}
