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
import net.ihe.gazelle.maestro.api.business.testreport.EntityIdentification;
import net.ihe.gazelle.maestro.api.business.testreport.SystemUnderTest;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestRunValidator;
import net.ihe.gazelle.security.business.acl.AccessControlList;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestRunValidatorTest {

    private final TestRunValidator validator = new TestRunValidator();

    @Test
    void shouldValidateTestRunWhenMandatoryMembersPresent() {
        TestRun testRun = buildValidTestRun();

        ObjectResult result = validator.validate(testRun);

        assertTrue(result.isValid(), result::toString);
        validator.assertValid(testRun);
    }

    @Test
    void shouldRejectTestRunWithoutTest() {
        TestRun testRun = buildValidTestRun().setTest(null);

        ObjectResult result = validator.validate(testRun);

        assertFalse(result.isValid());
    }

    private TestRun buildValidTestRun() {
        Property input = new StringProperty("input-key", "value");
        SystemUnderTest systemUnderTest = new SystemUnderTest()
                .setSystemIdentification(new EntityIdentification("sut-1"));

        return new TestRun()
                .setTest(buildValidTest())
                .setInputs(List.of(input))
                .setSystemsUnderTest(List.of(systemUnderTest))
                .setAccessControlList(new AccessControlList().setPublic(true));
    }

    private net.ihe.gazelle.maestro.api.business.test.Test buildValidTest() {
        Step step = new Step()
                .setName("Validate something")
                .setType("validation");

        return new net.ihe.gazelle.maestro.api.business.test.Test()
                .setId("test-1")
                .setName("Sample test")
                .setSteps(List.of(step));
    }
}
