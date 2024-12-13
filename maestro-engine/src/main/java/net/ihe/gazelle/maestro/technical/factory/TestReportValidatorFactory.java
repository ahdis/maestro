package net.ihe.gazelle.maestro.technical.factory;

import net.ihe.gazelle.framework.modelvalidator.application.StructureValidatorFactory;
import net.ihe.gazelle.framework.modelvalidator.domain.StructureValidator;
import net.ihe.gazelle.framework.modelvalidator.interlay.adapter.BValidatorBuilderFactory;
import net.ihe.gazelle.testreport.TestReport;
import net.ihe.gazelle.testreport.validator.TestReportValidator;

public class TestReportValidatorFactory implements StructureValidatorFactory<TestReport> {

    @Override
    public StructureValidator<TestReport> createStructureValidator() {
        return new TestReportValidator(new BValidatorBuilderFactory());
    }

    @Override
    public Class<TestReport> getType() {
        return TestReport.class;
    }

    @Override
    public String getName() {
        return "testReport";
    }
}
