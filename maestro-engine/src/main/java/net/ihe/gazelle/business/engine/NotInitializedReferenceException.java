package net.ihe.gazelle.business.engine;

public class NotInitializedReferenceException extends RuntimeException{
    public NotInitializedReferenceException(){super();}
    public NotInitializedReferenceException(String message) {
        super(message);
    }

    public NotInitializedReferenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotInitializedReferenceException(Throwable cause) {
        super(cause);
    }
}
