/**
 *
 */
package org.arachna.netweaver.dc.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentComponentType;
import org.arachna.netweaver.dc.types.PublicPart;
import org.arachna.netweaver.dc.types.PublicPartReference;
import org.arachna.netweaver.dc.types.PublicPartType;
import org.arachna.xml.AbstractDefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Reader for development components from a development configurations config.
 * 
 * @author Dirk Weigenand
 */
public class DevelopmentComponentReader extends AbstractDefaultHandler {
    /**
     * attribute name signaling that a component probably needs rebuilding
     * (depending on the attributes value).
     */
    private static final String NEEDS_REBUILD = "needsRebuild";

    /**
     * compound tag name for development components of a compartment.
     */
    private static final String DEVELOPMENT_COMPONENTS = "development-components";

    /**
     * tag name for a package-folder of a development component.
     */
    private static final String PACKAGE_FOLDER = "package-folder";

    /**
     * tag name for description of a development component.
     */
    private static final String DESCRIPTION = "description";

    /**
     * tag name for caption of a development component.
     */
    private static final String CAPTION = "caption";

    /**
     * tag name for a public part of a development component.
     */
    private static final String PUBLIC_PART = "public-part";

    /**
     * attribute value for public part reference.
     */
    private static final String AT_RUN_TIME = "at-run-time";

    /**
     * attribute value for public part reference.
     */
    private static final String AT_BUILD_TIME = "at-build-time";

    /**
     * tag name for a dependency of development component.
     */
    private static final String DEPENDENCY = "dependency";

    /**
     * tag name for a public part reference of a development component.
     */
    private static final String PP_REF = "pp-ref";

    /**
     * tag name for type of development component.
     */
    private static final String TYPE = "type";

    /**
     * tag name for sub type of development component.
     */
    private static final String SUB_TYPE = "sub-type";

    /**
     * tag name for vendor of a development component.
     */
    private static final String VENDOR = "vendor";

    /**
     * tag name for name of a development component.
     */
    private static final String NAME = "name";

    /**
     * tag name for a development component.
     */
    private static final String DEVELOPMENT_COMPONENT = "development-component";

    /**
     * list of development components read from configuration.
     */
    private final List<DevelopmentComponent> developmentComponents = new ArrayList<DevelopmentComponent>();

    /**
     * factory/registry for development components.
     */
    private final DevelopmentComponentFactory developmentComponentFactory;

    /**
     * development component currently worked upon.
     */
    private DevelopmentComponent currentComponent;

    /**
     * public part reference currently worked upon.
     */
    private PublicPartReference reference;

    /**
     * compound tag name for development components of a compartment.
     */
    private final Stack<String> elements = new Stack<String>();

    /**
     * public part currently worked upon.
     */
    private PublicPart publicPart;

    /**
     * Create an instance of a reader for development components from a
     * development configurations config.
     * 
     * @param xmlReader
     *            reader used to generate sax events
     * @param parent
     *            parent handler
     * @param developmentComponentFactory
     *            factory/registry for development components
     */
    public DevelopmentComponentReader(final XMLReader xmlReader, final AbstractDefaultHandler parent,
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
        final String currentParent = getCurrentParent();

        if (DESCRIPTION.equals(localName)) {
            if (DEVELOPMENT_COMPONENT.equals(currentParent)) {
                currentComponent.setDescription(getText());
            }
            else if (PUBLIC_PART.equals(currentParent)) {
                publicPart.setDescription(getText());
            }
        }
        else if (DEPENDENCY.equals(localName)) {
            currentComponent.add(reference);
            reference = null;
        }
        else if (DEVELOPMENT_COMPONENT.equals(localName)) {
            developmentComponents.add(currentComponent);
            currentComponent = null;
        }
        else if (PACKAGE_FOLDER.equals(localName)) {
            currentComponent.addSourceFolder(getText());
        }
        else if (PUBLIC_PART.equals(localName)) {
            currentComponent.add(publicPart);
            publicPart = null;
        }
        else if (DEVELOPMENT_COMPONENTS.equals(localName)) {
            getXmlReader().setContentHandler(getParent());
            getParent().endElement(uri, localName, qName);
        }
        else if ("classes".equals(localName)) {
            currentComponent.setOutputFolder(getText());
        }
        else if (CAPTION.equals(localName)) {
            currentComponent.setCaption(getText());
        }
    }

    /**
     * Returns the current parent tag from the stack of unclosed tags.
     * 
     * @return the tag name of the currently processed parent element or an
     *         empty string.
     */
    private String getCurrentParent() {
        String currentParent = "";

        if (!elements.isEmpty()) {
            elements.pop();

            if (!elements.isEmpty()) {
                currentParent = elements.peek();
            }
        }

        return currentParent;
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
        elements.push(localName);

        if (DEVELOPMENT_COMPONENT.equals(localName)) {
            currentComponent =
                developmentComponentFactory.create(attributes.getValue(VENDOR), attributes.getValue(NAME),
                    DevelopmentComponentType.fromString(attributes.getValue(TYPE), attributes.getValue(SUB_TYPE)));
            currentComponent.setNeedsRebuild(Boolean.valueOf(attributes.getValue(NEEDS_REBUILD)));
        }
        else if (DEPENDENCY.equals(localName)) {
            reference =
                new PublicPartReference(attributes.getValue(VENDOR), attributes.getValue(NAME),
                    attributes.getValue(PP_REF));
        }
        else if (AT_BUILD_TIME.equals(localName)) {
            reference.setAtBuildTime(true);
        }
        else if (AT_RUN_TIME.equals(localName)) {
            reference.setAtRunTime(true);
        }
        else if (PUBLIC_PART.equals(localName)) {
            String type = attributes.getValue(TYPE);

            if (type == null) {
                type = PublicPartType.COMPILE.toString();
            }

            publicPart =
                new PublicPart(attributes.getValue(NAME), attributes.getValue(CAPTION), "",
                    PublicPartType.fromString(type));
        }
    }

    /**
     * @return the developmentComponents
     */
    public final List<DevelopmentComponent> getDevelopmentComponents() {
        return developmentComponents;
    }
}
