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
import net.ihe.gazelle.maestro.api.business.testreport.validator.StepRunReportValidator;

import java.util.ArrayList;
import java.util.List;

public class StepRunReportBuilder extends AbstractBuilder<StepRunReport> {

   private String stepId;
   private String stepName;
   private String type;
   private StepResult result;
   private List<Property> outputs;
   private List<UnexpectedErrorBuilder> unexpectedErrors;

   public StepRunReportBuilder() {
      super();
      outputs = new ArrayList<>();
      unexpectedErrors = new ArrayList<>();
   }

   public StepRunReportBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
      super(validatorBuilderFactory);
      outputs = new ArrayList<>();
      unexpectedErrors = new ArrayList<>();
   }

   public StepRunReportBuilder setStepId(String stepId) {
      this.stepId = stepId;
      return this;
   }

   public StepRunReportBuilder setStepName(String stepName) {
      this.stepName = stepName;
      return this;
   }

   public StepRunReportBuilder setType(String type) {
      this.type = type;
      return this;
   }

   public StepRunReportBuilder setResult(StepResult result) {
      this.result = result;
      return this;
   }

   public StepRunReportBuilder setOutputs(List<Property> outputs) {
      this.outputs = new ArrayList<>(outputs);
      return this;
   }

   public StepRunReportBuilder addOutput(Property output) {
      this.outputs.add(output);
      return this;
   }

   public StepRunReportBuilder addOutputs(List<Property> outputs) {
      this.outputs.addAll(outputs);
      return this;
   }

   public List<Property> getOutputs() {
      return outputs;
   }

   public StepRunReportBuilder setUnexpectedErrors(List<UnexpectedErrorBuilder> unexpectedErrors) {
      this.unexpectedErrors = new ArrayList<>(unexpectedErrors);
      return this;
   }

   public StepRunReportBuilder addUnexpectedError(UnexpectedErrorBuilder unexpectedError) {
      unexpectedErrors.add(unexpectedError);
      return this;
   }

   @Override
   protected AbstractValidator<StepRunReport> instantiateValidator(ValidatorBuilderFactory validatorBuilderFactory) {
      return new StepRunReportValidator(validatorBuilderFactory);
   }

   @Override
   protected StepRunReport instantiate() {
      return new StepRunReport();
   }

   @Override
   protected void make(StepRunReport stepRunReport) {
      stepRunReport
            .setStepId(stepId)
            .setStepName(stepName)
            .setType(type)
            .setResult(result)
            .setOutputs(outputs)
            .setUnexpectedErrors(
                  !unexpectedErrors.isEmpty() ? unexpectedErrors.stream()
                        .map(AbstractBuilder::staticBuildWithoutValidation)
                        .toList() : new ArrayList<>()
            ).computeResult();
   }
}
