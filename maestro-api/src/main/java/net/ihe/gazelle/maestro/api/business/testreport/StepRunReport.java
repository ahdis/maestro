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

import net.ihe.gazelle.errorhandling.business.UnexpectedError;
import net.ihe.gazelle.maestro.api.business.property.Property;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * mandatory fields : stepId, type, result
 *
 */
public class StepRunReport implements Serializable {

   @Serial
   private static final long serialVersionUID = -5259327294299524545L;

   private String stepId;
   private String stepName;
   private String type;
   private Instant dateTime;
   private StepResult result;
   private List<Property> outputs;
   private List<UnexpectedError> unexpectedErrors;

   public StepRunReport() {
       dateTime = Instant.now();
       result = StepResult.UNDEFINED;
       outputs = new ArrayList<>();
       unexpectedErrors = new ArrayList<>();
   }

   public StepRunReport(String stepId, String type, StepResult result) {
      this();
      this.stepId = stepId;
      this.type = type;
      this.result = result;
   }

   public String getStepId() {
      return stepId;
   }

   public StepRunReport setStepId(String stepId) {
      this.stepId = stepId;
      return this;
   }

   public String getStepName() {
      return stepName;
   }

   public StepRunReport setStepName(String stepName) {
      this.stepName = stepName;
      return this;
   }

   public String getType() {
      return type;
   }

   public StepRunReport setType(String type) {
      this.type = type;
      return this;
   }

   public Instant getDateTime() {
      return dateTime;
   }

   public StepRunReport setDateTime(Instant dateTime) {
      this.dateTime = dateTime;
      return this;
   }

   public StepResult getResult() {
      return result;
   }

   public StepRunReport setResult(StepResult result) {
      this.result = result;
      return this;
   }

   public List<Property> getOutputs() {
      return new ArrayList<>(outputs);
   }

   public StepRunReport setOutputs(List<Property> outputs) {
      this.outputs = new ArrayList<>(outputs);
      return this;
   }

   public StepRunReport addOutput(Property output) {
      outputs.add(output);
      return this;
   }

   public StepRunReport addOutputs(List<Property> outputs) {
      this.outputs.addAll(outputs);
      return this;
   }

   public List<UnexpectedError> getUnexpectedErrors() {
      return unexpectedErrors;
   }

   public StepRunReport setUnexpectedErrors(List<UnexpectedError> unexpectedErrors) {
      this.unexpectedErrors = unexpectedErrors;
      return this;
   }

   public StepRunReport addUnexpectedError(UnexpectedError unexpectedError) {
      unexpectedErrors.add(unexpectedError);
      return this;
   }

   public boolean isStepIdValid() {
      return stepId != null && !stepId.isBlank();
   }

   public boolean isStepNameValid() {
      return stepName != null && !stepName.isBlank();
   }

   public boolean isTypeValid() {
      return type != null && !type.isBlank();
   }

   public boolean isResultValid() {
      return result != null;
   }

   public StepResult computeResult() {
      if (!unexpectedErrors.isEmpty()) {
         result = StepResult.UNDEFINED;
      }
      return result;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof StepRunReport report)) {
         return false;
      }
      return Objects.equals(stepId, report.stepId) && Objects.equals(stepName,
            report.stepName) && Objects.equals(type,
            report.type) && result == report.result && Objects.equals(outputs,
            report.outputs) && Objects.equals(unexpectedErrors, report.unexpectedErrors);
   }

   @Override
   public int hashCode() {
      return Objects.hash(stepId, stepName, type, result, outputs, unexpectedErrors);
   }
}
