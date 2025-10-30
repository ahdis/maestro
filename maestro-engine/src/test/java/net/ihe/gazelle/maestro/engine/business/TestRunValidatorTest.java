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

package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestRunValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TestRunValidatorTest {
    TestRunValidator testRunValidator;

    @BeforeEach
    void setUp() {
        testRunValidator = new TestRunValidator();
    }

    @Test
    void testValidateNullTestRun() {
        assertThrows(NullPointerException.class, () -> testRunValidator.validate((TestRun) null));
    }

    @Test
    void testValidateEmptyTestRun() {
        TestRun testRun = new TestRun();
        Throwable throwable = assertThrows(IllegalArgumentException.class, () -> testRunValidator.assertValid(testRun));
        assertThat(throwable.getMessage(), containsString("TestRun [testValid] Test must be set with at least a single step => invalid"));
    }

    @Test
    void testValidateEmptyTest() {
        TestRun testRun = new TestRun();
        testRun.setTest(new net.ihe.gazelle.maestro.api.business.test.Test());
        Throwable throwable = assertThrows(IllegalArgumentException.class, () -> testRunValidator.assertValid(testRun));
        assertThat(throwable.getMessage(), containsString("TestRun.test [stepsValid] Test must contain at least one step => invalid"));
    }

    @Test
    void testValidateWithRedundantStep() {
        TestRun testRun = new TestRun();
        net.ihe.gazelle.maestro.api.business.test.Test test = new net.ihe.gazelle.maestro.api.business.test.Test();
        Step step1 = mock(Step.class);
        when(step1.getType()).thenReturn("VALIDATION");
        when(step1.getProperties())
                .thenReturn(List.of(
                        new Property<>("validationService", "matchbox1"),
                        new Property<>("validationProfile", "profile1"),
                        new Property<>("contentToValidate", "content".getBytes())
                ));
        Step step2 = mock(Step.class);
        when(step2.getType()).thenReturn("VALIDATION");
        when(step2.getProperties())
                .thenReturn(List.of(
                        new Property<>("validationService", "matchbox1"),
                        new Property<>("validationProfile", "profile1"),
                        new Property<>("contentToValidate", "content".getBytes())
                ));

        test.addStep(step1);
        test.addStep(step2);

        testRun.setTest(test);

        Throwable throwable = assertThrows(IllegalArgumentException.class, () -> testRunValidator.assertValid(testRun));
        assertThat(throwable.getMessage(), containsString("TestRun.test [stepsUniques] Steps must be unique => invalid"));
    }
}
