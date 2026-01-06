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
