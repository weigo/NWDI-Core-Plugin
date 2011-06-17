package org.arachna.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Helper class for DOM builders.
 *
 * @author Dirk Weigenand
 */
public final class DomHelper {
    /**
     * the document to use creating elements.
     */
    private final Document document;

    /**
     *
     * @param document
     *            Dokument aus dem die Elemente erzeugt werden
     */
    public DomHelper(final Document document) {
        this.document = document;
    }

    /**
     * Get the document used in this <code>DOMCreator</code>.
     *
     * @return the document used in this <code>DOMCreator</code>.
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Erzeugt ein DOM-Element mit dem �bergebenen Namen.
     *
     * @param elementName
     *            Elementname
     * @return erzeugtes Element mit �bergebenem Namen
     */
    public Element createElement(final String elementName) {
        return document.createElement(elementName);
    }

    /**
     * Erzeugt ein DOM-Element mit dem �bergebenen Namen und Attributnamen und
     * -werten. Die L�nge der beiden �bergebenen Felder muss
     * �bereinstimmen.
     *
     * @param elementName
     *            Elementname
     * @param attributeNames
     *            Attributnamen f�r das zu erzeugende Element
     * @param attributeValues
     *            zu den Attributnamen geh�rige Attributwerte f�r das zu
     *            erzeugende Element
     * @return erzeugtes Element mit �bergebenem Namen und Attributen
     */
    public Element createElement(final String elementName, final String[] attributeNames, final String[] attributeValues) {
        final Element element = this.createElement(elementName);

        for (int i = 0; i < attributeNames.length; i++) {
            element.setAttribute(attributeNames[i], attributeValues[i]);
        }

        return element;
    }

    /**
     * Erzeugt ein DOM-Element mit dem �bergebenen Namen , Attributnamen und
     * -wert.
     *
     * @param elementName
     *            Elementname
     * @param attributeName
     *            Attributname f�r das zu erzeugende Element
     * @param attributeValue
     *            zum Attributnamen geh�riger Attributwert f�r das zu
     *            erzeugende Element
     * @return erzeugtes Element mit �bergebenem Namen und Attribut
     */
    public Element createElement(final String elementName, final String attributeName, final String attributeValue) {
        return this.createElement(elementName, new String[] { attributeName }, new String[] { attributeValue });
    }

    /**
     * Erzeugt ein Textelement.
     *
     * @param elementName
     *            Name des Elements
     * @param text
     *            Text des Elements
     * @return Element mit einem Textknoten
     */
    public Element createText(final String elementName, final String text) {
        final Element element = this.createElement(elementName);
        element.setTextContent(text);
        // element.setNodeValue(text);
        // element.appendChild(this.document.createTextNode(text));

        return element;
    }
}
