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

package net.ihe.gazelle.maestro.api.business.testreport;

import net.ihe.gazelle.errorhandling.business.UnexpectedErrorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.testreport.validator.TestRunReportValidator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TestRunReportBuilder extends AbstractBuilder<TestRunReport> {

   private String runId;
   private Instant dateTime;
   private TestBuilder test;
   private List<Property> inputs;
   private List<Property> outputs;
   private List<StepRunReportBuilder> stepRunReportBuilders;
   private List<StepRunReport> stepRunReports;
   private String urlToTestRun;
   private List<UnexpectedErrorBuilder> unexpectedErrors;

   public TestRunReportBuilder() {
      super();
      dateTime = Instant.now();
      inputs = new ArrayList<>();
      outputs = new ArrayList<>();
      stepRunReportBuilders = new ArrayList<>();
      stepRunReports = new ArrayList<>();
      unexpectedErrors = new ArrayList<>();
   }

   public TestRunReportBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
      super(validatorBuilderFactory);
      dateTime = Instant.now();
      inputs = new ArrayList<>();
      outputs = new ArrayList<>();
      stepRunReportBuilders = new ArrayList<>();
      stepRunReports = new ArrayList<>();
      unexpectedErrors = new ArrayList<>();
   }

   public TestRunReportBuilder setRunId(String runId) {
      this.runId = runId;
      return this;
   }

   public TestRunReportBuilder setDateTime(Instant dateTime) {
      this.dateTime = dateTime;
      return this;
   }

   public TestRunReportBuilder setTest(TestBuilder test) {
      this.test = test;
      return this;
   }

   public TestRunReportBuilder setInputs(List<Property> inputs) {
      this.inputs = inputs != null ? new ArrayList<>(inputs) : new ArrayList<>();
      return this;
   }

   public TestRunReportBuilder addInput(Property input) {
      inputs.add(input);
      return this;
   }

   public TestRunReportBuilder setOutputs(List<Property> outputs) {
      this.outputs = outputs != null ? new ArrayList<>(outputs) : new ArrayList<>();
      return this;
   }

   public TestRunReportBuilder addOutput(Property output) {
      outputs.add(output);
      return this;
   }

   public TestRunReportBuilder addStepRunReport(StepRunReportBuilder stepRunReport) {
      this.stepRunReportBuilders.add(stepRunReport);
      return this;
   }

   public TestRunReportBuilder addStepRunReport(StepRunReport stepRunReports) {
      this.stepRunReports.add(stepRunReports);
      return this;
   }

   public TestRunReportBuilder addStepRunReportBuilders(List<StepRunReportBuilder> stepRuns) {
      this.stepRunReportBuilders.addAll(stepRuns);
      return this;
   }

   public TestRunReportBuilder addStepRunReports(List<StepRunReport> stepRunReports) {
      this.stepRunReports.addAll(stepRunReports);
      return this;
   }

   public TestRunReportBuilder setUrlToTestRun(String urlToTestRun) {
      this.urlToTestRun = urlToTestRun;
      return this;
   }

   public TestRunReportBuilder setUnexpectedErrors(List<UnexpectedErrorBuilder> unexpectedErrors) {
      this.unexpectedErrors = new ArrayList<>(unexpectedErrors);
      return this;
   }

   public TestRunReportBuilder addUnexpectedError(UnexpectedErrorBuilder unexpectedError) {
      this.unexpectedErrors.add(unexpectedError);
      return this;
   }

   @Override
   protected AbstractValidator<TestRunReport> instantiateValidator(ValidatorBuilderFactory validatorBuilderFactory) {
      return new TestRunReportValidator(validatorBuilderFactory);
   }

   @Override
   protected TestRunReport instantiate() {
      return new TestRunReport();
   }

   @Override
   protected void make(TestRunReport testRunReport) {
      testRunReport.setRunId(runId)
            .setDateTime(dateTime)
            .setTest(AbstractBuilder.staticBuildWithoutValidation(test))
            .setInputs(inputs)
            .setOutputs(outputs)
            .setStepRunReports(
                  !stepRunReportBuilders.isEmpty() ? stepRunReportBuilders.stream()
                        .map(AbstractBuilder::staticBuildWithoutValidation)
                        .toList() : new ArrayList<>()
            )
            .addStepRunReports(
                  !stepRunReports.isEmpty() ? stepRunReports : new ArrayList<>()
            )
            .setUrlToTestRun(urlToTestRun)
            .setUnexpectedErrors(
                  !unexpectedErrors.isEmpty() ? unexpectedErrors.stream()
                        .map(AbstractBuilder::staticBuildWithoutValidation)
                        .toList() : new ArrayList<>()
            ).computeResult();
   }
}
