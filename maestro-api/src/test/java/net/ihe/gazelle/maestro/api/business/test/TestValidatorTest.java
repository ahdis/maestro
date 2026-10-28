/*
 * Copyright 2026 IHE International.
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

package net.ihe.gazelle.maestro.api.business.test;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TestValidatorTest {

   @org.junit.jupiter.api.Test
   void testMinimalValid() {
      Test test = getMinimalTest();
      TestValidator validator = new TestValidator();
      assertDoesNotThrow(() -> validator.assertValid(test));
   }

   @org.junit.jupiter.api.Test
   void testMustHaveAName() {
      Test test = getMinimalTest().setName(null);
      TestValidator validator = new TestValidator();
      var result = validator.validate(test);
      assertThat(
            result.toString(),
            containsString("Test Name must be set and not empty => invalid")
      );
   }

   @org.junit.jupiter.api.Test
   void testMustHaveAnId() {
      Test test = getMinimalTest().setId(null);
      TestValidator validator = new TestValidator();
      var result = validator.validate(test);
      assertThat(
            result.toString(),
            containsString("Test ID must be set => invalid")
      );
   }

   @org.junit.jupiter.api.Test
   void testMustHaveAtLeastOneStep() {
      Test test = getMinimalTest().setSteps(null);
      TestValidator validator = new TestValidator();
      var result = validator.validate(test);
      assertThat(
            result.toString(),
            containsString("Test must contain at least one step => invalid")
      );
   }

   @org.junit.jupiter.api.Test
   void testStepsMustBeUnique() {
      Test test = getMinimalTest()
            .addStep(new Step()
                  .setName("First Step")
                  .setType("ACTION")
            );
      TestValidator validator = new TestValidator();
      var result = validator.validate(test);
      assertThat(
            result.toString(),
            containsString("Steps must be unique => invalid")
      );
   }

   @org.junit.jupiter.api.Test
   void testInvalidSupportedInput() {
      Test test = getMinimalTest()
            .addSupportedInput(new SupportedTextInput()
                  .setId(null)
                  .setLabel("A text input")
                  .setRequired(true)
            );
      TestValidator validator = new TestValidator();
      var result = validator.validate(test);
      assertThat(
            result.toString(),
            containsString("Test.supportedInputs[0] ID must be set and not empty => invalid")
      );
   }

   @org.junit.jupiter.api.Test
   void testOutputAliasCannotBeReused() {
      Test test = getMinimalTest()
            .addStep(new Step().setName("Second Step").setType("VALIDATION")
                  .setOutputMappings(java.util.Map.of("document", "sharedDocument")));
      test.getSteps().getFirst().setOutputMappings(java.util.Map.of("report", "sharedDocument"));
      var result = new TestValidator().validate(test);
      assertThat(result.toString(),
            containsString("Output aliases must be unique and must not reuse a supported input ID => invalid"));
   }

   @org.junit.jupiter.api.Test
   void testOutputAliasCannotShadowSupportedInput() {
      Test test = getMinimalTest().addSupportedInput(new SupportedTextInput().setId("document").setLabel("Document"));
      test.getSteps().getFirst().setOutputMappings(java.util.Map.of("report", "document"));
      var result = new TestValidator().validate(test);
      assertThat(result.toString(),
            containsString("Output aliases must be unique and must not reuse a supported input ID => invalid"));
   }

   private static Test getMinimalTest() {
      return new Test()
            .setId("123456")
            .setName("Sample Test")
            .addStep(new Step()
                  .setName("First Step")
                  .setType("VALIDATION")
            );
   }

}
