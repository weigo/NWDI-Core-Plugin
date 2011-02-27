/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.confdef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.arachna.netweaver.dc.types.BuildVariant;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.xml.AbstractDefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Reader for the <compartment> part of <code>.confdef</code> files (that
 * describe a development configuration in the NWDI).
 * 
 * @author Dirk Weigenand
 * 
 */
public final class CompartmentReader extends AbstractDefaultHandler {
    /**
     * Location of inactive workspace.
     */
    private static final String INACTIVE_LOCATION = "inactive-location";

    /**
     * element name for repository data.
     */
    private static final String REPOSITORY = "repository";

    /**
     * url attribute for dtr repository element.
     */
    private static final String DTR_URL = "url";

    /**
     * tag name for 'build-option'.
     */
    private static final String BUILD_OPTION = "build-option";

    /**
     * tag name for 'build-variant'.
     */
    private static final String BUILD_VARIANT = "build-variant";

    /**
     * value of attribute 'archive-state'.
     */
    private static final String ARCHIVE_STATE_YES = "yes";

    /**
     * tag name for 'archive-state'.
     */
    private static final String ARCHIVE_STATE = "archive-state";

    /**
     * tag name for 'sc-vendor'.
     */
    private static final String SC_VENDOR = "sc-vendor";

    /**
     * tag name for 'sc-name'.
     */
    private static final String SC_NAME = "sc-name";

    /**
     * tag name for 'caption'.
     */
    private static final String CAPTION = "caption";

    /**
     * tag name for 'name'.
     */
    private static final String NAME = "name";

    /**
     * tag name for 'option-value'.
     */
    private static final String OPTION_VALUE = "option-value";

    /**
     * tag name for 'used-compartment'.
     */
    private static final String USED_COMPARTMENT = "used-compartment";

    /**
     * tag name for 'sc-compartment'.
     */
    private static final String SC_COMPARTMENT = "sc-compartment";

    /**
     * tag name for 'sc-compartments'.
     */
    private static final String SC_COMPARTMENTS = "sc-compartments";

    /**
     * {@link Compartment}s read.
     */
    private final List<Compartment> compartments = new ArrayList<Compartment>();

    /**
     * compartment currently processed.
     */
    private Compartment compartment;

    /**
     * {@link BuildVariant} for the currently processed compartment.
     */
    private BuildVariant buildVariant;

    /**
     * name of the current build option.
     */
    private String buildOptionName;

    /**
     * name of the current build variant.
     */
    private String buildVariantName;

    /**
     * Create an instance of a <code>CompartmentReader</code> using the given
     * {@link XMLReader} and parent <code>AbstractDefaultHandler</code> object.
     * 
     * @param parent
     *            the parent <code>AbstractDefaultHandler</code> to give control
     *            back when finished handling SAX events.
     */
    public CompartmentReader(final AbstractDefaultHandler parent) {
        super(parent);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(final String uri, final String localName, final String name) throws SAXException {
        if (SC_COMPARTMENTS.equals(localName)) {
            this.getXmlReader().setContentHandler(this.getParent());
            this.getParent().endElement(uri, localName, name);
        }
        else if (SC_COMPARTMENT.equals(localName)) {
            this.compartments.add(compartment);
            this.compartment = null;
        }
        else if (USED_COMPARTMENT.equals(localName)) {
            final String compartment = this.getText();
            final int vendorEndIndex = compartment.indexOf('_');
            final String vendor = compartment.substring(0, vendorEndIndex);
            final String compartmentName = compartment.substring(vendorEndIndex + 1);
            final String softwareComponentName = compartmentName.substring(0, compartmentName.lastIndexOf('_'));
            // FIXME: should be a reference or the real McCoy...
            this.compartment.add(new Compartment(compartment, CompartmentState.Archive, vendor, "",
                softwareComponentName));
        }
        else if (OPTION_VALUE.equals(localName)) {
            if (this.buildVariantName != null && this.buildVariant == null) {
                this.buildVariant = new BuildVariant(this.buildVariantName);
            }

            this.buildVariant.addBuildOption(this.buildOptionName, this.getText());
            this.buildOptionName = null;
        }
        else if (INACTIVE_LOCATION.equals(localName)) {
            this.compartment.setInactiveLocation(this.getText());
        }

        this.getText();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(final String uri, final String localName, final String name, final Attributes atts)
        throws SAXException {
        if (SC_COMPARTMENT.equals(localName)) {
            final String state = atts.getValue(ARCHIVE_STATE);
            CompartmentState compartmentState = null;

            if (ARCHIVE_STATE_YES.equals(state)) {
                compartmentState = CompartmentState.Archive;
            }
            else {
                compartmentState = CompartmentState.Source;
            }

            this.compartment =
                new Compartment(atts.getValue(NAME), compartmentState, atts.getValue(SC_VENDOR),
                    atts.getValue(CAPTION), atts.getValue(SC_NAME));
        }
        else if (BUILD_VARIANT.equals(localName)) {
            if (ARCHIVE_STATE_YES.equals(atts.getValue("required-for-activation"))) {
                this.buildVariantName = atts.getValue(NAME);
            }
        }
        else if (BUILD_OPTION.equals(localName)) {
            this.buildOptionName = atts.getValue(NAME);
        }
        else if (REPOSITORY.equals(localName)) {
            this.compartment.setDtrUrl(atts.getValue(DTR_URL));
        }
    }

    /**
     * Get read compartments.
     * 
     * @return the compartments read.
     */
    public List<Compartment> getCompartments() {
        return Collections.unmodifiableList(compartments);
    }

    /**
     * Gibt die zum Aktivieren festgelegte BuildVariant zurück.
     * 
     * @return zum Aktivieren festgelegte BuildVariant
     */
    public BuildVariant getBuildVariant() {
        if (this.buildVariant == null) {
            // default build options
            this.buildVariant = new BuildVariant("default");
        }

        return this.buildVariant;
    }
}
