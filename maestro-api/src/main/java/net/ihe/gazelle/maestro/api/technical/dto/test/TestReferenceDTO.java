package net.ihe.gazelle.maestro.api.technical.dto.test;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.test.TestReference;
import net.ihe.gazelle.maestro.api.technical.dto.property.PropertyDTO;
import net.ihe.gazelle.modelmarshaller.technical.dto.DTO;
import net.ihe.gazelle.modelmarshaller.technical.dto.SubTypingDTO;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "testId",
        "properties",
})
public class TestReferenceDTO implements DTO<TestReference> {

    @JsonIgnore
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

    @SuppressWarnings("rawtypes")
   @JsonProperty("properties")
   public List<PropertyDTO> getProperties() {
      return testReference.getProperties().stream()
            .map(SubTypingDTO::fromBusinessObject)
            .map(PropertyDTO.class::cast)
            .toList();
   }

    @SuppressWarnings("rawtypes")
   public void setProperties(List<PropertyDTO> propertyDTOs) {
      testReference.setProperties(
            propertyDTOs != null ?
                  propertyDTOs.stream()
                        .map(DTO::getBusinessObject)
                        .map(Property.class::cast)
                        .toList() :
                  null
      );
   }

   @Override
   @JsonIgnore
   public TestReference getBusinessObject() {
      return testReference;
   }
}
