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

package net.ihe.gazelle.maestro.quarkus.websocket.dto;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.technical.dto.testrun.TestRunDTO;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
      name = "StartTestRun",
      description = "Message sent when a new test run is started."
)
@JsonTypeName(StartTestRunDTO.TYPE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"authorization"})
@JsonPropertyOrder({
      "type",
      "testRun",
      "persist",
})
public class StartTestRunDTO extends MessageDTO<TestRun> {

   @JsonIgnore
   public static final String TYPE = "START_TEST_RUN";

   private boolean persist;

   public StartTestRunDTO() {
      this(new TestRun());
   }

   public StartTestRunDTO(TestRun testRun) {
      super(testRun);
      this.persist = true;
   }

   @Schema(
         name = "testRun",
         description = "The test run that is staring."
   )
   @JsonProperty("testRun")
   public TestRunDTO getTestSuiteRun() {
      return new TestRunDTO(message);
   }

   public void setTestSuiteRun(TestRunDTO testRun) {
      this.message = testRun.getBusinessObject();
   }

   @Schema(
         name = "persist",
         description = "Whether the test report should be persisted."
   )
   @JsonProperty("persist")
   public boolean isPersist() {
      return persist;
   }

   public void setPersist(boolean persist) {
      this.persist = persist;
   }

   @Override
   @JsonIgnore
   public Class<TestRun> getBusinessObjectClass() {
      return TestRun.class;
   }
}
