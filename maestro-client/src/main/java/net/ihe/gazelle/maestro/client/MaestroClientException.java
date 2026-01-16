package net.ihe.gazelle.maestro.client;

public class MaestroClientException extends RuntimeException {

    private final int status;

    public MaestroClientException(int status, String message) {
        super(message);
        this.status = status;
    }

    public MaestroClientException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
