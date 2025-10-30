package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.maestro.api.business.message.StartStepRun;
import net.ihe.gazelle.maestro.api.business.message.StartTestRun;
import net.ihe.gazelle.maestro.api.business.message.StartTestSuiteRun;
import net.ihe.gazelle.maestro.api.business.message.StepRunFinished;
import net.ihe.gazelle.maestro.api.business.message.TestRunFinished;
import net.ihe.gazelle.maestro.api.business.message.TestSuiteRunFinished;

import java.util.concurrent.ExecutorService;

public class HardWiredEventBroker implements MaestroEventProducer, TestSuiteEventProducer, TestRunEventProducer, StepRunEventProducer {

   private final ExecutorService executorService;

   private MaestroImpl maestro;
   private TestSuiteRunner testSuiteRunner;
   private TestRunner testRunner;
   private StepRunner stepRunner;

   public HardWiredEventBroker(ExecutorService executorService) {
      this.executorService = executorService;
   }

   public void setServices(MaestroImpl maestro, TestSuiteRunner testSuiteRunner, TestRunner testRunner, StepRunner stepRunner) {
      this.maestro = maestro;
      this.testSuiteRunner = testSuiteRunner;
      this.testRunner = testRunner;
      this.stepRunner = stepRunner;
   }

   @Override
   public void command(StartTestSuiteRun startTestSuiteRun) {
      executorService.submit(() -> testSuiteRunner.run(startTestSuiteRun));
   }

   @Override
   public void notify(StepRunFinished stepRunFinished) {
      executorService.submit(() -> testRunner.listen(stepRunFinished));
   }

   @Override
   public void command(StartStepRun startStepRun) {
      executorService.submit(() -> stepRunner.run(startStepRun));
   }

   @Override
   public void notify(TestRunFinished testRunFinished) {
      executorService.submit(() -> testSuiteRunner.listen(testRunFinished));
   }

   @Override
   public void command(StartTestRun startTestRun) {
      executorService.submit(() -> testRunner.run(startTestRun));
   }

   @Override
   public void notify(TestSuiteRunFinished testSuiteRunFinished) {
      executorService.submit(() -> maestro.listen(testSuiteRunFinished));
   }
}
