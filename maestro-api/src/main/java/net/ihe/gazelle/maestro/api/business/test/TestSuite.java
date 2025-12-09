package net.ihe.gazelle.maestro.api.business.test;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TestSuite implements Serializable {

   @Serial
   private static final long serialVersionUID = 6316961081179645096L;

   private String id;
   private String name;
   private List<TestReference> testReferences = new ArrayList<>();
   private List<SupportedInput> supportedInputs = new ArrayList<>();

   public TestSuite() {
      // Default constructor
   }

   public String getId() {
      return id;
   }

   public TestSuite setId(String id) {
      this.id = id;
      return this;
   }

   public String getName() {
      return name;
   }

   public TestSuite setName(String name) {
      this.name = name;
      return this;
   }

   public List<TestReference> getTestReferences() {
      return new ArrayList<>(testReferences);
   }

   public TestSuite setTestReferences(List<TestReference> testReferences) {
      this.testReferences = testReferences != null
              ? new ArrayList<>(testReferences)
              : new ArrayList<>();
      return this;
   }

   public List<SupportedInput> getSupportedInputs() {
      return new ArrayList<>(supportedInputs);
   }

   public TestSuite setSupportedInputs(List<SupportedInput> supportedInputs) {
       this.supportedInputs = supportedInputs != null
               ? new ArrayList<>(supportedInputs)
               : new ArrayList<>();
       return this;
   }

   public boolean isNameDefined() {
      return name != null && !name.isBlank();
   }

   public boolean isIdDefined() {
      return id != null && !id.isBlank();
   }

   public boolean hasAtLeastOneTest() {
      return testReferences != null && !testReferences.isEmpty();
   }
}
