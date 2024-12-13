package net.ihe.gazelle.maestro.technical.ws;

public class ParsingErrorException extends RuntimeException{
    public ParsingErrorException() {
        super();
    }

    public ParsingErrorException(String message) {
        super(message);
    }

    public ParsingErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParsingErrorException(Throwable cause) {
        super(cause);
    }
}
