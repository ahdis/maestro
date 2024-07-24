package net.ihe.gazelle.business.exception;

public class MissingArgumentException extends RuntimeException{

    public MissingArgumentException(String message) {
        super(message);
    }

    public MissingArgumentException() {
        super();
    }

    public MissingArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingArgumentException(Throwable cause) {
        super(cause);
    }
}
