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

package net.ihe.gazelle.maestro.itb.step.technical.gateway.dto;

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