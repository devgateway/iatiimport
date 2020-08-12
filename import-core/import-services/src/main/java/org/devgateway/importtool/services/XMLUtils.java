package org.devgateway.importtool.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author Octavian Ciubotaru
 */
public class XMLUtils {

    /**
     * Parse XML document.
     *
     * @param text xml text to be parsed
     * @throws RuntimeException in case IO error, parsing error or parser initialization error
     * @return parsed document
     */
    public static Document createXMLDocument(String text) {
        InputStream inputStream = new ByteArrayInputStream(text.getBytes());
        return createXMLDocument(inputStream);
    }

    /**
     * Parse XML document.
     *
     * @param inputStream stream to be parsed
     * @throws RuntimeException in case IO error, parsing error or parser initialization error
     * @return parsed document
     */
    public static Document createXMLDocument(InputStream inputStream) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setIgnoringElementContentWhitespace(true);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(inputStream);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException("Error parsing data", e);
        }
    }
}
