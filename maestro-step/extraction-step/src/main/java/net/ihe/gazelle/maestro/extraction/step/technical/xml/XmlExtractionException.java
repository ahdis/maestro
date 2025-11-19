package net.ihe.gazelle.maestro.extraction.step.technical.xml;

/**
 * Exception thrown when there is an error during XML extraction
 */
public class XmlExtractionException extends RuntimeException {
    public XmlExtractionException(String message) {
        super(message);
    }

    public XmlExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
}
