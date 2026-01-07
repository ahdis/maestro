package net.ihe.gazelle.maestro.api.business.message;

import net.ihe.gazelle.lang.Event;

import java.io.Serial;

/**
 * A message to notify that a test execution started
 */
@Event
public class TestRunStarted implements Message {

   @Serial
   private static final long serialVersionUID = 1L;

   /**
    * The id of the test to execute
    */
   private String testId;

   /**
    * Default constructor
    */
   public TestRunStarted() {
   }

   /**
    * Constructs a new {@code TestRunStarted} instance with the specified test ID.
    *
    * @param testId the ID of the test to be executed
    */
   public TestRunStarted(String testId) {
      this.testId = testId;
   }

   /**
    * Retrieves the ID of the test to be executed.
    *
    * @return the test ID as a string.
    */
   public String getTestId() {
      return testId;
   }

   /**
    * Sets the ID of the test to be executed.
    *
    * @param testId the ID of the test to be set
    * @return the current instance of {@code TestRunStarted} to allow method chaining
    */
   public TestRunStarted setTestId(String testId) {
      this.testId = testId;
      return this;
   }
}
