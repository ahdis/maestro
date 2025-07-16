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

package net.ihe.gazelle.maestro.business;

import net.ihe.gazelle.maestro.api.business.testrun.DuplicatedStepsException;
import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.Step;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.technical.ws.MissingArgumentException;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStep;
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
        testRun.setTest(new net.ihe.gazelle.maestro.api.business.testrun.Test());
        assertThrows(MissingArgumentException.class, () -> testRunValidator.validate(testRun));
    }

    @Test
    void testValidateWithRedundantStep() {
        TestRun testRun = new TestRun();
        net.ihe.gazelle.maestro.api.business.testrun.Test test = new net.ihe.gazelle.maestro.api.business.testrun.Test();
        Step step1 = new ValidationStep("test-step",
                new Property<>(ValidationStep.VALIDATION_SERVICE, "matchbox1"),
                new Property<>(ValidationStep.VALIDATION_PROFILE,"profile1"),
                new Property<>(ValidationStep.CONTENT_TO_VALIDATE, "content".getBytes()));

        Step step2 = new ValidationStep("test-step",
                new Property<>(ValidationStep.VALIDATION_SERVICE, "matchbox2"),
                new Property<>(ValidationStep.VALIDATION_PROFILE,"profile2"),
                new Property<>(ValidationStep.CONTENT_TO_VALIDATE, "content".getBytes()));

        test.addStep(step1);
        test.addStep(step2);

        testRun.setTest(test);

        assertThrows(DuplicatedStepsException.class, () -> testRunValidator.validate(testRun));
    }
}
