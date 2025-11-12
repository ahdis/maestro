package net.ihe.gazelle.maestro.xml.extraction.step.technical.xml;

import net.ihe.gazelle.maestro.xml.extraction.step.business.xml.XmlExtractionService;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;

public class XmlExtractionServiceImpl implements XmlExtractionService {

    private final DocumentBuilderFactory documentBuilderFactory;
    private final XPathFactory xPathFactory;

    public XmlExtractionServiceImpl() {
        this.documentBuilderFactory = DocumentBuilderFactory.newInstance();
        this.documentBuilderFactory.setNamespaceAware(true);
        this.xPathFactory = XPathFactory.newInstance();
    }

    @Override
    public String extractValue(String xmlFile, String xmlPath) throws XmlExtractionException {
        assertParamsAreValid(xmlFile, xmlPath);

        try {
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlFile)));

            XPath xpath = xPathFactory.newXPath();
            NodeList nodeList = (NodeList) xpath.evaluate(xmlPath, document, XPathConstants.NODESET);

            if (nodeList.getLength() == 0) {
                return null;
            } else if (nodeList.getLength() == 1) {
                return nodeList.item(0).getTextContent();
            } else {
                throw new XmlExtractionException("Multiple elements found for the given XML path (" + nodeList.getLength() + " elements). This is not supported yet by extraction step");
            }
        } catch (XPathExpressionException | ParserConfigurationException | IOException | SAXException e) {
            throw new XmlExtractionException("Failed to extract value from XML using path: " + xmlPath, e);
        }
    }

    private static void assertParamsAreValid(String xmlFile, String xmlPath) {
        if (xmlFile == null || xmlFile.isEmpty())
            throw new XmlExtractionException("Source XML is null or empty.");
        if (xmlPath == null || xmlPath.isEmpty())
            throw new XmlExtractionException("XML path is null or empty.");
    }
}
