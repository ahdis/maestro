/*
 * Copyright 2025-2026 IHE International.
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

import net.ihe.gazelle.maestro.api.business.message.DeleteEvidence;
import net.ihe.gazelle.maestro.api.business.message.PutEvidence;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;

import java.util.ArrayList;
import java.util.List;

/**
 * TestRunSessionsStore is a structure used to store TestRun's context and StepRunReportBuilder of an executed step
 * during TestRun execution. It also contains a reference to the TestRun.
 */
public class TestRunSession {

    private String testSessionId;
    private TestRun testRun;
    private List<Property> context;
    private StepCursor stepCursor;
    private List<StepRunReport> stepReports;
    private List<PutEvidence> evidences;

    /**
     * Default constructor
     */
    public TestRunSession() {
        context = new ArrayList<>();
        stepReports = new ArrayList<>();
        evidences = new ArrayList<>();
    }

    /**
     * Creates a new {@code TestRunSession} for the specified {@link TestRun}.
     *
     * @param testRun the test run associated with this session
     */
    public TestRunSession(TestRun testRun) {
        this.testRun = testRun;
        this.context = new ArrayList<>(testRun.getInputs());
        this.stepCursor = StepCursor.from(testRun.getTest().getSteps());
        this.stepReports = new ArrayList<>();
        this.evidences = new ArrayList<>();
    }

    /**
     * Retrieves the identifier for the current test session.
     *
     * @return the test session ID as a {@code String}.
     */
    public String getTestSessionId() {
        return testSessionId;
    }

    /**
     * Sets the identifier for the current test session.
     *
     * @param testSessionId the test session ID as a {@code String}
     * @return the current {@code TestRunSession} instance for method chaining
     */
    public TestRunSession setTestSessionId(String testSessionId) {
        this.testSessionId = testSessionId;
        return this;
    }

    /**
     * Retrieves the {@link TestRun} associated with this session.
     *
     * @return the test run
     */
    public TestRun getTestRun() {
        return testRun;
    }

    /**
     * Sets the {@link TestRun} associated with this session.
     *
     * @param testRun the test run to associate with this session
     * @return the current {@code TestRunSession} instance for method chaining
     */
    public TestRunSession setTestRun(TestRun testRun) {
        this.testRun = testRun;
        return this;
    }

    /**
     * Returns a copy of the current context properties for this session.
     *
     * @return a list of context properties
     */
    public List<Property> getContext() {
        return new ArrayList<>(context);
    }

    /**
     * Sets the context properties for the current {@code TestRunSession}.
     *
     * @param context the list of {@code Property} instances representing the context to set
     * @return the current {@code TestRunSession} instance for method chaining
     */
    public TestRunSession setContext(List<Property> context) {
        if (context != null) {
            this.context = new ArrayList<>(context);
        }
        return this;
    }

    /**
     * Adds properties to the current context.
     *
     * @param properties the list of properties to add
     * @return the current {@code TestRunSession} instance for method chaining
     */
    public TestRunSession addPropertiesToContext(List<Property> properties) {
        context.addAll(properties);
        return this;
    }

    /**
     * Sets the index of the step cursor to the specified value and updates the current session state.
     *
     * @param stepCursor the index to set for the step cursor
     * @return the current {@code TestRunSession} instance for method chaining
     */
    public TestRunSession setStepCursor(int stepCursor) {
        this.stepCursor = StepCursor.from(testRun.getTest().getSteps());
        this.stepCursor.setIndex(stepCursor);
        return this;
    }

    /**
     * Indicates whether there is a next step to execute.
     *
     * @return {@code true} if a next step exists, {@code false} otherwise
     */
    public boolean hasNextStep() {
        return stepCursor.hasNext();
    }

    /**
     * Advances to the next step and returns it.
     *
     * @return the next {@link Step} to execute
     */
    public Step nextStep() {
        return stepCursor.next();
    }

    /**
     * Returns the current step that is being executed.
     *
     * @return the current {@link Step}
     */
    public Step currentStep() {
        return stepCursor.current();
    }

    /**
     * Returns the index of the current step.
     *
     * @return the index of the current step
     */
    public int currentStepIndex() {
        return stepCursor.currentIndex();
    }

    /**
     * Returns a copy of the list of executed step reports.
     *
     * @return a list of {@link StepRunReport} instances
     */
    public List<StepRunReport> getStepReports() {
        return new ArrayList<>(stepReports);
    }

    /**
     * Sets the list of executed step reports for this test run session.
     *
     * @param stepReports the list of {@link StepRunReport} instances to set
     * @return the current {@code TestRunSession} instance for method chaining
     */
    public TestRunSession setStepReports(List<StepRunReport> stepReports) {
        if (stepReports != null) {
            this.stepReports = new ArrayList<>(stepReports);
        }
        return this;
    }

    /**
     * Adds a {@link StepRunReport} to the list of executed step reports.
     *
     * @param stepReportBuilder the step report to add
     */
    public void addStepRunReport(StepRunReport stepReportBuilder) {
        stepReports.add(stepReportBuilder);
    }

    /**
     * Retrieves the list of evidence items associated with the test run session.
     *
     * @return a list of {@link PutEvidence} instances representing the evidence.
     */
    public List<PutEvidence> getEvidences() {
        return new ArrayList<>(evidences);
    }

    /**
     * Sets the list of evidence items associated with the test run session.
     *
     * @param evidences the list of {@code PutEvidence} instances to set
     * @return the current {@code TestRunSession} instance for method chaining
     */
    public TestRunSession setEvidences(List<PutEvidence> evidences) {
        if (evidences != null) {
            this.evidences = new ArrayList<>(evidences);
        }
        return this;
    }

    /**
     * Adds a {@link PutEvidence} instance to the list of evidence items
     * associated with this test run session.
     *
     * @param putEvidence the {@code PutEvidence} instance to add
     * @return the current {@code TestRunSession} instance for method chaining
     */
    public TestRunSession addEvidence(PutEvidence putEvidence) {
        evidences.add(putEvidence);
        return this;
    }

    /**
     * Removes a specific piece of evidence from the list of evidences based on the given criteria.
     *
     * @param deleteEvidence the {@code DeleteEvidence} instance containing the run ID, step name, and
     *                       evidence name to match and remove from the list of evidences
     */
    public void removeEvidence(DeleteEvidence deleteEvidence) {
        evidences.removeIf(
                putEvidence -> putEvidence.getRunId().equals(deleteEvidence.getRunId())
                && putEvidence.getStepName().equals(deleteEvidence.getStepName())
                && putEvidence.getContent().getName().equals(deleteEvidence.getEvidenceName())
        );
    }
}
