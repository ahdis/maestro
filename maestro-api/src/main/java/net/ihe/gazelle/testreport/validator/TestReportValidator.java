package net.ihe.gazelle.testreport.validator;

import net.ihe.gazelle.framework.modelvalidator.domain.RootValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.StructureValidator;
import net.ihe.gazelle.framework.modelvalidator.domain.Validator;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.domain.result.ObjectResult;
import net.ihe.gazelle.testreport.TestReport;
import net.ihe.gazelle.testreport.visitor.TestReportBuilderVisitor;

public class TestReportValidator implements StructureValidator<TestReport> {

    private final Validator<TestReport> validator;

    public TestReportValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        RootValidatorBuilder<TestReport> validatorBuilder = new RootValidatorBuilder<>(TestReport.class);
        TestReportBuilderVisitor.INSTANCE.visit(validatorBuilder, validatorBuilderFactory);
        this.validator = validatorBuilder.build();
    }

    @Override
    public ObjectResult validate(TestReport object) {
        return validator.validate(object);
    }

    @Override
    public void assertValid(TestReport object) {
        validator.assertValid(object);
    }

    @Override
    public boolean isValid(TestReport object) {
        return validator.isValid(object);
    }
}
