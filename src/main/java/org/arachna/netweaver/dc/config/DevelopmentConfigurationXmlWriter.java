/**
 * 
 */
package org.arachna.netweaver.dc.config;

import java.io.Writer;
import java.util.Collection;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.arachna.netweaver.dc.types.BuildVariant;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dc.types.PublicPart;
import org.arachna.netweaver.dc.types.PublicPartReference;

/**
 * Writer for persisting a {@link DevelopmentConfiguration} to XML.
 * 
 * @author Dirk Weigenand
 */
public final class DevelopmentConfigurationXmlWriter {
    /**
     * vendor attribute.
     */
    private static final String VENDOR = "vendor";

    /**
     * type attribute.
     */
    private static final String TYPE = "type";

    /**
     * name attribute.
     */
    private static final String NAME = "name";

    /**
     * description attribute.
     */
    private static final String DESCRIPTION = "description";

    /**
     * caption attribute.
     */
    private static final String CAPTION = "caption";

    /**
     * development configuration that shall be persisted as XML.
     */
    private final DevelopmentConfiguration configuration;

    /**
     * {@link XMLStreamWriter} to generate XML with.
     */
    private XMLStreamWriter output;

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
     *            {@link Writer} to write XML into.
     * @throws XMLStreamException
     *             when writing the attributes/elements fails.
     */
    public void write(final Writer writer) throws XMLStreamException {
        final XMLOutputFactory factory = XMLOutputFactory.newInstance();
        output = factory.createXMLStreamWriter(writer);
        emitDocument();
    }

    /**
     * Save development configuration to XML.
     * 
     * @throws XMLStreamException
     *             when writing the attributes/elements fails.
     */
    private void emitDocument() throws XMLStreamException {
        output.writeStartDocument();
        emitDevelopmentConfiguration();
        output.writeEndDocument();
    }

    /**
     * Write the development configuration, build variant and contained
     * compartments tags and respective attributes.
     * 
     * @throws XMLStreamException
     *             when writing the attributes/elements fails.
     */
    private void emitDevelopmentConfiguration() throws XMLStreamException {
        output.writeStartElement("development-configuration");
        output.writeAttribute(CAPTION, configuration.getCaption());
        output.writeAttribute(DESCRIPTION, configuration.getDescription());
        output.writeAttribute(NAME, configuration.getName());
        emitBuildVariant();
        emitCompartments();
        output.writeEndElement();
    }

    /**
     * Emit compartments as XML.
     * 
     * @throws XMLStreamException
     *             when writing the attributes/elements fails.
     */
    private void emitCompartments() throws XMLStreamException {
        for (final Compartment compartment : configuration.getCompartments()) {
            output.writeStartElement("compartment");
            emitCompartmentAttributes(compartment);
            emitUsedCompartments(compartment.getUsedCompartments());
            emitDevelopmentComponents(compartment.getDevelopmentComponents());
            output.writeEndElement();
        }
    }

    /**
     * Emit development components of a compartment as XML.
     * 
     * @param developmentComponents
     *            development components to serialize as XML.
     * @throws XMLStreamException
     *             when writing the attributes/elements fails.
     */
    private void emitDevelopmentComponents(final Collection<DevelopmentComponent> developmentComponents)
        throws XMLStreamException {
        output.writeStartElement("development-components");

        for (final DevelopmentComponent component : developmentComponents) {
            emitDevelopmentComponent(component);
        }

        output.writeEndElement();
    }

    /**
     * Emit the given development component as XML.
     * 
     * @param component
     *            development component to serialize as XML.
     * @throws XMLStreamException
     *             when writing the attributes/elements fails.
     */
    private void emitDevelopmentComponent(final DevelopmentComponent component) throws XMLStreamException {
        output.writeStartElement("development-component");
        emitDevelopmentComponentAttributes(component);
        output.writeStartElement(DESCRIPTION);
        output.writeCharacters(component.getDescription());
        output.writeEndElement();
        emitUsedDcs(component.getUsedDevelopmentComponents());
        emitPublicParts(component.getPublicParts());
        output.writeEndElement();
    }

    /**
     * Emit public parts of a development component to XML.
     * 
     * @param publicParts
     *            public parts to serialize as XML.
     * @throws XMLStreamException
     *             when writing the attributes/elements fails.
     */
    private void emitPublicParts(final Collection<PublicPart> publicParts) throws XMLStreamException {
        output.writeStartElement("public-parts");

        for (final PublicPart pp : publicParts) {
            emitPublicPart(pp);
        }

        output.writeEndElement();
    }

    /**
     * Emit a public part as XML.
     * 
     * @param pp
     *            public part to serialize as XML.
     * @throws XMLStreamException
     *             when writing the attributes/elements fails.
     */
    private void emitPublicPart(final PublicPart pp) throws XMLStreamException {
        output.writeStartElement("public-part");
        output.writeAttribute(CAPTION, pp.getCaption());
        output.writeAttribute(NAME, pp.getPublicPart());
        output.writeAttribute(TYPE, pp.getType().toString());
        output.writeStartElement(DESCRIPTION);
        output.writeCharacters(pp.getDescription());
        output.writeEndElement();
        output.writeEndElement();
    }

    /**
     * Emit references to other development components to XML.
     * 
     * @param usedDevelopmentComponents
     *            references to public parts of used DCs to serialize as XML.
     * @throws XMLStreamException
     *             when writing the attributes/elements fails.
     */
    private void emitUsedDcs(final Collection<PublicPartReference> usedDevelopmentComponents) throws XMLStreamException {
        output.writeStartElement("dependencies");

        for (final PublicPartReference ppRef : usedDevelopmentComponents) {
            emitUsedDc(ppRef);
        }

        output.writeEndElement();
    }

    /**
     * Emit the given public part reference as XML.
     * 
     * @param ppRef
     *            public part reference to serialize as XML.
     * @throws XMLStreamException
     *             when writing the attributes/elements fails.
     */
    private void emitUsedDc(final PublicPartReference ppRef) throws XMLStreamException {
        output.writeStartElement("dependency");
        output.writeAttribute(NAME, ppRef.getComponentName());
        output.writeAttribute("pp-ref", ppRef.getName());
        output.writeAttribute(VENDOR, ppRef.getVendor());

        if (ppRef.isAtBuildTime()) {
            output.writeEmptyElement("at-build-time");
        }

        if (ppRef.isAtRunTime()) {
            output.writeEmptyElement("at-run-time");
        }

        output.writeEndElement();
    }

    /**
     * Emit the name, type and vendor attributes of the given development
     * component as XML.
     * 
     * @param component
     *            development component whose name, type and vendor attributes
     *            shall be serialized to XML.
     * @throws XMLStreamException
     *             when writing the attributes/elements fails.
     */
    private void emitDevelopmentComponentAttributes(final DevelopmentComponent component) throws XMLStreamException {
        output.writeAttribute(NAME, component.getName());
        output.writeAttribute(TYPE, component.getType().toString());
        output.writeAttribute(VENDOR, component.getVendor());
    }

    /**
     * Emit the given collection of compartments as used-compartments element.
     * 
     * @param compartments
     *            compartments to serialize as used-compartments.
     * @throws XMLStreamException
     *             when writing the attributes/elements fails.
     */
    private void emitUsedCompartments(final Collection<Compartment> compartments) throws XMLStreamException {
        output.writeStartElement("used-compartments");

        for (final Compartment compartment : compartments) {
            output.writeEmptyElement("used-compartment");
            emitCompartmentAttributes(compartment);
        }

        output.writeEndElement();
    }

    /**
     * Emit the given compartment as XML.
     * 
     * @param compartment
     *            compartment to save as XML.
     * @throws XMLStreamException
     *             when writing the attributes/elements fails.
     */
    private void emitCompartmentAttributes(final Compartment compartment) throws XMLStreamException {
        output.writeAttribute("archive-state", compartment.isArchiveState() ? "yes" : "no");
        output.writeAttribute(CAPTION, compartment.getCaption());
        output.writeAttribute(NAME, compartment.getName());
        output.writeAttribute("sc-name", compartment.getSoftwareComponent());
        output.writeAttribute(VENDOR, compartment.getVendor());
    }

    /**
     * Emit the build variant as XML.
     * 
     * @throws XMLStreamException
     *             when writing the attributes/elements fails.
     */
    private void emitBuildVariant() throws XMLStreamException {
        output.writeStartElement("build-variant");
        final BuildVariant buildVariant = configuration.getBuildVariant();
        output.writeAttribute(NAME, buildVariant.getName());

        for (final String name : buildVariant.getBuildOptionNames()) {
            output.writeStartElement("option");
            output.writeAttribute(NAME, name);
            output.writeAttribute("value", buildVariant.getBuildOption(name));
            output.writeEndElement();
        }

        output.writeEndElement();
    }
}
