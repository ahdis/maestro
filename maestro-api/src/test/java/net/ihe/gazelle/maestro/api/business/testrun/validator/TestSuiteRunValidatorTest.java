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

package net.ihe.gazelle.maestro.api.business.testrun.validator;

import net.ihe.gazelle.framework.modelvalidator.business.ObjectResult;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.test.TestReference;
import net.ihe.gazelle.maestro.api.business.test.TestSuite;
import net.ihe.gazelle.maestro.api.business.testreport.EntityIdentification;
import net.ihe.gazelle.maestro.api.business.testreport.SystemUnderTest;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRunValidator;
import net.ihe.gazelle.security.business.acl.AccessControlList;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestSuiteRunValidatorTest {

    private final TestSuiteRunValidator validator = new TestSuiteRunValidator();

    @Test
    void shouldValidateTestSuiteRunWhenConsistentWithSuite() {
        TestSuiteRun testSuiteRun = buildValidTestSuiteRun();

        ObjectResult result = validator.validate(testSuiteRun);

        assertTrue(result.isValid(), result::toString);
        validator.assertValid(testSuiteRun);
    }

    @Test
    void shouldRejectTestSuiteRunWithoutTestSuite() {
        TestSuiteRun testSuiteRun = buildValidTestSuiteRun()
                .setTestSuite(null);

        ObjectResult result = validator.validate(testSuiteRun);

        assertFalse(result.isValid());
    }

    private TestSuiteRun buildValidTestSuiteRun() {
        Property input = new StringProperty("suite-input", "value");
        SystemUnderTest systemUnderTest = new SystemUnderTest()
                .setSystemIdentification(new EntityIdentification("sut-1"));

        net.ihe.gazelle.maestro.api.business.test.Test test = buildValidTest();

        TestSuite testSuite = new TestSuite()
                .setId("suite-1")
                .setName("Suite")
                .setTestReferences(List.of(new TestReference().setTestId(test.getId())));

        return new TestSuiteRun()
                .setTestSuite(testSuite)
                .setTests(List.of(test))
                .setInputs(List.of(input))
                .setSystemsUnderTest(List.of(systemUnderTest))
                .setAccessControlList(new AccessControlList().setPublic(true));
    }

    private net.ihe.gazelle.maestro.api.business.test.Test buildValidTest() {
        Step step = new Step()
                .setId("step-1")
                .setName("Validate something")
                .setType("validation");

        return new net.ihe.gazelle.maestro.api.business.test.Test()
                .setId("test-1")
                .setName("Sample test")
                .setSteps(List.of(step));
    }
}
