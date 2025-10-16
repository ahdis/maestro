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

import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;

import java.util.ArrayList;
import java.util.List;

/**
 * TestRunSessionsStore is a structure used to store TestRun's context and StepRunReportBuilder of executed step during
 * TestRun execution. It also contains a reference to the TestRun.
 */
public class TestRunSession {

   private final String testSuiteRunId;
   private final Context context;
   private final TestRun testRun;
   private final List<StepRunReportBuilder> stepReports;
   private final StepCursor stepCursor;

   public TestRunSession(String testSuiteRunId, TestRun testRun) {
      this(testSuiteRunId, testRun, StepCursor.from(testRun.getTest()), new Context(testRun.getInputs()));
   }

   public TestRunSession(String testSuiteRunId, TestRun testRun, StepCursor stepCursor, Context context) {
      this.testSuiteRunId = testSuiteRunId;
      this.context = context;
      this.testRun = testRun;
      this.stepCursor = stepCursor;
      this.stepReports = new ArrayList<>();
   }

   public String getTestSuiteRunId() {
      return testSuiteRunId;
   }

   public Context getContext() {
      return context;
   }

   public TestRun getTestRun() {
      return testRun;
   }

   public List<StepRunReportBuilder> getStepReports() {
      return new ArrayList<>(stepReports);
   }

   public void addStepRunReport(StepRunReportBuilder stepReportBuilder) {
      stepReports.add(stepReportBuilder);
   }

   public StepCursor getStepCursor() {
      return stepCursor;
   }
}
