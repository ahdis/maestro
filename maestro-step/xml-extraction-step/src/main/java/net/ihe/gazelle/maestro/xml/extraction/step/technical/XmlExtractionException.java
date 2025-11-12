package net.ihe.gazelle.maestro.xml.extraction.step.technical;

public class XmlExtractionException extends RuntimeException {
    public XmlExtractionException(String message) {
        super(message);
    }

    public XmlExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
}
