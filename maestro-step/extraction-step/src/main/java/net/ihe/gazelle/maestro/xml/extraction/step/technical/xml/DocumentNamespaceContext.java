package net.ihe.gazelle.maestro.xml.extraction.step.technical.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Minimal NamespaceContext that reads namespace declarations from the document.
 * Required for XPath to resolve namespace prefixes like "saml2:" or "ds:" in XPath expressions.
 * Note: Only collects prefixed namespaces (xmlns:prefix="uri"), not default namespaces (xmlns="uri").
 */
public class DocumentNamespaceContext implements NamespaceContext {
    private static final String XMLNS_PREFIX = "xmlns:";
    private static final int XMLNS_PREFIX_LENGTH = 6;

    private final Map<String, String> namespaces = new HashMap<>();

    DocumentNamespaceContext(Document document) {
        collectNamespaces(document.getDocumentElement());
    }

    private void collectNamespaces(Node node) {
        if (node.getNodeType() == Node.ELEMENT_NODE && node.hasAttributes()) {
            var attributes = node.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attr = attributes.item(i);
                String name = attr.getNodeName();
                if (name.startsWith(XMLNS_PREFIX)) {
                    String prefix = name.substring(XMLNS_PREFIX_LENGTH);
                    namespaces.put(prefix, attr.getNodeValue());
                }
            }
        }

        // Recursively collect from children
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            collectNamespaces(children.item(i));
        }
    }

    @Override
    public String getNamespaceURI(String prefix) {
        return namespaces.getOrDefault(prefix, XMLConstants.NULL_NS_URI);
    }

    @Override
    public String getPrefix(String uri) {
        return null;
    }

    @Override
    public Iterator<String> getPrefixes(String uri) {
        return null;
    }
}

