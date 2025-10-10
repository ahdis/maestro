package net.ihe.gazelle.maestro.api.business.testrun.validator;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.testreport.validator.SystemUnderTestValidator;
import net.ihe.gazelle.maestro.api.business.testrun.TestSetRun;
import net.ihe.gazelle.security.business.acl.AccessControlListValidator;

public class TestSetRunValidator extends AbstractValidator<TestSetRun> {

    private final TestRunValidator testRunValidator;
    private final AccessControlListValidator accessControlListValidator;
    private final SystemUnderTestValidator systemUnderTestValidator;

    public TestSetRunValidator() {
        super();
        this.testRunValidator = new TestRunValidator();
        this.accessControlListValidator = new AccessControlListValidator();
        this.systemUnderTestValidator = new SystemUnderTestValidator();
    }

    @Override
    protected ValidatorBuilder<? extends TestSetRun> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        return validatorBuilderFactory.getBuilder(TestSetRun.class);
    }

    @Override
    protected void addRulesAndMembers(ValidatorBuilder<? extends TestSetRun> validatorBuilder) {
        validatorBuilder
                .addRule("testSuiteNameValid", TestSetRun::isTestSuiteNameValid, "Test Suite name must be set and not empty")
                .addRule("testSetRunIdValid", TestSetRun::isTestSetRunIdValid, "Test Set Run ID must be set and not empty")
                .addMember("testRuns", TestSetRun::getTestRuns, testRunValidator::getAssembledValidatorBuilder)
                .addMember("accessControlList", TestSetRun::getAccessControlList, accessControlListValidator::getAssembledValidatorBuilder)
                .addMember("systemUnderTest", TestSetRun::getSystemsUnderTest, systemUnderTestValidator::getAssembledValidatorBuilder)
        ;
    }
}
