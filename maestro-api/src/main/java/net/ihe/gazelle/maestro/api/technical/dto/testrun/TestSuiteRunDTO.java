/*
 * Copyright 2025 Kereval.
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

package net.ihe.gazelle.maestro.api.technical.dto.testrun;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;
import net.ihe.gazelle.maestro.api.technical.dto.property.PropertyDTO;
import net.ihe.gazelle.maestro.api.technical.dto.report.SystemUnderTestDTO;
import net.ihe.gazelle.maestro.api.technical.dto.test.TestDTO;
import net.ihe.gazelle.maestro.api.technical.dto.test.TestSuiteDTO;
import net.ihe.gazelle.modelmarshaller.technical.dto.DTO;
import net.ihe.gazelle.modelmarshaller.technical.dto.SubTypingDTO;
import net.ihe.gazelle.security.technical.acl.AccessControlListDTO;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
      "testSuite",
      "tests",
      "inputs",
      "systemsUnderTest",
      "accessControlList"
})
public class TestSuiteRunDTO implements DTO<TestSuiteRun> {

   @JsonIgnore
   private final TestSuiteRun testSuiteRun;

   public TestSuiteRunDTO() {
      this(new TestSuiteRun());
   }

   public TestSuiteRunDTO(TestSuiteRun testSuiteRun) {
      this.testSuiteRun = testSuiteRun;
   }

   @Override
   @JsonIgnore
   public TestSuiteRun getBusinessObject() {
      return testSuiteRun;
   }

   @JsonProperty("testSuite")
   public TestSuiteDTO getTestSuite() {
      return new TestSuiteDTO(testSuiteRun.getTestSuite());
   }

   public void setTestSuite(TestSuiteDTO testSuiteDTO) {
      testSuiteRun.setTestSuite(testSuiteDTO.getBusinessObject());
   }

   @JsonProperty("tests")
   public List<TestDTO> getTests() {
      return testSuiteRun.getTests().stream().map(TestDTO::new).toList();
   }

   public void setTests(List<TestDTO> testDTOs) {
      testSuiteRun.setTests(
            testDTOs != null ?
                  testDTOs.stream()
                        .map(TestDTO::getBusinessObject)
                        .toList() :
                  null
      );
   }

    @SuppressWarnings("rawtypes")
   @JsonProperty("inputs")
   public List<PropertyDTO> getInputs() {
      return testSuiteRun.getInputs().stream()
            .map(SubTypingDTO::fromBusinessObject)
            .map(PropertyDTO.class::cast)
            .toList();
   }

    @SuppressWarnings("rawtypes")
   public void setInputs(List<PropertyDTO> inputs) {
      testSuiteRun.setInputs(
            inputs != null ? inputs.stream()
                  .map(DTO::getBusinessObject)
                  .map(Property.class::cast).toList() :
                  null
      );
   }

   @JsonProperty("systemsUnderTest")
   public List<SystemUnderTestDTO> getSystemsUnderTest() {
      return testSuiteRun.getSystemsUnderTest().stream().map(SystemUnderTestDTO::new).toList();
   }

   public void setSystemsUnderTest(List<SystemUnderTestDTO> sutDTOs) {
      testSuiteRun.setSystemsUnderTest(
            sutDTOs != null ?
                  sutDTOs.stream()
                        .map(SystemUnderTestDTO::getBusinessObject)
                        .toList() :
                  null
      );
   }

   @JsonProperty("accessControlList")
   public AccessControlListDTO getAccessControlList() {
      return new AccessControlListDTO(testSuiteRun.getAccessControlList());
   }

   public void setAccessControlList(AccessControlListDTO accessControlList) {
      testSuiteRun.setAccessControlList(accessControlList.getBusinessObject());
   }

}
