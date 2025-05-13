package net.ihe.gazelle.maestro.technical.ws.async;

/**
 * This class is a structure to store a callback URL and authorization token
 * for a given test set run execution received from TM
 */
public class TestRunStore {

    private String clientCallback;
    private String callbackAuthorization;

    public TestRunStore() {
        // Empty constructor
    }

    public String getClientCallback() {
        return clientCallback;
    }

    public void setClientCallback(String clientCallback) {
        this.clientCallback = clientCallback;
    }

    public String getCallbackAuthorization() {
        return callbackAuthorization;
    }

    public void setCallbackAuthorization(String callbackAuthorization) {
        this.callbackAuthorization = callbackAuthorization;
    }
}
