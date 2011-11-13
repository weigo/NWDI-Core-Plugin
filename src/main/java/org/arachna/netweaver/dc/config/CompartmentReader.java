/**
 *
 */
package org.arachna.netweaver.dc.config;

import java.util.ArrayList;
import java.util.List;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.xml.AbstractDefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * SAX handler for reading {@link Compartment} objects.
 * 
 * @author Dirk Weigenand
 */
public class CompartmentReader extends AbstractDefaultHandler {
    /**
     * attribute for compartment state (archive-state="yes").
     */
    private static final String YES = "yes";

    /**
     * tag for a used compartment.
     */
    private static final String USED_COMPARTMENT = "used-compartment";

    /**
     * attribute vendor.
     */
    private static final String VENDOR = "vendor";

    /**
     * attribute sc-name.
     */
    private static final String SC_NAME = "sc-name";

    /**
     * attribute archive-state.
     */
    private static final String ARCHIVE_STATE = "archive-state";

    /**
     * attribute caption.
     */
    private static final String CAPTION = "caption";

    /**
     * attribute name.
     */
    private static final String NAME = "name";

    /**
     * tag for encapsulating a collection of development components.
     */
    private static final String DEVELOPMENT_COMPONENTS = "development-components";

    /**
     * tag encapsulating a development configuration.
     */
    private static final String DEVELOPMENT_CONFIGURATION = "development-configuration";

    /**
     * tag encapsulating a compartment.
     */
    private static final String COMPARTMENT = "compartment";

    /**
     * collection of compartments read from configuration file.
     */
    private final List<Compartment> compartments = new ArrayList<Compartment>();

    /**
     * compartment currently read.
     */
    private Compartment currentCompartment;

    /**
     * factory for creating and registering development components.
     */
    private final DevelopmentComponentFactory developmentComponentFactory;

    /**
     * SAX handler for parsing the development components of a compartment.
     */
    private DevelopmentComponentReader developmentComponentReader;

    /**
     * Create a new reader for a compartment using the given {@link XMLReader},
     * parent SAX handler and development component factory.
     * 
     * @param xmlReader
     *            the XMLReader to use for XML parsing
     * @param parent
     *            the parent SAX handler control should be returned to after
     *            reading compartments
     * @param developmentComponentFactory
     *            factory for development components
     */
    public CompartmentReader(final XMLReader xmlReader, final AbstractDefaultHandler parent,
        final DevelopmentComponentFactory developmentComponentFactory) {
        super(parent);
        setXmlReader(xmlReader);
        this.developmentComponentFactory = developmentComponentFactory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (CompartmentReader.COMPARTMENT.equals(localName)) {
            compartments.add(currentCompartment);
            currentCompartment = null;
        }
        else if (CompartmentReader.DEVELOPMENT_CONFIGURATION.equals(localName)) {
            getXmlReader().setContentHandler(getParent());
            getParent().endElement(uri, localName, qName);
        }
        else if (CompartmentReader.DEVELOPMENT_COMPONENTS.equals(localName)) {
            currentCompartment.add(developmentComponentReader.getDevelopmentComponents());
            developmentComponentReader = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
        throws SAXException {
        if (CompartmentReader.COMPARTMENT.equals(localName)) {
            currentCompartment =
                new Compartment(attributes.getValue(CompartmentReader.NAME),
                    getCompartmentState(attributes.getValue(CompartmentReader.ARCHIVE_STATE)),
                    attributes.getValue(CompartmentReader.VENDOR), attributes.getValue(CompartmentReader.CAPTION),
                    attributes.getValue(CompartmentReader.SC_NAME));
        }
        else if (CompartmentReader.USED_COMPARTMENT.equals(localName)) {
            currentCompartment.add(new Compartment(attributes.getValue(CompartmentReader.NAME),
                getCompartmentState(attributes.getValue(CompartmentReader.ARCHIVE_STATE)), attributes
                    .getValue(CompartmentReader.VENDOR), attributes.getValue(CompartmentReader.CAPTION), attributes
                    .getValue(CompartmentReader.SC_NAME)));
        }
        else if (CompartmentReader.DEVELOPMENT_COMPONENTS.equals(localName)) {
            developmentComponentReader =
                new DevelopmentComponentReader(getXmlReader(), this, developmentComponentFactory);
            getXmlReader().setContentHandler(developmentComponentReader);
        }
    }

    /**
     * Returns the compartments read from the XML configuration.
     * 
     * @return compartments read from the XML configuration.
     */
    public final List<Compartment> getCompartments() {
        return compartments;
    }

    /**
     * Return the state the compartment is in.
     * 
     * @param state
     *            value of attribute {@link #ARCHIVE_STATE}.
     * @return the {@link CompartmentState} determined from the attribute value.
     */
    private CompartmentState getCompartmentState(final String state) {
        return YES.equals(state) ? CompartmentState.Archive : CompartmentState.Source;
    }
}
