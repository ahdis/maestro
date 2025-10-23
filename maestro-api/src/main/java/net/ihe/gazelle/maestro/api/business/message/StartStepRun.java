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

import net.ihe.gazelle.lang.Command;
import net.ihe.gazelle.maestro.spi.business.runner.StepRun;

/**
 * Step message used by producer to notify a subscriber to launch step execution
 */
@Command
public class StartStepRun implements Message {
   @Serial
   private static final long serialVersionUID = 1L;
   private String sessionId; // internal
   private String testRunId; // internal
   private String testId;
   private StepRun stepRun;

   public StartStepRun() {
   }

   public StartStepRun(String sessionId, String testRunId, String testId, StepRun stepRun) {
      this.sessionId = sessionId;
      this.testRunId = testRunId;
      this.testId = testId;
      this.stepRun = stepRun;
   }

   public String getSessionId() {
      return sessionId;
   }

   public StartStepRun setSessionId(String sessionId) {
      this.sessionId = sessionId;
      return this;
   }

   public String getTestRunId() {
      return testRunId;
   }

   public StartStepRun setTestRunId(String testRunId) {
      this.testRunId = testRunId;
      return this;
   }

   public String getTestId() {
      return testId;
   }

   public StartStepRun setTestId(String testId) {
      this.testId = testId;
      return this;
   }

   public StepRun getStepRun() {
      return stepRun;
   }

   public StartStepRun setStepRun(StepRun stepRun) {
      this.stepRun = stepRun;
      return this;
   }
}
