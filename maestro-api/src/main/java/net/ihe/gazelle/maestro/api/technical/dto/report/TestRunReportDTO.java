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
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.testreport.Result;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReport;
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
      "runId",
      "dateTime",
      "result",
      "test",
      "inputs",
      "outputs",
      "stepRunReports",
      "urlToTestRun",
      "unexpectedErrors"
})
public class TestRunReportDTO implements DTO<TestRunReport> {

   public static final String ISO_8601_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSX";

   @JsonIgnore
   private final TestRunReport testRunReport;

   public TestRunReportDTO() {
      this(new TestRunReport());
   }

   public TestRunReportDTO(TestRunReport testRunReport) {
      this.testRunReport = testRunReport;
   }

   @Override
   @JsonIgnore
   public TestRunReport getBusinessObject() {
      return testRunReport;
   }

   @JsonGetter(value = "runId")
   public String getRunId() {
      return testRunReport.getRunId();
   }

   @JsonSetter(value = "runId")
   public void setRunId(String runId) {
      testRunReport.setRunId(runId);
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
   public String result() {
      return testRunReport.getResult().name();
   }

   @JsonSetter(value = "result")
   public void setResult(String result) {
      testRunReport.setResult(Result.valueOf(result));
   }

   @JsonGetter(value = "test")
   public TestDTO test() {
      return new TestDTO(testRunReport.getTest());
   }

   @JsonSetter(value = "test")
   public void setTest(TestDTO testDTO) {
      testRunReport.setTest(testDTO.getBusinessObject());
   }

   @JsonGetter(value = "inputs")
   public List<PropertyDTO> getInputs() {
      return testRunReport.getInputs().stream()
            .map(SubTypingDTO::fromBusinessObject)
            .map(PropertyDTO.class::cast)
            .toList();
   }

   @JsonSetter(value = "inputs")
   public void setInputs(List<PropertyDTO> inputs) {
      testRunReport.setInputs(
            inputs != null ?
                  inputs.stream()
                        .map(PropertyDTO::getBusinessObject)
                        .map(Property.class::cast)
                        .toList() :
                  null
      );
   }

   @JsonGetter(value = "outputs")
   public List<PropertyDTO> getOutputs() {
      return testRunReport.getOutputs().stream()
            .map(SubTypingDTO::fromBusinessObject)
            .map(PropertyDTO.class::cast)
            .toList();
   }

   @JsonSetter(value = "outputs")
   public void setOutputs(List<PropertyDTO> outputs) {
      testRunReport.setOutputs(
            outputs != null ?
                  outputs.stream()
                        .map(PropertyDTO::getBusinessObject)
                        .map(Property.class::cast)
                        .toList() :
                  null
      );
   }

   @JsonGetter(value = "stepRunReports")
   public List<StepRunReportDTO> getStepRunReports() {
      return testRunReport
            .getStepRunReports()
            .stream()
            .map(StepRunReportDTO::new)
            .toList();
   }

   @JsonSetter(value = "stepRunReports")
   public void setStepRunReports(List<StepRunReportDTO> stepRunReports) {
      testRunReport.setStepRunReports(
            stepRunReports.stream().map(StepRunReportDTO::getBusinessObject).toList()
      );
   }

   @JsonGetter(value = "urlToTestRun")
   public String getUrlToTestRun() {
      return testRunReport.getUrlToTestRun();
   }

   @JsonSetter(value = "urlToTestRun")
   public void setUrlToTestRun(String urlToTestRun) {
      testRunReport.setUrlToTestRun(urlToTestRun);
   }

   @JsonGetter(value = "unexpectedErrors")
   public List<UnexpectedErrorDTO> getUnexpectedErrors() {
      return testRunReport
            .getUnexpectedErrors()
            .stream()
            .map(UnexpectedErrorDTO::new)
            .toList();
   }

   @JsonSetter(value = "unexpectedErrors")
   public void setUnexpectedErrors(List<UnexpectedErrorDTO> unexpectedErrors) {
      testRunReport.setUnexpectedErrors(
            unexpectedErrors.stream().map(UnexpectedErrorDTO::getBusinessObject).toList()
      );
   }
}
