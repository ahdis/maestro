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

package net.ihe.gazelle.maestro.api.business.message;

import java.io.Serial;

import net.ihe.gazelle.lang.Event;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.spi.business.runner.StepRun;

/**
 * Step message used by a producer to notify listener that a step execution ended. It contains a report builder loaded
 * with infos to build executed step's report
 */
@Event
public class StepRunFinished implements Message {
   @Serial
   private static final long serialVersionUID = 1L;
   private String testRunId;
   private String testId;
   private StepRun stepRun;
   private StepRunReportBuilder stepRunReportBuilder;

   public StepRunFinished() {
   }

   public StepRunFinished(String testRunId, String testId, StepRun stepRun, StepRunReportBuilder stepRunReportBuilder) {
      this.testRunId = testRunId;
      this.testId = testId;
      this.stepRun = stepRun;
      this.stepRunReportBuilder = stepRunReportBuilder;
   }

   public String getTestRunId() {
      return testRunId;
   }

   public StepRunFinished setTestRunId(String testRunId) {
      this.testRunId = testRunId;
      return this;
   }

   public StepRun getStepRun() {
      return stepRun;
   }

   public StepRunFinished setStepRun(StepRun stepRun) {
      this.stepRun = stepRun;
      return this;
   }

   public StepRunReportBuilder getStepRunReportBuilder() {
      return stepRunReportBuilder;
   }

   public StepRunFinished setStepRunReportBuilder(StepRunReportBuilder stepRunReportBuilder) {
      this.stepRunReportBuilder = stepRunReportBuilder;
      return this;
   }
}
