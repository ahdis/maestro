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
import net.ihe.gazelle.modelmarshaller.technical.dto.DTO;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "stepId",
        "type",
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

    @JsonGetter(value = "stepId")
    public String getStepId() {
        return getBusinessObject().getStepId();
    }

    @JsonSetter(value = "stepId")
    public void setStepId(String stepId) {
        getBusinessObject().setStepId(stepId);
    }

    @JsonGetter(value = "type")
    public String getType() {
        return getBusinessObject().getType();
    }

    @JsonSetter(value = "type")
    public void setType(String type) {
        getBusinessObject().setType(type);
    }

    @JsonGetter(value = "result")
    public String getResult() {
        return getBusinessObject().getResult().name();
    }

    @JsonSetter(value = "result")
    public void setResult(String result) {
        getBusinessObject().setResult(StepResult.valueOf(result));
    }

    @JsonGetter(value = "outputs")
    public List<OutputDTO> outputs() {
        return getBusinessObject()
                .getOutputs()
                .stream()
                .map(OutputDTO::new)
                .toList();
    }

    @JsonSetter(value = "outputs")
    public void setOutputs(List<OutputDTO> outputs) {
        getBusinessObject().setOutputs(
                outputs.stream().map(OutputDTO::getBusinessObject).toList()
        );
    }

    @JsonGetter(value = "unexpectedErrors")
    public List<UnexpectedErrorDTO> getUnexpectedErrors() {
        return getBusinessObject()
                .getUnexpectedErrors()
                .stream()
                .map(UnexpectedErrorDTO::new)
                .toList();
    }

    @JsonSetter(value = "unexpectedErrors")
    public void setUnexpectedErrors(List<UnexpectedErrorDTO> unexpectedErrors) {
        getBusinessObject().setUnexpectedErrors(
                unexpectedErrors.stream().map(UnexpectedErrorDTO::getBusinessObject).toList()
        );
    }
}
