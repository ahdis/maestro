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

package net.ihe.gazelle.maestro.api.technical.dto;

import net.ihe.gazelle.lang.UnexpectedError;
import net.ihe.gazelle.maestro.api.business.testreport.*;
import net.ihe.gazelle.maestro.api.technical.dto.report.OutputDTO;
import net.ihe.gazelle.maestro.api.technical.dto.report.SystemUnderTestDTO;
import net.ihe.gazelle.maestro.api.technical.dto.report.TestReportDTO;
import net.ihe.gazelle.maestro.api.technical.dto.report.UnexpectedErrorDTO;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TestReportDTOTest {

    private static final TextSerDes SER_DES = new JacksonSerDes();

    @Test
    void testDTOmarshalling() {
        String json = SER_DES.serializeAsString(new TestReportDTO(createTestReport(createTestServiceBuilder())));
        TestReport testReport = SER_DES.deserialize(json, TestReportDTO.class).getBusinessObject();
        assertTrue(testReport.isValid());

        json = SER_DES.serializeAsString(new TestReportDTO(createTestReport(null)));
        testReport = SER_DES.deserialize(json, TestReportDTO.class).getBusinessObject();
        assertTrue(testReport.isValid());
    }

    @Test
    void testSystemUnderTestDTO() {
        String json = SER_DES.serializeAsString(new SystemUnderTestDTO(
                new SystemUnderTest()
                        .setSystemIdentification(new EntityIdentification()
                                .setName("name")
                                .setVersion("1.0")))
        );
        SystemUnderTest systemUnderTest =  SER_DES.deserialize(json, SystemUnderTest.class);
        assertTrue(systemUnderTest.isValid());
    }

    @Test
    void testItemDTO() {
        String json = SER_DES.serializeAsString(new OutputDTO(
                new Output()
                        .setName("name")
                        .setContent("test".getBytes())
        ));
        Output output =  SER_DES.deserialize(json, Output.class);
        assertTrue(output.isValid());
    }

    @Test
    void testUnexpectedErrorDTO() {
        String json = SER_DES.serializeAsString(
                new UnexpectedErrorDTO(
                        new UnexpectedError()
                                .setName("name")
                                .setMessage("message")
                                .setCause(new UnexpectedError()
                                        .setName("name")
                                        .setMessage("message"))
                )
        );
        UnexpectedError error =  SER_DES.deserialize(json, UnexpectedError.class);
        assertTrue(error.isValid());
    }

    private TestRunReportBuilder createTestRunBuilder() {
        return new TestRunReportBuilder()
                .setRunId("testRunId")
                .setUrlToTestRun("url")
                .setDateTime(new Date())
                .setTest(new TestBuilder()
                        .setId("testId")
                        .setName("testName")
                        .setDescription("description")
                        .setVersion("1.0"))
                .addOutput(new OutputBuilder()
                        .setName("name")
                        .setReference("reference")
                        .setFormat("format")
                )
                .setStepRunReports(new ArrayList<>())
                .addStepRunReport(new StepRunReportBuilder()
                        .setStepId("stepId")
                        .setType("validation")
                        .setResult(StepResult.PASSED))
                .setInputs(List.of(
                        new InputBuilder()
                                .setType("type")
                                .setName("item")
                                .setReference("reference")
                ))
                .setUnexpectedErrors(List.of(
                        new UnexpectedErrorBuilder()
                                .setName("unexpectedError")
                                .setMessage("message")
                ));
    }

    private TestServiceBuilder createTestServiceBuilder() {
        return new TestServiceBuilder()
                .setServiceIdentification(new EntityIdentificationBuilder()
                        .setName("service")
                        .setVersion("1.0"))
                .setDisclaimer("disclaimer");
    }

    private TestReport createTestReport(TestServiceBuilder testServiceBuilder) {
        return new TestReportBuilder()
                .setReportVersion()
                .setDateTime(new Date())
                .setTestService(testServiceBuilder)
                .addSystemUnderTest(new SystemUnderTestBuilder()
                        .setSystemIdentification(new EntityIdentificationBuilder()
                                .setName("system")
                                .setVersion("1.0"))
                        .addMacAddress("mac")
                        .addHostName("host")
                        .addIpAddress("ip"))
                .addTestRunReport(createTestRunBuilder())
                .build();
    }
}
