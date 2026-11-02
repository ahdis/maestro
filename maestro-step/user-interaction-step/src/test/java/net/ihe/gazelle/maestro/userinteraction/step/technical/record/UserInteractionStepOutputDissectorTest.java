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

package net.ihe.gazelle.maestro.userinteraction.step.technical.record;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.BiConsumer;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayItemProperty;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.spi.business.recording.FutureReferenceCallbackRegistry;
import net.ihe.gazelle.maestro.spi.business.recording.StepOutputPlan;
import net.ihe.gazelle.maestro.spi.business.recording.StepOutputPlanDissectorFactory;
import net.ihe.gazelle.maestro.spi.business.recording.plan.AttachmentPlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.ItemPlan;
import net.ihe.gazelle.modelmarshaller.technical.marshalling.MarshallingException;
import net.ihe.gazelle.modelmarshaller.technical.marshalling.TextMarshaller;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class UserInteractionStepOutputDissectorTest {

   private static final TextMarshaller<TestReport> TEST_REPORT_MARSHALLER = new TextMarshaller<>() {

      @Override
      public byte[] marshall(TestReport report) throws MarshallingException {
         return marshallAsString(report).getBytes(StandardCharsets.UTF_8);
      }

      @Override
      public TestReport unmarshall(byte[] payload) {
         throw new UnsupportedOperationException();
      }
   };

   private final UserInteractionStepOutputDissector dissector = new UserInteractionStepOutputDissector();

   @Test
   void recordsFileOnceAndReplacesBothReportViews() {
      var file = new ByteArrayProperty("document", new byte[]{1, 2}).setFileName("patient.xml").setMimeType("text/xml");
      var report = new StepRunReport()
            .setInputs(List.of(file.copy()))
            .setOutputs(List.of(file.copy(), new StringProperty("patientId", "123")));
      var root = new ItemPlan<TestReport>("TEST_REPORT", new TestReport(), TEST_REPORT_MARSHALLER);
      var registry = new TestFutureReferenceCallbackRegistry();
      var reusedAttachment = new java.util.concurrent.atomic.AtomicReference<String>();
      registry.registerCallback("documentToValidate", (name, identifier) -> reusedAttachment.set(identifier));
      dissector.dissect(new StepOutputPlan(root, report, "http://dh", registry, java.util.Map.of("document", "documentToValidate")));
      assertEquals(1, root.futureReferencePlans().size());
      var future = root.futureReferencePlans().getFirst();
      var attachment = (AttachmentPlan) future.referencePlan().getTarget();
      assertEquals("patient.xml", attachment.filename());
      assertEquals("document", future.referencePlan().getName());
      future.mutation().mutateReference(new TestReport(), "document", "attachment-1");
      var output = (ByteArrayItemProperty) report.getOutputs().getFirst();
      var input = (ByteArrayItemProperty) report.getInputs().getFirst();
      assertEquals("attachment-1", reusedAttachment.get());
      assertEquals("/attachments/attachment-1", output.getReference());
      assertEquals(output.getReference(), input.getReference());
      assertNull(output.getValue());
      assertNull(input.getValue());
      assertEquals("text/xml", output.getMimeType());
      assertNotSame(input, output);
      assertEquals("123", report.getOutputs().get(1).getValue());
   }

   @Test
   void existingAttachmentIsReusedAndNotifiesConsumersOnRetry() {
      var file = new ByteArrayItemProperty("document", null).setReference("/attachments/att-existing");
      file.setFileName("patient.xml");
      var report = new StepRunReport().setInputs(List.of(file)).setOutputs(List.of(file));
      var root = new ItemPlan<TestReport>("TEST_REPORT", new TestReport(), TEST_REPORT_MARSHALLER);
      var registry = new TestFutureReferenceCallbackRegistry();
      var resolved = new java.util.concurrent.atomic.AtomicReference<String>();
      registry.registerCallback("documentToValidate", (name, identifier) -> resolved.set(identifier));
      dissector.dissect(new StepOutputPlan(root, report, "http://dh", registry, java.util.Map.of("document", "documentToValidate")));
      var future = root.futureReferencePlans().getFirst();
      assertEquals("att-existing", future.referencePlan().getTarget());
      future.mutation().mutateReference(new TestReport(), "document", "att-existing");
      assertEquals("att-existing", resolved.get());
      assertEquals("patient.xml", ((ByteArrayItemProperty) report.getOutputs().getFirst()).getFileName());
   }

   @Test
   void serviceLoaderDiscoversInteractionDissector() {
      var factories = java.util.ServiceLoader.load(StepOutputPlanDissectorFactory.class);
      assertTrue(factories.stream().map(java.util.ServiceLoader.Provider::get)
            .anyMatch(factory -> factory.supports(new StepRunReport().setType("USER_INTERACTION"))));
   }

   @Test
   void skipsScalarsAndAbsentFileValues() {
      var report = new StepRunReport().setOutputs(List.of(new StringProperty("patientId", "123"), new ByteArrayProperty("document", null)));
      var root = new ItemPlan<TestReport>("TEST_REPORT", new TestReport(), TEST_REPORT_MARSHALLER);
      dissector.dissect(new StepOutputPlan(root, report, "http://dh", new TestFutureReferenceCallbackRegistry()));
      assertTrue(root.futureReferencePlans().isEmpty());
   }

   private static final class TestFutureReferenceCallbackRegistry implements FutureReferenceCallbackRegistry {
      private final java.util.Map<String, BiConsumer<String, String>> callbacks = new java.util.HashMap<>();
      public void registerCallback(String name, BiConsumer<String, String> callback) {
         callbacks.put(name, callback);
      }
      public void notifyResolved(String name, String identifier) {
         if (callbacks.containsKey(name)) {
            callbacks.get(name).accept(name, identifier);
         }
      }
   }
}
