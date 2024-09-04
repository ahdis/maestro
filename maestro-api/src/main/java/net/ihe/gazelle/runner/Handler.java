package net.ihe.gazelle.runner;


public interface Handler {

    /**
     * @param service service to contact
     * @return true if service is available
     */
    boolean isAvailable(String service);
}
