package net.ihe.gazelle.maestro.api.technical.dto.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.ihe.gazelle.maestro.api.business.test.SupportedInput;
import net.ihe.gazelle.maestro.api.business.test.TestReference;
import net.ihe.gazelle.maestro.api.business.test.TestSuite;
import net.ihe.gazelle.modelmarshaller.technical.dto.DTO;
import net.ihe.gazelle.modelmarshaller.technical.dto.SubTypingDTO;

import java.util.List;

public class TestSuiteDTO implements DTO<TestSuite> {

   private final TestSuite testSuite;

   public TestSuiteDTO() {
      this(new TestSuite());
   }

   public TestSuiteDTO(TestSuite testSuite) {
      this.testSuite = testSuite;
   }

   @JsonProperty("id")
   public String getId() {
      return testSuite.getId();
   }

   public void setId(String id) {
      testSuite.setId(id);
   }

   @JsonProperty("name")
   public String getName() {
      return testSuite.getName();
   }

   public void setName(String name) {
      testSuite.setName(name);
   }

   @JsonProperty("testReferences")
   public List<TestReferenceDTO> getTestReferences() {
      return testSuite.getTestReferences().stream()
            .map(TestReferenceDTO::new)
            .toList();
   }

   public void setTestReferences(List<TestReferenceDTO> testReferenceDTOs) {
      testSuite.setTestReferences(
            testReferenceDTOs != null ?
                  testReferenceDTOs.stream()
                        .map(DTO::getBusinessObject)
                        .map(TestReference.class::cast)
                        .toList() :
                  null
      );
   }

   @JsonProperty("supportedInputs")
   public List<SupportedInputDTO> getSupportedInputs() {
      return testSuite.getSupportedInputs().stream()
            .map(SubTypingDTO::fromBusinessObject)
            .map(SupportedInputDTO.class::cast)
            .toList();
   }

   public void setSupportedInputs(List<SupportedInputDTO> supportedInputDTOs) {
      testSuite.setSupportedInputs(
            supportedInputDTOs != null ?
                  supportedInputDTOs.stream()
                        .map(DTO::getBusinessObject)
                        .map(SupportedInput.class::cast)
                        .toList() :
                  null
      );
   }

   @Override
   public TestSuite getBusinessObject() {
      return testSuite;
   }

}
