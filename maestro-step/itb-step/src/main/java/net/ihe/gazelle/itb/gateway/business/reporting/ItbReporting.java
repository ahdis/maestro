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

package net.ihe.gazelle.itb.gateway.business.reporting;

import java.util.Objects;

public class ItbReporting {

    private ItbSystem system;
    private ItbTestSession testSession;
    private String testReport;
    private ItbResult result;
    private byte[] pdfReport;
    private String logs;

    public ItbReporting() {
    }

    public ItbReporting(ItbSystem system, ItbTestSession testSession, String testReport) {
        this.system = system;
        this.testSession = testSession;
        this.testReport = testReport;
    }

    public ItbSystem getSystem() {
        return system;
    }

    public void setSystem(ItbSystem system) {
        this.system = system;
    }

    public ItbTestSession getTestSession() {
        return testSession;
    }

    public void setTestSession(ItbTestSession testSession) {
        this.testSession = testSession;
    }

    public String getTestReport() {
        return testReport;
    }

    public void setTestReport(String testReport) {
        this.testReport = testReport;
    }

   public ItbResult getResult() {
      return result;
   }

   public ItbReporting setResult(ItbResult result) {
      this.result = result;
      return this;
   }

   public byte[] getPdfReport() {
      return pdfReport;
   }

   public ItbReporting setPdfReport(byte[] pdfReport) {
      this.pdfReport = pdfReport;
      return this;
   }

   public String getLogs() {
      return logs;
   }

   public ItbReporting setLogs(String logs) {
      this.logs = logs;
      return this;
   }

   @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItbReporting that = (ItbReporting) o;
        return Objects.equals(system, that.system) &&
                Objects.equals(testSession, that.testSession) &&
                Objects.equals(testReport, that.testReport);
    }

    @Override
    public int hashCode() {
        return Objects.hash(system, testSession, testReport);
    }

    @Override
    public String toString() {
        return "ItbReporting{" +
                "system=" + system +
                ", testSession=" + testSession +
                ", testReport='" + (testReport != null ? "présent" : "absent") + '\'' +
                '}';
    }

}