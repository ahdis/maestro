package net.ihe.gazelle.maestro.xml.extraction.step.business;

import net.ihe.gazelle.maestro.xml.extraction.step.technical.XmlExtractionException;

public interface XmlExtractionService {

    /**
     * Extracts a value from the given XML string based on the provided XML path.
     * @param sourceXml the source XML string
     * @param xmlPath the XML path to extract the value from
     * @return the extracted value
     * @throws XmlExtractionException if extraction fails
     */
    String extractValue(String sourceXml, String xmlPath) throws XmlExtractionException;
}
