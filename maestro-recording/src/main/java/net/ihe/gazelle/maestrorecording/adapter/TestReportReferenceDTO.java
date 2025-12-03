package net.ihe.gazelle.maestrorecording.adapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.ihe.gazelle.maestro.api.business.testreport.Result;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
      "testReportId",
      "result",
      "testSuiteName"
})
class TestReportReferenceDTO {

    private String testReportId;
    private Result result;
    private String testSuiteName;

    @JsonProperty("testReportId")
    String getTestReportId() {
        return testReportId;
    }

    void setTestReportId(String testReportId) {
        this.testReportId = testReportId;
    }

    @JsonProperty("result")
    Result getResult() {
        return result;
    }

    void setResult(Result result) {
        this.result = result;
    }

    @JsonProperty("testSuiteName")
    String getTestSuiteName() {
        return testSuiteName;
    }

    void setTestSuiteName(String testSuiteName) {
        this.testSuiteName = testSuiteName;
    }
}
