package net.ihe.gazelle.maestro.runner;


public interface Handler {

    /**
     * @return true if service is available
     */
    boolean isAvailable();
}
