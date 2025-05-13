package net.ihe.gazelle.maestro.technical.dto;

import net.ihe.gazelle.lang.UnexpectedError;
import net.ihe.gazelle.maestro.technical.dto.report.ItemDTO;
import net.ihe.gazelle.maestro.technical.dto.report.SystemUnderTestDTO;
import net.ihe.gazelle.maestro.technical.dto.report.TestReportDTO;
import net.ihe.gazelle.maestro.technical.dto.report.UnexpectedErrorDTO;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import net.ihe.gazelle.testreport.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TestReportDTOTest {

    private static final TextSerDes SER_DES = new JacksonSerDes();

    @Test
    void testDTOmarshalling() {
        String json = SER_DES.serializeAsString(new TestReportDTO(createTestReport(createTestServiceBuilder())));
        TestReport testReport = SER_DES.deserialize(json, TestReportDTO.class).getDomain();
        assertTrue(testReport.isValid());

        json = SER_DES.serializeAsString(new TestReportDTO(createTestReport(null)));
        testReport = SER_DES.deserialize(json, TestReportDTO.class).getDomain();
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
        String json = SER_DES.serializeAsString(new ItemDTO(
                new Item()
                        .setName("name")
                        .setContent("test".getBytes())
        ));
        Item item =  SER_DES.deserialize(json, Item.class);
        assertTrue(item.isValid());
    }

    @Test
    void testUnexpectedErrorDTO() {
        String json = SER_DES.serializeAsString(
                new UnexpectedErrorDTO(
                        new UnexpectedError()
                                .setName("name")
                                .setMessage("message")
                )
        );
        UnexpectedError error =  SER_DES.deserialize(json, UnexpectedError.class);
        assertTrue(error.isValid());
    }

    private TestRunReportBuilder createTestRunBuilder() {
        return new TestRunReportBuilder()
                .setDateTime(new Date())
                .setTest(new TestBuilder()
                        .setId("testId")
                        .setName("testName")
                        .setDescription("description")
                        .setVersion("1.0"))
                .addOutput(new ItemBuilder()
                        .setName("name")
                        .setReference("reference"))
                .setStepRunReports(new ArrayList<>())
                .addStepRunReport(new StepRunReportBuilder()
                        .setStepId("stepId")
                        .setType("validation")
                        .setResult(StepResult.PASSED));
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
