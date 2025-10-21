package net.ihe.gazelle.maestro.api.business.test;



import net.ihe.gazelle.maestro.api.business.property.Property;

import java.util.ArrayList;
import java.util.List;

public class TestReference {

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
      this.properties = properties != null ? properties : new ArrayList<>();
      return this;
   }
}
