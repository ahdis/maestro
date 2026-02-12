/*
 * Copyright 2026 IHE International.
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

package net.ihe.gazelle.maestro.spi.business.recording;

import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.testreport.Result;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.spi.business.recording.plan.FutureReferencePlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.ItemPlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.ReferencePlan;
import net.ihe.gazelle.modelmarshaller.technical.marshalling.MarshallingException;
import net.ihe.gazelle.modelmarshaller.technical.marshalling.TextMarshaller;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class RecordingPlanTest {

   private static final TextMarshaller<String> STRING_MARSHALLER = new TextMarshaller<>() {
      @Override
      public String marshallAsString(String value) throws MarshallingException {
         return value;
      }

      @Override
      public String unmarshall(String payload) {
         return payload;
      }

      @Override
      public byte[] marshall(String value) throws MarshallingException {
         return value.getBytes();
      }

      @Override
      public String unmarshall(byte[] payload) {
         return new String(payload);
      }
   };

   @Test
   void forItemKeepsProvidedTypeAndTarget() {
      ItemPlan<String> childPlan = new ItemPlan<>("child-type", "child", STRING_MARSHALLER);

      ReferencePlan referencePlan = ReferencePlan.forItem("child-ref", childPlan.type(), childPlan);

      assertEquals("child-type", referencePlan.getTargetType());
      assertSame(childPlan, referencePlan.getTarget());
   }

   @Test
   void withItemReferenceUsesReferencedItemType() {
      ItemPlan<String> parentPlan = new ItemPlan<>("parent-type", "parent", STRING_MARSHALLER);
      ItemPlan<String> childPlan = new ItemPlan<>("child-type", "child", STRING_MARSHALLER);

      parentPlan.withItemReference("child-ref", childPlan);
      FutureReferencePlan<String> futureReferencePlan = parentPlan.futureReferencePlans().getFirst();

      assertEquals("child-type", futureReferencePlan.referencePlan().getTargetType());
      assertSame(childPlan, futureReferencePlan.referencePlan().getTarget());
   }

   @Test
   void addItemReferencePlanUsesItemType() {
      ItemPlan<TestReport> rootReportPlan = new ItemPlan<>("test-report", newTestReport(), null);
      StepRunReport stepRunReport = new StepRunReport()
            .addOutput(new ByteArrayProperty("output-a", "value".getBytes()));
      StepOutputPlan stepOutputPlan = new StepOutputPlan(rootReportPlan, stepRunReport, "http://dh");
      ItemPlan<String> outputPlan = new ItemPlan<>("output-type", "output-payload", STRING_MARSHALLER);

      stepOutputPlan.addItemReferencePlan("output-a", outputPlan);
      FutureReferencePlan<TestReport> futureReferencePlan = rootReportPlan.futureReferencePlans().getFirst();

      assertEquals("output-type", futureReferencePlan.referencePlan().getTargetType());
      assertSame(outputPlan, futureReferencePlan.referencePlan().getTarget());
   }

   private static TestReport newTestReport() {
      return new TestReport()
            .setUuid("report-1")
            .setDateTime(Instant.now())
            .setResult(Result.UNDEFINED);
   }
}
