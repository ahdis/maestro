package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.maestro.api.business.message.StepRunFinished;

public interface StepRunEventProducer {

   void notify(StepRunFinished stepRunFinished);

}
