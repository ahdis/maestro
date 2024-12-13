package net.ihe.gazelle.maestro.business.step.validation;

public class ServiceClientException extends RuntimeException {
    public ServiceClientException(String message) {
        super(message);
    }

    public ServiceClientException(String message, Throwable cause) {
      super(message, cause);
    }
}
