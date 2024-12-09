package net.ihe.gazelle.maestro.technical.dto;

import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.interlay.adapter.BValidatorBuilderFactory;
import net.ihe.gazelle.maestro.technical.dto.report.ItemDTO;
import net.ihe.gazelle.maestro.technical.dto.report.SystemUnderTestDTO;
import net.ihe.gazelle.maestro.technical.dto.report.UnexpectedErrorDTO;
import net.ihe.gazelle.modelmarshaller.application.StructureMapper;
import net.ihe.gazelle.modelmarshaller.interlay.factory.JsonObjectMapperBuilder;
import net.ihe.gazelle.modelmarshaller.interlay.mapper.JsonStructureMapper;
import net.ihe.gazelle.testreport.*;
import net.ihe.gazelle.maestro.technical.dto.report.TestReportDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TestReportDTOTest {

    private static StructureMapper structureMapper;
    private static ValidatorBuilderFactory validatorBuilderFactory;

    @BeforeAll
    public static void init(){
        structureMapper = new JsonStructureMapper(new JsonObjectMapperBuilder().build());
        validatorBuilderFactory = new BValidatorBuilderFactory();
    }

    @Test
    void testDTOmarshalling() {
        String json = structureMapper.marshall(new TestReportDTO(createTestReport(createTestServiceBuilder())));
        TestReport testReport = structureMapper.unmarshall(json, TestReportDTO.class).getDomain();
        assertTrue(testReport.isValid());

        json = structureMapper.marshall(new TestReportDTO(createTestReport(null)));
        testReport = structureMapper.unmarshall(json, TestReportDTO.class).getDomain();
        assertTrue(testReport.isValid());
    }

    @Test
    void testSystemUnderTestDTO() {
        String json = structureMapper.marshall(new SystemUnderTestDTO(
                new SystemUnderTest()
                        .setSystemIdentification(new EntityIdentification()
                                .setName("name")
                                .setVersion("1.0")))
        );
        SystemUnderTest systemUnderTest = structureMapper.unmarshall(json, SystemUnderTest.class);
        assertTrue(systemUnderTest.isValid());
    }

    @Test
    void testItemDTO() {
        String json = structureMapper.marshall(new ItemDTO(
                new Item()
                        .setName("name")
                        .setContent("test".getBytes())
        ));
        Item item = structureMapper.unmarshall(json, Item.class);
        assertTrue(item.isValid());
    }

    @Test
    void testUnexpectedErrorDTO() {
        String json = structureMapper.marshall(
                new UnexpectedErrorDTO(
                        new UnexpectedError()
                                .setName("name")
                                .setMessage("message")
                )
        );
        UnexpectedError error = structureMapper.unmarshall(json, UnexpectedError.class);
        assertTrue(error.isValid());
    }

    private TestRunReportBuilder createTestRunBuilder() {
        return new TestRunReportBuilder(validatorBuilderFactory)
                .setDateTime(new Date())
                .setTest(new TestBuilder(validatorBuilderFactory)
                        .setId("testId")
                        .setName("testName")
                        .setDescription("description")
                        .setVersion("1.0"))
                .addOutput(new ItemBuilder(validatorBuilderFactory)
                        .setName("name")
                        .setReference("reference"))
                .setStepRunReports(new ArrayList<>())
                .addStepRunReport(new StepRunReportBuilder(validatorBuilderFactory)
                        .setStepId("stepId")
                        .setType("validation")
                        .setResult(StepResult.PASSED));
    }

    private TestServiceBuilder createTestServiceBuilder() {
        return new TestServiceBuilder(validatorBuilderFactory)
                .setServiceIdentification(new EntityIdentificationBuilder(validatorBuilderFactory)
                        .setName("service")
                        .setVersion("1.0"))
                .setDisclaimer("disclaimer");
    }

    private TestReport createTestReport(TestServiceBuilder testServiceBuilder) {
        return new TestReportBuilder(validatorBuilderFactory)
                .generateUuid()
                .setReportVersion()
                .setDateTime(new Date())
                .setTestService(testServiceBuilder)
                .addSystemUnderTest(new SystemUnderTestBuilder(validatorBuilderFactory)
                        .setSystemIdentification(new EntityIdentificationBuilder(validatorBuilderFactory)
                                .setName("system")
                                .setVersion("1.0"))
                        .addMacAddress("mac")
                        .addHostName("host")
                        .addIpAddress("ip"))
                .addTestRunReport(createTestRunBuilder())
                .build();
    }
}
