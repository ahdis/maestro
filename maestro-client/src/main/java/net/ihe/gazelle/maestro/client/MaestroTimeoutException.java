package net.ihe.gazelle.maestro.client;

public class MaestroTimeoutException extends MaestroClientException {

    public MaestroTimeoutException(String message, Throwable cause) {
        super(0, message, cause);
    }
}
