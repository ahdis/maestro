package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.maestro.api.business.message.StartTestRun;
import net.ihe.gazelle.maestro.api.business.message.TestSuiteRunFinished;

public interface TestSuiteEventProducer {

   void command(StartTestRun startTestRun);

   void notify(TestSuiteRunFinished testSuiteRunFinished);

}
