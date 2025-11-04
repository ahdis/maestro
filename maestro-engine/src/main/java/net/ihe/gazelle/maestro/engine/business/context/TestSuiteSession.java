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

import net.ihe.gazelle.maestro.api.business.testreport.TestRunReport;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * TestSuiteSession is a structure used to store report builder from executed TestRun, testSet's id and reference to
 * this testSet.
 */
public class TestSuiteSession {

   private final TestSuiteRun testSuiteRun;
   private final TestRunCursor testRunCursor;
   private final List<TestRunReport> testRunReports;

   public TestSuiteSession(TestSuiteRun testSuiteRun, TestRunCursor testRunCursor) {
      this.testSuiteRun = testSuiteRun;
      this.testRunCursor = testRunCursor;
       this.testRunReports = new ArrayList<>();
   }

   public TestSuiteRun getTestSuiteRun() {
      return testSuiteRun;
   }

   public List<TestRunReport> getTestRunReports() {
      return new ArrayList<>(testRunReports);
   }

   public void addTestRunReport(TestRunReport report) {
       testRunReports.add(report);
   }

   public TestRunCursor getTestRunCursor() {
      return testRunCursor;
   }


   @Override
   public boolean equals(Object o) {
      if (!(o instanceof TestSuiteSession store)) {
         return false;
      }
      return Objects.equals(testSuiteRun, store.testSuiteRun)
             && Objects.equals(testRunReports, store.testRunReports)
             && Objects.equals(testRunCursor, store.testRunCursor);
   }

   @Override
   public int hashCode() {
      return Objects.hash(testSuiteRun, testRunReports, testRunCursor);
   }
}
