package net.ihe.gazelle.handler;


public interface Handler {

    /**
     * @param service service to contact
     * @return true if service is available
     */
    boolean isAvailable(String service);
}
