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

package net.ihe.gazelle.maestro.business.step.itb.gateway.dto;

import java.util.Objects;

public class ItbTriggerDTO {
    private ItbSystemDTO system;
    private ItbTestSessionDTO testSession;
    private String testReport;

    public ItbTriggerDTO() {
    }

    public ItbTriggerDTO(ItbSystemDTO system, ItbTestSessionDTO testSession, String testReport) {
        this.system = system;
        this.testSession = testSession;
        this.testReport = testReport;
    }

    public ItbSystemDTO getSystem() {
        return system;
    }

    public void setSystem(ItbSystemDTO system) {
        this.system = system;
    }

    public ItbTestSessionDTO getTestSession() {
        return testSession;
    }

    public void setTestSession(ItbTestSessionDTO testSession) {
        this.testSession = testSession;
    }

    public String getTestReport() {
        return testReport;
    }

    public void setTestReport(String testReport) {
        this.testReport = testReport;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItbTriggerDTO that = (ItbTriggerDTO) o;
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
        return "ItbTriggerDTO{" +
                "system=" + system +
                ", testSession=" + testSession +
                ", testReport='" + (testReport != null ? "présent" : "absent") + '\'' +
                '}';
    }

}