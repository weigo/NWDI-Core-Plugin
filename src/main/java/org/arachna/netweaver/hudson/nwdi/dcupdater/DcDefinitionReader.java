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
     * tag name for a component's long description.
     */
    private static final String DESCRIPTION = "description";

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
        parentElements.pop();
        final String parent = getParentElementName();

        if (CAPTION.equals(localName)) {
            component.setCaption(getText());
        }
        else if (DESCRIPTION.equals(localName)) {
            component.setDescription(getText());
        }
        else if (DEPENDENCIES.equals(localName)) {
            component.setUsedComponents(usedDependenciesReader.getUsedComponents());
            usedDependenciesReader = null;
        }
        else if (COMPONENT_TYPE.equals(localName)) {
            component.setType(DevelopmentComponentType.fromString(componentType, componentSubType));
        }
        else if (DEVELOPMENT_COMPONENT.equals(parent)) {
            updateVendorOrName(localName);
        }
        else if (COMPONENT_TYPE.equals(parent)) {
            updateDevelopmentComponentTypeInfo(localName);
        }
        else if (PACKAGE_FOLDER.equals(localName)) {
            component.addSourceFolder(getText());
        }

        getText();
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
            componentType = getText();
        }
        else if (COMPONENT_SUB_TYPE.equals(tagName)) {
            componentSubType = getText();
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
            component.setName(getText());
        }
        else if (VENDOR.equals(tagName)) {
            component.setVendor(getText());
        }
    }

    /**
     * Returns the name of the current parent element or an empty string.
     * 
     * @return the name of the current parent element or an empty string.
     */
    private String getParentElementName() {
        String parent = "";

        if (!parentElements.isEmpty()) {
            parent = parentElements.peek();
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
        parentElements.push(localName);

        if (DEPENDENCIES.equals(localName)) {
            usedDependenciesReader = new DcDependenciesReader(this);
            usedDependenciesReader.setXmlReader(getXmlReader());
            getXmlReader().setContentHandler(usedDependenciesReader);
        }
    }
}
