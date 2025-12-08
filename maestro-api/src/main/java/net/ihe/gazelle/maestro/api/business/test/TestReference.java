package net.ihe.gazelle.maestro.api.business.test;



import net.ihe.gazelle.maestro.api.business.property.Property;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TestReference implements Serializable {

   @Serial
   private static final long serialVersionUID = 1312276848438863528L;

   private String testId;
   private List<Property> properties = new ArrayList<>();

   public TestReference() {
      // Default constructor
   }

   public String getTestId() {
      return testId;
   }

   public TestReference setTestId(String testId) {
      this.testId = testId;
      return this;
   }

   public List<Property> getProperties() {
      return properties;
   }

   public TestReference setProperties(List<Property> properties) {
      this.properties = properties != null ? new ArrayList<>(properties) : new ArrayList<>();
      return this;
   }

   public boolean isTestIdDefined() {
      return testId != null && !testId.isEmpty();
   }
}
