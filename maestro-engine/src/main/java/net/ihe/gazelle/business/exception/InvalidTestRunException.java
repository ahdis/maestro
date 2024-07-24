package net.ihe.gazelle.business.exception;

public class InvalidTestRunException extends RuntimeException{
    public InvalidTestRunException() {
        super();
    }

    public InvalidTestRunException(String message) {
        super(message);
    }

    public InvalidTestRunException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidTestRunException(Throwable cause) {
        super(cause);
    }
}
