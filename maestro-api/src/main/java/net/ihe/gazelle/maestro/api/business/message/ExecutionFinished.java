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
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;

import java.io.Serial;

@Event
public class ExecutionFinished implements Message {

    @Serial
    private static final long serialVersionUID = 1L;

    private String sessionId;
    private transient Object run;
    private String reportLocation;
    private TestReport report;

    public ExecutionFinished() {
    }

    public ExecutionFinished(String sessionId, Object run, TestReport report) {
        this.sessionId = sessionId;
        this.run = run;
        this.report = report;
    }

    public String getSessionId() {
        return sessionId;
    }

    public ExecutionFinished setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public Object getRun() {
        return run;
    }

    public ExecutionFinished setRun(Object run) {
        this.run = run;
        return this;
    }

    public String getReportLocation() {
        return reportLocation;
    }

    public ExecutionFinished setReportLocation(String reportLocation) {
        this.reportLocation = reportLocation;
        return this;
    }

    public TestReport getReport() {
        return report;
    }

    public ExecutionFinished setReport(TestReport report) {
        this.report = report;
        return this;
    }

}
