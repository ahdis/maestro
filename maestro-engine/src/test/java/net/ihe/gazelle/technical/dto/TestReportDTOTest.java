package net.ihe.gazelle.technical.dto;

import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.interlay.adapter.BValidatorBuilderFactory;
import net.ihe.gazelle.modelmarshaller.application.StructureMapper;
import net.ihe.gazelle.modelmarshaller.interlay.factory.JsonObjectMapperBuilder;
import net.ihe.gazelle.modelmarshaller.interlay.mapper.JsonStructureMapper;
import net.ihe.gazelle.report.builder.StepRunReportBuilder;
import net.ihe.gazelle.report.builder.TestBuilder;
import net.ihe.gazelle.report.builder.TestReportBuilder;
import net.ihe.gazelle.report.builder.TestRunReportBuilder;
import net.ihe.gazelle.report.model.StepResult;
import net.ihe.gazelle.report.model.TestReport;
import net.ihe.gazelle.technical.dto.report.TestReportDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
    void test_dto_marshalling() {
        String json = structureMapper.marshall(new TestReportDTO(createTestReport()));
        TestReport testReport = structureMapper.unmarshall(json, TestReport.class);
        assertTrue(testReport.isValid());
    }

    private TestRunReportBuilder createTestRunBuilder() {
        return new TestRunReportBuilder(validatorBuilderFactory)
                .setDateTime(new Date())
                .setTest(new TestBuilder(validatorBuilderFactory).setId("testId"))
                .addStepRun(new StepRunReportBuilder(validatorBuilderFactory)
                        .setStepId("stepId")
                        .setType("validation")
                        .setResult(StepResult.PASSED)
                );
    }

    private TestReport createTestReport() {
        return new TestReportBuilder(validatorBuilderFactory)
                .setUuid()
                .setReportVersion()
                .setDateTime(new Date())
                .addTestRunReport(createTestRunBuilder())
                .build();
    }
}
