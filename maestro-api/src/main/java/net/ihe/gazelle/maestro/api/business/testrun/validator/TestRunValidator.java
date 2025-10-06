package net.ihe.gazelle.maestro.api.business.testrun.validator;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;

public class TestRunValidator extends AbstractValidator<TestRun> {

    private final PropertyValidator propertyValidator;
    private final TestValidator testValidator;

    public TestRunValidator() {
        super();
        this.propertyValidator = new PropertyValidator();
        this.testValidator = new TestValidator();
    }

    @Override
    protected ValidatorBuilder<? extends TestRun> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        return validatorBuilderFactory.getBuilder(TestRun.class);
    }

    @Override
    protected void addRulesAndMembers(ValidatorBuilder<? extends TestRun> validatorBuilder) {
            validatorBuilder
                    .addRule("testValid", TestRun::isTestValid, "Test must be set with at least a single step")
                    .addRule("inputValid", TestRun::areInputsValid, "If inputs are set, they must be not empty")
                    .addMember("inputs", TestRun::getInputs, propertyValidator::getAssembledValidatorBuilder)
                    .addMember("test", TestRun::getTest, testValidator::getAssembledValidatorBuilder)
            ;
    }
}
