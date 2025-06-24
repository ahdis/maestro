package net.ihe.gazelle.maestro.technical.handler;

import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.maestro.business.step.simulation.SimulationHandler;
import net.ihe.gazelle.maestro.business.step.simulation.SimulationHandlerMock;
import net.ihe.gazelle.maestro.business.step.userinteract.UserInteractionHandler;
import net.ihe.gazelle.maestro.business.step.userinteract.UserInteractionHandlerImpl;
import net.ihe.gazelle.maestro.business.step.validation.ValidationHandler;
import net.ihe.gazelle.maestro.business.step.validation.ValidationHandlerMock;

public class HandlerProviderMock implements HandlerProvider {

    @Override
    public Handler getHandler(Class<? extends Handler> handlerType, String serviceName) {
        if (handlerType.isAssignableFrom(ValidationHandler.class)) {
            return new ValidationHandlerMock();
        } else if (handlerType.isAssignableFrom(SimulationHandler.class)) {
            return new SimulationHandlerMock(null);
        } else if (handlerType.isAssignableFrom(UserInteractionHandler.class)) {
            return new UserInteractionHandlerImpl();
        }
        return null;
    }
}
