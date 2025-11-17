package net.ihe.gazelle.maestro.extraction.step.technical.xml;

import net.ihe.gazelle.maestro.extraction.step.business.xml.XmlExtractionService;
import net.sf.saxon.s9api.*;

import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Implementation of XmlExtractionService that extracts values from XML strings using XPath 2.0+ (via Saxon).
 * This implementation uses Saxon-HE which supports XPath 2.0, 3.0, and 3.1.
 */
public class XmlExtractionServiceImpl implements XmlExtractionService {

    private final Processor processor;

    public XmlExtractionServiceImpl() {
        // Saxon Processor supports XPath 2.0, 3.0, and 3.1
        this.processor = new Processor(false);
    }

    @Override
    public String extractValue(byte[] xmlFile, String xmlPath, Map<String,String> namespaces) throws XmlExtractionException {
        assertParamsAreValid(xmlFile, xmlPath);

        // Normalize XML by removing all spaces between tags before parsing
        String normalizedXml = normalizeXml(new String(xmlFile, StandardCharsets.UTF_8));

        try {
            // Build the XML document using Saxon
            DocumentBuilder builder = processor.newDocumentBuilder();
            XdmNode document = builder.build(new StreamSource(new StringReader(normalizedXml)));

            // Compile and evaluate the XPath expression
            XPathCompiler xpathCompiler = processor.newXPathCompiler();
            handleNameSpaces(namespaces, xpathCompiler, document);
            XPathSelector selector = xpathCompiler.compile(xmlPath).load();
            selector.setContextItem(document);

            // Evaluate and get results
            XdmValue result = selector.evaluate();
            if (result.isEmpty()) {
                return null;
            } else if (result.size() > 1) {
                throw new XmlExtractionException("Multiple elements found for the given XML path (" + result.size() + " elements). This is not supported yet by extraction step");
            }

            XdmItem item = result.itemAt(0);
            if (item.isAtomicValue()) {
                // For atomic values (text, numbers, etc.)
                return item.getStringValue();
            } else if (item instanceof XdmNode node) {
                // Handle attribute nodes
                if (node.getNodeKind() == XdmNodeKind.ATTRIBUTE) {
                    return node.getStringValue();
                }

                // Handle text nodes
                if (node.getNodeKind() == XdmNodeKind.TEXT) {
                    String value = node.getStringValue();
                    return value != null ? value.trim() : null;
                }
                return serializeNode(node);
            }

            return item.getStringValue();
        } catch (SaxonApiException e) {
            throw new XmlExtractionException("Failed to extract value from XML using path: " + xmlPath, e);
        }
    }

    private void handleNameSpaces(Map<String, String> namespaces, XPathCompiler xpathCompiler, XdmNode document) {
        if (namespaces == null || namespaces.isEmpty()) {
            // If no custom namespaces provided, auto-detect from document
            declareNamespaces(xpathCompiler, document);
        } else {
            // Use provided custom namespaces
            for (Map.Entry<String, String> namespace : namespaces.entrySet()) {
                String prefix = namespace.getKey();
                String uri = namespace.getValue();

                // Handle default namespace (empty prefix)
                if (prefix.isEmpty()) {
                    // Saxon requires a non-empty prefix for XPath, use "default"
                    xpathCompiler.declareNamespace("default", uri);
                } else {
                    xpathCompiler.declareNamespace(prefix, uri);
                }
            }
        }
    }

    /**
     * Declare namespaces from the document to the XPath compiler.
     * This method extracts namespace declarations from the document and makes them available to XPath.
     * It recursively collects all namespace declarations from all elements in the document.
     */
    private void declareNamespaces(XPathCompiler compiler, XdmNode document) {
        // Collect all namespace declarations from the entire document
        java.util.Map<String, String> namespaceMap = new java.util.HashMap<>();
        collectNamespaces(document, namespaceMap);

        // Declare all collected namespaces
        for (java.util.Map.Entry<String, String> entry : namespaceMap.entrySet()) {
            String prefix = entry.getKey();
            String uri = entry.getValue();

            // Skip xml namespace (it's predefined)
            if (!"xml".equals(prefix)) {
                if (prefix.isEmpty()) {
                    // Default namespace - assign a prefix for XPath usage
                    compiler.declareNamespace("default", uri);
                } else {
                    compiler.declareNamespace(prefix, uri);
                }
            }
        }
    }

    /**
     * Recursively collect all namespace declarations from a node and its descendants.
     */
    private void collectNamespaces(XdmNode node, java.util.Map<String, String> namespaceMap) {
        if (node.getNodeKind() == XdmNodeKind.ELEMENT) {
            // Collect namespaces from this element
            XdmSequenceIterator<XdmNode> namespaces = node.axisIterator(Axis.NAMESPACE);
            while (namespaces.hasNext()) {
                XdmNode ns = namespaces.next();
                String prefix = ns.getNodeName() != null ? ns.getNodeName().getLocalName() : "";
                String uri = ns.getStringValue();
                namespaceMap.put(prefix, uri);
            }
        }

        // Recurse to children
        for (XdmNode child : node.children()) {
            collectNamespaces(child, namespaceMap);
        }
    }

    /**
     * Serialize an XdmNode to a compact XML string without indentation.
     */
    private String serializeNode(XdmNode node) throws SaxonApiException {
        Serializer serializer = processor.newSerializer();
        serializer.setOutputProperty(Serializer.Property.METHOD, "xml");
        serializer.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "yes");
        serializer.setOutputProperty(Serializer.Property.INDENT, "no");

        java.io.StringWriter writer = new java.io.StringWriter();
        serializer.setOutputWriter(writer);
        serializer.serializeNode(node);

        return writer.toString();
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