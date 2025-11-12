package net.ihe.gazelle.maestro.xml.extraction.step.technical;

import net.ihe.gazelle.maestro.xml.extraction.step.business.XmlExtractionService;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class XmlExtractionServiceImpl implements XmlExtractionService {

    private final DocumentBuilderFactory documentBuilderFactory;
    private final XPathFactory xPathFactory;

    public XmlExtractionServiceImpl() {
        this.documentBuilderFactory = DocumentBuilderFactory.newInstance();
        this.documentBuilderFactory.setNamespaceAware(true);
        this.xPathFactory = XPathFactory.newInstance();
    }

    @Override
    public String extractValue(String sourceXml, String xmlPath) throws XmlExtractionException {
        assertParamsAreValid(sourceXml, xmlPath);

        try {
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(sourceXml)));

            XPath xpath = xPathFactory.newXPath();
            NodeList nodeList = (NodeList) xpath.evaluate(xmlPath, document, XPathConstants.NODESET);

            if (nodeList.getLength() == 0)
                throw new XmlExtractionException("No element found for the given XML path: " + xmlPath);

            if (nodeList.getLength() == 1)
                return nodeList.item(0).getTextContent();

            // Multiple nodes: return as comma-separated values or JSON array
            List<String> values = new ArrayList<>();
            for (int i = 0; i < nodeList.getLength(); i++) {
                values.add(nodeList.item(i).getTextContent());
            }
            return String.join(",", values);
        } catch (Exception e) {
            throw new XmlExtractionException("Failed to extract value from XML using path: " + xmlPath, e);
        }
    }

    private static void assertParamsAreValid(String sourceXml, String xmlPath) {
        if (sourceXml == null || sourceXml.isEmpty())
            throw new XmlExtractionException("Source XML is null or empty.");
        if (xmlPath == null || xmlPath.isEmpty())
            throw new XmlExtractionException("XML path is null or empty.");
    }
}
