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

import net.ihe.gazelle.lang.Event;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.spi.business.StepRun;

import java.io.Serial;

/**
 * Step message used by a producer to notify listener that a step execution ended. It contains a report builder loaded
 * with infos to build executed step's report
 */
@Event
public class StepRunFinished implements Message {
   @Serial
   private static final long serialVersionUID = 1L;
   private String sessionId;
   private String testId;
   private StepRun stepRun;
   private StepRunReport stepRunReport;

   public StepRunFinished() {
   }

   public StepRunFinished(String sessionId, String testId, StepRun stepRun, StepRunReport stepRunReport) {
      this.sessionId = sessionId;
      this.testId = testId;
      this.stepRun = stepRun;
      this.stepRunReport = stepRunReport;
   }

   public String getSessionId() {
      return sessionId;
   }

   public StepRunFinished setSessionId(String sessionId) {
      this.sessionId = sessionId;
      return this;
   }

   public String getTestId() {
      return testId;
   }

   public StepRunFinished setTestId(String testId) {
      this.testId = testId;
      return this;
   }

   public StepRun getStepRun() {
      return stepRun;
   }

   public StepRunFinished setStepRun(StepRun stepRun) {
      this.stepRun = stepRun;
      return this;
   }

   public StepRunReport getStepRunReport() {
      return stepRunReport;
   }

   public StepRunFinished setSteprunReport(StepRunReport stepRunReport) {
      this.stepRunReport = stepRunReport;
      return this;
   }
}
