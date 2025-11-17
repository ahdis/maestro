package net.ihe.gazelle.maestro.xml.extraction.step.business.xml;

import net.ihe.gazelle.maestro.xml.extraction.step.technical.xml.XmlExtractionException;

import java.util.Map;

public interface XmlExtractionService {

    /**
     * Extracts a value from the given XML string based on the provided XML path.
     * @param xmlFile the source XML string
     * @param xmlPath the XML path to extract the value from
     * @param namespaces a map of namespace prefixes to URIs (key: prefix, value: URI), empty key is default namespace
     * @return the extracted value or null if not found
     * @throws XmlExtractionException if extraction fails (bad xpath, multiple nodes found, etc.)
     */
    String extractValue(byte[] xmlFile, String xmlPath, Map<String,String> namespaces) throws XmlExtractionException;
}
