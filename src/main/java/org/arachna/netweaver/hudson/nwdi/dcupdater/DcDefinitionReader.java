/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import java.util.Stack;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentType;
import org.arachna.xml.AbstractDefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * A Reader for <code>.dcdef</code> configuration files.
 * 
 * @author Dirk Weigenand
 */
public class DcDefinitionReader extends AbstractDefaultHandler {
    /**
     * tag name for component name.
     */
    private static final String NAME = "name";

    /**
     * tag name for a component's short description.
     */
    private static final String CAPTION = "caption";

    /**
     * tag name for a development component.
     */
    private static final String DEVELOPMENT_COMPONENT = "development-component";

    /**
     * tag name for a development components vendor.
     */
    private static final String VENDOR = "vendor";

    /**
     * compound tag name for a development components type.
     */
    private static final String COMPONENT_TYPE = "component-type";

    /**
     * tag name for a development components sub type.
     */
    private static final String COMPONENT_SUB_TYPE = "sub-type";

    /**
     * tag name for a development components type.
     */
    private static final String TYPE = "type";

    /**
     * tag name for a development components package-folder (one of many).
     */
    private static final String PACKAGE_FOLDER = "package-folder";

    /**
     * tag name for a development components dependencies (used development
     * components).
     */
    private static final String DEPENDENCIES = "dependencies";

    /**
     * development component currently worked upon.
     */
    private final DevelopmentComponent component;

    /**
     * reader for used development components.
     */
    private DcDependenciesReader usedDependenciesReader;

    /**
     * Stack used for tracking parent elements.
     */
    private final Stack<String> parentElements = new Stack<String>();

    /**
     * type of development component.
     */
    private String componentType;

    /**
     * sub type of a development component.
     */
    private String componentSubType;

    /**
     * Create instance of a reader of a developments components definition.
     * 
     * @param component
     *            development component to update from definition
     */
    public DcDefinitionReader(final DevelopmentComponent component) {
        super(null);
        this.component = component;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(final String uri, final String localName, final String name) throws SAXException {
        this.parentElements.pop();
        final String parent = this.getParentElementName();

        if (CAPTION.equals(localName)) {
            this.component.setDescription(this.getText());
        }
        else if (DEPENDENCIES.equals(localName)) {
            this.component.addAll(this.usedDependenciesReader.getUsedComponents());
            this.usedDependenciesReader = null;
        }
        else if (COMPONENT_TYPE.equals(localName)) {
            this.component.setType(DevelopmentComponentType.fromString(this.componentType, this.componentSubType));
        }
        else if (DEVELOPMENT_COMPONENT.equals(parent)) {
            updateVendorOrName(localName);
        }
        else if (COMPONENT_TYPE.equals(parent)) {
            updateDevelopmentComponentTypeInfo(localName);
        }
        else if (PACKAGE_FOLDER.equals(localName)) {
            this.component.addSourceFolder(this.getText());
        }

        this.getText();
    }

    /**
     * Set the type or subtype of the current component depending on the given
     * tag name.
     * 
     * @param tagName
     *            name of the current tag
     */
    private void updateDevelopmentComponentTypeInfo(final String tagName) {
        if (TYPE.equals(tagName)) {
            this.componentType = this.getText();
        }
        else if (COMPONENT_SUB_TYPE.equals(tagName)) {
            this.componentSubType = this.getText();
        }
    }

    /**
     * Set the components name or vendor depending on the value of the given tag
     * name.
     * 
     * @param tagName
     *            name of the current tag.
     */
    private void updateVendorOrName(final String tagName) {
        if (NAME.equals(tagName)) {
            this.component.setName(this.getText());
        }
        else if (VENDOR.equals(tagName)) {
            this.component.setVendor(this.getText());
        }
    }

    /**
     * Returns the name of the current parent element or an empty string.
     * 
     * @return the name of the current parent element or an empty string.
     */
    private String getParentElementName() {
        String parent = "";

        if (!this.parentElements.isEmpty()) {
            parent = this.parentElements.peek();
        }

        return parent;
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
        this.parentElements.push(localName);

        if (DEPENDENCIES.equals(localName)) {
            this.usedDependenciesReader = new DcDependenciesReader(this);
            this.usedDependenciesReader.setXmlReader(this.getXmlReader());
            this.getXmlReader().setContentHandler(this.usedDependenciesReader);
        }
    }
}
