package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.maestro.api.business.message.StartStepRun;
import net.ihe.gazelle.maestro.api.business.message.TestRunFinished;

public interface TestRunEventProducer {

   void command(StartStepRun startStepRun);

   void notify(TestRunFinished testRunFinished);

}
