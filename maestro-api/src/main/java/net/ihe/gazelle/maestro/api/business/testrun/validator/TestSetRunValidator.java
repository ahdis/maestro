package net.ihe.gazelle.maestro.api.business.testrun.validator;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.acl.validator.AccessControlListValidator;
import net.ihe.gazelle.maestro.api.business.testreport.validator.SystemUnderTestValidator;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;

public class TestSetRunValidator extends AbstractValidator<TestSuiteRun> {

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
    protected ValidatorBuilder<? extends TestSuiteRun> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        return validatorBuilderFactory.getBuilder(TestSuiteRun.class);
    }

    @Override
    protected void addRulesAndMembers(ValidatorBuilder<? extends TestSuiteRun> validatorBuilder) {
        validatorBuilder
                .addRule("testSuiteNameValid", TestSuiteRun::isTestSuiteNameValid, "Test Suite name must be set and not empty")
                .addRule("testSetRunIdValid", TestSuiteRun::isTestSetRunIdValid, "Test Set Run ID must be set and not empty")
                .addMember("testRuns", TestSuiteRun::getTestRuns, testRunValidator::getAssembledValidatorBuilder)
                .addMember("accessControlList", TestSuiteRun::getAccessControlList, accessControlListValidator::getAssembledValidatorBuilder)
                .addMember("systemUnderTest", TestSuiteRun::getSystemsUnderTest, systemUnderTestValidator::getAssembledValidatorBuilder)
        ;
    }
}
