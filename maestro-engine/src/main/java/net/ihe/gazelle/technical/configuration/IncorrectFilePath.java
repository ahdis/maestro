package net.ihe.gazelle.technical.configuration;

public class IncorrectFilePath extends RuntimeException {
    public IncorrectFilePath(String message, Throwable cause) {
        super(message, cause);
    }
}
