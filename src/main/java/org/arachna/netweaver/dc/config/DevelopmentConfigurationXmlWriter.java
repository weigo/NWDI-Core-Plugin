/**
 * 
 */
package org.arachna.netweaver.dc.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;

/**
 * Writer for persisting a {@link DevelopmentConfiguration} to XML.
 * 
 * @author Dirk Weigenand
 */
public final class DevelopmentConfigurationXmlWriter {
    /**
     * name of velocity template used to export a development configuration to
     * XML.
     */
    private static final String VELOCITY_TEMPLATE = "DevelopmentConfiguration.vtl";

    /**
     * development configuration that shall be persisted as XML.
     */
    private final DevelopmentConfiguration configuration;

    /**
     * Create an instance of a <code>DevelopmentConfigurationXmlWriter</code>
     * with the given development configuration.
     * 
     * @param configuration
     *            the development configuration to persist to XML.
     */
    public DevelopmentConfigurationXmlWriter(final DevelopmentConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Write the development configuration as XML into the given writer.
     * 
     * @param writer
     *            {@link Writer} to write XML into
     * @throws ParseErrorException
     * @throws MethodInvocationException
     * @throws ResourceNotFoundException
     * @throws IOException
     *             when the template for exporting to XML could not be found or
     *             an error occurred writing the XML.
     */
    public void write(final Writer writer) throws ParseErrorException, MethodInvocationException,
        ResourceNotFoundException, IOException {
        final VelocityEngine engine = new VelocityEngine();
        final Context context = new VelocityContext();
        context.put("configuration", configuration);
        engine.evaluate(context, writer, "", getTemplateReader());
    }

    /**
     * Return a reader for the Velocity template for rendering the development
     * configuration to XML.
     * 
     * @return reader for the Velocity template for rendering the development
     *         configuration to XML
     * @throws IOException
     *             when the velocity template could not be found.
     */
    private Reader getTemplateReader() throws IOException {
        final InputStream resource = this.getClass().getResourceAsStream(VELOCITY_TEMPLATE);

        if (resource == null) {
            throw new IOException(String.format(
                "Velocity template for exporting a development configuration '%s' not found!", VELOCITY_TEMPLATE));
        }

        return new InputStreamReader(resource);
    }
}
