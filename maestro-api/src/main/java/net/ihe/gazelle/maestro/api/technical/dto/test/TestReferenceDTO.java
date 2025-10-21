package net.ihe.gazelle.maestro.api.technical.dto.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.ihe.gazelle.maestro.api.business.test.TestReference;
import net.ihe.gazelle.maestro.api.technical.dto.property.PropertyDTO;
import net.ihe.gazelle.modelmarshaller.technical.dto.DTO;
import net.ihe.gazelle.modelmarshaller.technical.dto.SubTypingDTO;

import java.util.List;

public class TestReferenceDTO implements DTO<TestReference> {

   private final TestReference testReference;

   public TestReferenceDTO() {
      this(new TestReference());
   }

   public TestReferenceDTO(TestReference testReference) {
      this.testReference = testReference;
   }

   @JsonProperty("testId")
   public String getTestId() {
      return testReference.getTestId();
   }

   public void setTestId(String testId) {
      testReference.setTestId(testId);
   }

   @JsonProperty("properties")
   public List<PropertyDTO> getProperties() {
      return testReference.getProperties().stream()
            .map(SubTypingDTO::fromBusinessObject)
            .map(PropertyDTO.class::cast)
            .toList();
   }

   public void setProperties(List<PropertyDTO> propertyDTOs) {
      testReference.setProperties(
            propertyDTOs != null ?
                  propertyDTOs.stream()
                        .map(DTO::getBusinessObject)
                        .map(net.ihe.gazelle.maestro.api.business.property.Property.class::cast)
                        .toList() :
                  null
      );
   }

   @Override
   public TestReference getBusinessObject() {
      return testReference;
   }
}
