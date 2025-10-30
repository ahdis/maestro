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
import net.ihe.gazelle.maestro.api.business.testreport.TestReportSummary;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;

import java.io.Serial;

/**
 * A message to notify that a TestSet Execution Ended and to send a TestReport
 */
@Event
public class TestSuiteRunFinished implements Message {
   @Serial
   private static final long serialVersionUID = 1L;
   private String sessionId;
   private TestSuiteRun testSuiteRun;
   private TestReportSummary reportSummary;

   public TestSuiteRunFinished() {
   }

   public TestSuiteRunFinished(String sessionId, TestSuiteRun testSuiteRun, TestReportSummary reportSummary) {
      this.sessionId = sessionId;
      this.testSuiteRun = testSuiteRun;
      this.reportSummary = reportSummary;
   }

   public String getSessionId() {
      return sessionId;
   }

   public TestSuiteRunFinished setSessionId(String sessionId) {
      this.sessionId = sessionId;
      return this;
   }

   public TestSuiteRun getTestSuiteRun() {
      return testSuiteRun;
   }

   public TestSuiteRunFinished setTestSuiteRun(TestSuiteRun testSuiteRun) {
      this.testSuiteRun = testSuiteRun;
      return this;
   }

   public TestReportSummary getReportSummary() {
      return reportSummary;
   }

   public TestSuiteRunFinished setReportSummary(TestReportSummary reportSummary) {
      this.reportSummary = reportSummary;
      return this;
   }
}
