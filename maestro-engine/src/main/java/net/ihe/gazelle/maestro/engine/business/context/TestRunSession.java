/*
 * Copyright 2025 Kereval.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ihe.gazelle.maestro.engine.business.context;

import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;

import java.util.ArrayList;
import java.util.List;

/**
 * TestRunSessionsStore is a structure used to store TestRun's context and StepRunReportBuilder of executed step during
 * TestRun execution. It also contains a reference to the TestRun.
 */
public class TestRunSession {

   private final TestRun testRun;
   private final List<Property> context;
   private final StepCursor stepCursor;
   private final List<StepRunReport> stepReports;

   public TestRunSession(TestRun testRun) {
      this.testRun = testRun;
      this.context = new ArrayList<>(testRun.getInputs());
      this.stepCursor = StepCursor.from(testRun.getTest().getSteps());
      this.stepReports = new ArrayList<>();
   }

   public TestRun getTestRun() {
      return testRun;
   }

   public List<Property> getContext() {
      return context;
   }

   public StepCursor getStepCursor() {
      return stepCursor;
   }

   public boolean hasNextStep() {
      return stepCursor.hasNext();
   }

   public Step nextStep() {
      return stepCursor.next();
   }

   public List<StepRunReport> getStepReports() {
      return new ArrayList<>(stepReports);
   }

   public void addStepRunReport(StepRunReport stepReportBuilder) {
      stepReports.add(stepReportBuilder);
   }
}
