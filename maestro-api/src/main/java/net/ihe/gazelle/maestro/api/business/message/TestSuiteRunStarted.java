package net.ihe.gazelle.maestro.api.business.message;

import net.ihe.gazelle.lang.Event;

import java.io.Serial;

/**
 * A message to notify that a test suite execution started
 */
@Event
public class TestSuiteRunStarted implements Message {

   @Serial
   private static final long serialVersionUID = 1L;

   /**
    * The id of the test suite run
    */
   private String testSuiteRunId;

   /**
    * Default constructor
    */
   public TestSuiteRunStarted() {
   }

   /**
    * Constructs a new TestSuiteRunStarted instance with the specified test suite run ID.
    *
    * @param testSuiteRunId the ID of the test suite run that has started.
    */
   public TestSuiteRunStarted(String testSuiteRunId) {
      this.testSuiteRunId = testSuiteRunId;
   }

   /**
    * Retrieves the ID of the test suite run.
    *
    * @return the ID of the test suite run as a string.
    */
   public String getTestSuiteRunId() {
      return testSuiteRunId;
   }

   /**
    * Sets the ID of the test suite run and returns the current instance for method chaining.
    *
    * @param testSuiteRunId the ID of the test suite run to be set.
    * @return the current instance of {@code TestSuiteRunStarted} for method chaining.
    */
   public TestSuiteRunStarted setTestSuiteRunId(String testSuiteRunId) {
      this.testSuiteRunId = testSuiteRunId;
      return this;
   }
}
