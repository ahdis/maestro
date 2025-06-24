package net.ihe.gazelle.maestro.business.step.userinteract;

import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;

public interface UserInteractionHandler extends Handler {

    String NAME = "UserInteractionHandler";
    String INTERFACE_NAME = "User Interaction API";

    void displayMessage(String message);
}
