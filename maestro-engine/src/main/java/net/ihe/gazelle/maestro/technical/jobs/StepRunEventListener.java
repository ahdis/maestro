package net.ihe.gazelle.maestro.technical.jobs;

import net.ihe.gazelle.maestro.runner.StepMessage;

public interface StepRunEventListener {

    void receiveStepEvent(StepMessage stepMessage);

}
