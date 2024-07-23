package net.ihe.gazelle.business.exception;

public class DuplicatedStepsException extends RuntimeException{
    public DuplicatedStepsException() {
        super();
    }

    public DuplicatedStepsException(String message) {
        super(message);
    }

    public DuplicatedStepsException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicatedStepsException(Throwable cause) {
        super(cause);
    }
}
