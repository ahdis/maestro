package net.ihe.gazelle.maestro.technical.configuration;

public class ReadingFileException extends RuntimeException {

    public ReadingFileException(String message) {
        super(message);
    }

    public ReadingFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
