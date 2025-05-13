package net.ihe.gazelle.maestro.business.step.itb;

import net.ihe.gazelle.maestro.runner.Handler;

public class ItbHandler implements Handler {

    protected ItbHandler() {
        /*Empty constructor used for mocking*/
    }

    public void handle(ItbStep step) {

    }

    /**
     * Override from {@link Handler} Interface
     */
    @Override
    public boolean isAvailable() {
        return true;
    }

}
