package net.ihe.gazelle.maestro.business.engine;

import net.ihe.gazelle.maestro.DuplicatedStepsException;
import net.ihe.gazelle.maestro.Property;
import net.ihe.gazelle.maestro.Step;
import net.ihe.gazelle.maestro.TestRun;
import net.ihe.gazelle.maestro.business.step.validation.ValidationStep;
import net.ihe.gazelle.maestro.technical.ws.MissingArgumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TestRunValidatorTest {
    TestRunValidator testRunValidator;

    @BeforeEach
    public void setUp() {
        testRunValidator = new TestRunValidator();
    }

    @Test
    void testValidateNullTestRun() {
        assertThrows(MissingArgumentException.class, () -> testRunValidator.validate(null));
    }

    @Test
    void testValidateEmptyTestRun() {
        TestRun testRun = new TestRun();
        assertThrows(MissingArgumentException.class, () -> testRunValidator.validate(testRun));
    }

    @Test
    void testValidateEmptyTest() {
        TestRun testRun = new TestRun();
        testRun.setTest(new net.ihe.gazelle.maestro.Test());
        assertThrows(MissingArgumentException.class, () -> testRunValidator.validate(testRun));
    }

    @Test
    void testValidateWithRedundantStep() {
        TestRun testRun = new TestRun();
        net.ihe.gazelle.maestro.Test test = new net.ihe.gazelle.maestro.Test();
        Step step1 = new ValidationStep("test-step",
                new Property<>(ValidationStep.VALIDATION_SERVICE, "saucisse"),
                new Property<>(ValidationStep.VALIDATION_PROFILE,"saucisse"),
                new Property<>(ValidationStep.CONTENT_TO_VALIDATE, "saucisse".getBytes()));

        Step step2 = new ValidationStep("test-step",
                new Property<>(ValidationStep.VALIDATION_SERVICE, "morteau"),
                new Property<>(ValidationStep.VALIDATION_PROFILE,"morteau"),
                new Property<>(ValidationStep.CONTENT_TO_VALIDATE, "morteau".getBytes()));

        test.addStep(step1);
        test.addStep(step2);

        testRun.setTest(test);

        assertThrows(DuplicatedStepsException.class, () -> testRunValidator.validate(testRun));
    }
}
