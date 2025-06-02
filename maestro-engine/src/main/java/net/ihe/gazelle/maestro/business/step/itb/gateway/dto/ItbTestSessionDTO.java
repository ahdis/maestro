package net.ihe.gazelle.maestro.business.step.itb.gateway.dto;

import java.util.Objects;

public class ItbTestSessionDTO {
    private String testSuiteIdentifier;
    private String testCaseIdentifier;
    private String testSessionIdentifier;

    public ItbTestSessionDTO() {
    }

    public ItbTestSessionDTO(String testSuiteIdentifier, String testCaseIdentifier, String testSessionIdentifier) {
        this.testSuiteIdentifier = testSuiteIdentifier;
        this.testCaseIdentifier = testCaseIdentifier;
        this.testSessionIdentifier = testSessionIdentifier;
    }

    public String getTestSuiteIdentifier() {
        return testSuiteIdentifier;
    }

    public void setTestSuiteIdentifier(String testSuiteIdentifier) {
        this.testSuiteIdentifier = testSuiteIdentifier;
    }

    public String getTestCaseIdentifier() {
        return testCaseIdentifier;
    }

    public void setTestCaseIdentifier(String testCaseIdentifier) {
        this.testCaseIdentifier = testCaseIdentifier;
    }

    public String getTestSessionIdentifier() {
        return testSessionIdentifier;
    }

    public void setTestSessionIdentifier(String testSessionIdentifier) {
        this.testSessionIdentifier = testSessionIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItbTestSessionDTO that = (ItbTestSessionDTO) o;
        return Objects.equals(testSuiteIdentifier, that.testSuiteIdentifier) &&
                Objects.equals(testCaseIdentifier, that.testCaseIdentifier) &&
                Objects.equals(testSessionIdentifier, that.testSessionIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(testSuiteIdentifier, testCaseIdentifier, testSessionIdentifier);
    }

    @Override
    public String toString() {
        return "TestSessionDTO{" +
                "testSuiteIdentifier='" + testSuiteIdentifier + '\'' +
                ", testCaseIdentifier='" + testCaseIdentifier + '\'' +
                ", testSessionIdentifier='" + testSessionIdentifier + '\'' +
                '}';
    }
}