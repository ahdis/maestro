package net.ihe.gazelle.maestro.client;

public class MaestroHttpError extends MaestroClientException {

    private final String body;

    public MaestroHttpError(int status, String body) {
        super(status, "HTTP " + status + (body != null && !body.isBlank() ? ": " + body : ""));
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
