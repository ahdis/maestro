package net.ihe.gazelle.maestro.xml.extraction.step.technical.xml;

import net.ihe.gazelle.maestro.xml.extraction.step.business.xml.XmlExtractionService;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static javax.xml.transform.TransformerFactory.newInstance;

/**
 * Implementation of XmlExtractionService that extracts values from XML strings using XPath.
 */
public class XmlExtractionServiceImpl implements XmlExtractionService {

    private final DocumentBuilderFactory documentBuilderFactory;
    private final XPathFactory xPathFactory;

    public XmlExtractionServiceImpl() {
        this.documentBuilderFactory = DocumentBuilderFactory.newInstance();
        this.documentBuilderFactory.setNamespaceAware(true);
        this.xPathFactory = XPathFactory.newInstance();
    }

    @Override
    public String extractValue(byte[] xmlFile, String xmlPath) throws XmlExtractionException {
        assertParamsAreValid(xmlFile, xmlPath);

        // Normalize XML by removing all spaces between tags before parsing
        String normalizedXml = normalizeXml(new String(xmlFile, java.nio.charset.StandardCharsets.UTF_8));

        try {
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(normalizedXml)));

            XPath xpath = xPathFactory.newXPath();
            xpath.setNamespaceContext(new DocumentNamespaceContext(document));
            NodeList nodeList = (NodeList) xpath.evaluate(xmlPath, document, XPathConstants.NODESET);

            if (nodeList.getLength() == 0)
                return null;
            else if (nodeList.getLength() > 1)
                throw new XmlExtractionException("Multiple elements found for the given XML path (" + nodeList.getLength() + " elements). This is not supported yet by extraction step");

            Node node = nodeList.item(0);

            // Handle text nodes directly
            if (node.getNodeType() == Node.TEXT_NODE) {
                String value = node.getNodeValue();
                return value != null ? value.trim() : null;
            }

            // Handle attribute nodes
            if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
                return node.getNodeValue();
            }

            return nodeToString(node);
        } catch (XPathExpressionException | ParserConfigurationException | IOException | SAXException e) {
            throw new XmlExtractionException("Failed to extract value from XML using path: " + xmlPath, e);
        }
    }


    /**
     * Convert a DOM Node to its string representation.
     * @param node the DOM Node to convert
     * @return the string representation of the node
     * @throws XmlExtractionException if conversion fails
     */
    private String nodeToString(org.w3c.dom.Node node) throws XmlExtractionException {
        try {
            StringWriter writer = new StringWriter();
            Transformer transformer = newInstance().newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "no");
            transformer.transform(new DOMSource(node), new StreamResult(writer));
            return writer.toString();
        } catch (TransformerException e) {
            throw new XmlExtractionException("Failed to convert node to string", e);
        }
    }

    /**
     * Validate that the input parameters are not null or empty.
     */
    private void assertParamsAreValid(byte[] xmlFile, String xmlPath) {
        if (xmlFile == null || xmlFile.length == 0)
            throw new XmlExtractionException("Source XML is null or empty.");
        if (xmlPath == null || xmlPath.isEmpty())
            throw new XmlExtractionException("XML path is null or empty.");
    }

    /**
     * Normalize XML by removing all whitespace between tags to ensure clean extraction.
     * This removes indentation, newlines, and spaces between tags while preserving
     * meaningful whitespace inside text content.
     */
    private String normalizeXml(String xml) {
        return xml.replaceAll(">\\s+<", "><").trim();
    }
}