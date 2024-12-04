package net.ihe.gazelle.maestro.technical.ws;

public class MissingArgumentException extends RuntimeException{

    public MissingArgumentException(String message) {
        super(message);
    }

    public MissingArgumentException(Throwable cause) {
        super(cause);
    }
}
