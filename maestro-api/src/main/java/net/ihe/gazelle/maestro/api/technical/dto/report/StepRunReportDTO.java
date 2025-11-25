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

package net.ihe.gazelle.maestro.api.technical.dto.report;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.errorhandling.technical.UnexpectedErrorDTO;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.technical.dto.property.PropertyDTO;
import net.ihe.gazelle.modelmarshaller.technical.dto.DTO;
import net.ihe.gazelle.modelmarshaller.technical.dto.SubTypingDTO;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "stepId",
        "type",
        "dateTime",
        "result",
        "outputs"
})
public class StepRunReportDTO implements DTO<StepRunReport> {

   @JsonIgnore
   private final StepRunReport stepRunReport;

   public StepRunReportDTO() {
      this(new StepRunReport());
   }

   public StepRunReportDTO(StepRunReport stepRunReport) {
      this.stepRunReport = stepRunReport;
   }

   @Override
   @JsonIgnore
   public StepRunReport getBusinessObject() {
      return stepRunReport;
   }

   @JsonGetter(value = "stepName")
   public String getStepName() {
      return stepRunReport.getStepName();
   }

   @JsonSetter(value = "stepName")
   public void setStepName(String stepName) {
      stepRunReport.setStepName(stepName);
   }

   @JsonGetter(value = "type")
   public String getType() {
      return stepRunReport.getType();
   }

   @JsonSetter(value = "type")
   public void setType(String type) {
      stepRunReport.setType(type);
   }

    @JsonGetter(value = "dateTime")
    public String getDateTime() {
        return ZonedDateTime.ofInstant(getBusinessObject().getDateTime(), ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    @JsonSetter(value = "dateTime")
    public void setDateTime(String dateTime) {
        getBusinessObject().setDateTime(
                Instant.from(ZonedDateTime.parse(dateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME))
        );
    }

   @JsonGetter(value = "result")
   public String getResult() {
      return stepRunReport.getResult().name();
   }

   @JsonSetter(value = "result")
   public void setResult(String result) {
      stepRunReport.setResult(StepResult.valueOf(result));
   }

   @JsonGetter(value = "outputs")
   public List<PropertyDTO> outputs() {
      return stepRunReport.getOutputs().stream()
            .map(SubTypingDTO::fromBusinessObject)
            .map(PropertyDTO.class::cast)
            .toList();
   }

   @JsonSetter(value = "outputs")
   public void setOutputs(List<PropertyDTO> outputs) {
      stepRunReport.setOutputs(
              outputs.stream()
                      .map(PropertyDTO::getBusinessObject)
                      .toList()
      );
   }

   @JsonGetter(value = "unexpectedErrors")
   public List<UnexpectedErrorDTO> getUnexpectedErrors() {
      return stepRunReport
            .getUnexpectedErrors()
            .stream()
            .map(UnexpectedErrorDTO::new)
            .toList();
   }

   @JsonSetter(value = "unexpectedErrors")
   public void setUnexpectedErrors(List<UnexpectedErrorDTO> unexpectedErrors) {
      stepRunReport.setUnexpectedErrors(
            unexpectedErrors.stream()
                    .map(UnexpectedErrorDTO::getBusinessObject)
                    .toList()
      );
   }
}
