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
     *            {@link Writer} to write XML into
     * @throws XMLStreamException
     */
    public void write(final Writer writer) throws XMLStreamException {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        output = factory.createXMLStreamWriter(writer);
        emitDocument();
    }

    /**
     * @param output
     * @throws XMLStreamException
     */
    private void emitDocument() throws XMLStreamException {
        output.writeStartDocument();
        emitDevelopmentConfiguration();
        output.writeEndDocument();
    }

    /**
     * @param output
     * @throws XMLStreamException
     */
    private void emitDevelopmentConfiguration() throws XMLStreamException {
        output.writeStartElement("development-configuration");
        output.writeAttribute("caption", this.configuration.getCaption());
        output.writeAttribute("description", this.configuration.getDescription());
        // output.writeAttribute("location", this.configuration.getLocation());
        output.writeAttribute("name", this.configuration.getName());
        emitBuildVariant();
        emitCompartments();
        output.writeEndElement();
    }

    /**
     * @throws XMLStreamException
     * 
     */
    private void emitCompartments() throws XMLStreamException {
        for (Compartment compartment : this.configuration.getCompartments()) {
            output.writeStartElement("compartment");
            emitCompartmentAttributes(compartment);
            emitUsedCompartments(compartment.getUsedCompartments());
            emitDevelopmentComponents(compartment.getDevelopmentComponents());
            output.writeEndElement();
        }
    }

    /**
     * @param developmentComponents
     * @throws XMLStreamException
     */
    private void emitDevelopmentComponents(Collection<DevelopmentComponent> developmentComponents)
        throws XMLStreamException {
        output.writeStartElement("development-components");

        for (DevelopmentComponent component : developmentComponents) {
            emitDevelopmentComponent(component);
        }

        output.writeEndElement();
    }

    /**
     * @param component
     * @throws XMLStreamException
     */
    private void emitDevelopmentComponent(DevelopmentComponent component) throws XMLStreamException {
        output.writeStartElement("development-component");
        emitDevelopmentComponentAttributes(component);
        output.writeStartElement("description");
        output.writeCharacters(component.getDescription());
        output.writeEndElement();
        emitUsedDcs(component.getUsedDevelopmentComponents());
        emitPublicParts(component.getPublicParts());
        output.writeEndElement();
    }

    /**
     * @param component
     * @throws XMLStreamException
     */
    private void emitPublicParts(Collection<PublicPart> publicParts) throws XMLStreamException {
        output.writeStartElement("public-parts");

        for (PublicPart pp : publicParts) {
            emitPublicPart(pp);
        }

        output.writeEndElement();
    }

    /**
     * @param pp
     * @throws XMLStreamException
     */
    private void emitPublicPart(PublicPart pp) throws XMLStreamException {
        output.writeStartElement("public-part");
        output.writeAttribute("caption", pp.getCaption());
        output.writeAttribute("name", pp.getPublicPart());
        output.writeStartElement("description");
        output.writeCharacters(pp.getDescription());
        output.writeEndElement();
        output.writeEndElement();
    }

    /**
     * @param usedDevelopmentComponents
     * @throws XMLStreamException
     */
    private void emitUsedDcs(Collection<PublicPartReference> usedDevelopmentComponents) throws XMLStreamException {
        output.writeStartElement("dependencies");

        for (PublicPartReference ppRef : usedDevelopmentComponents) {
            emitUsedDc(ppRef);
        }

        output.writeEndElement();
    }

    /**
     * @param ppRef
     * @throws XMLStreamException
     */
    private void emitUsedDc(PublicPartReference ppRef) throws XMLStreamException {
        output.writeStartElement("dependency");
        output.writeAttribute("name", ppRef.getComponentName());
        output.writeAttribute("pp-ref", ppRef.getName());
        output.writeAttribute("vendor", ppRef.getVendor());

        if (ppRef.isAtBuildTime()) {
            output.writeEmptyElement("at-build-time");
        }

        if (ppRef.isAtRunTime()) {
            output.writeEmptyElement("at-run-time");
        }

        output.writeEndElement();
    }

    /**
     * @param component
     * @throws XMLStreamException
     */
    private void emitDevelopmentComponentAttributes(DevelopmentComponent component) throws XMLStreamException {
        output.writeAttribute("name", component.getName());
        output.writeAttribute("type", component.getType().toString());
        output.writeAttribute("vendor", component.getVendor());
    }

    /**
     * @param compartment
     * @throws XMLStreamException
     */
    private void emitUsedCompartments(Collection<Compartment> compartments) throws XMLStreamException {
        output.writeStartElement("used-compartments");

        for (Compartment compartment : compartments) {
            output.writeEmptyElement("used-compartment");
            emitCompartmentAttributes(compartment);
        }

        output.writeEndElement();
    }

    /**
     * @param compartment
     * @throws XMLStreamException
     */
    private void emitCompartmentAttributes(Compartment compartment) throws XMLStreamException {
        output.writeAttribute("archive-state", compartment.isArchiveState() ? "yes" : "no");
        output.writeAttribute("caption", compartment.getCaption());
        output.writeAttribute("name", compartment.getName());
        output.writeAttribute("sc-name", compartment.getSoftwareComponent());
        output.writeAttribute("vendor", compartment.getVendor());
    }

    /**
     * @throws XMLStreamException
     * 
     */
    private void emitBuildVariant() throws XMLStreamException {
        output.writeStartElement("build-variant");
        BuildVariant buildVariant = this.configuration.getBuildVariant();
        output.writeAttribute("name", buildVariant.getName());

        for (String name : buildVariant.getBuildOptionNames()) {
            output.writeStartElement("option");
            output.writeAttribute("name", name);
            output.writeAttribute("value", buildVariant.getBuildOption(name));
            output.writeEndElement();
        }

        output.writeEndElement();
    }
}
