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

import net.ihe.gazelle.maestro.api.business.property.ByteArrayItemProperty;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.spi.business.recording.plan.AttachmentPlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.ItemObjectMutation;
import net.ihe.gazelle.maestro.spi.business.recording.plan.ItemPlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.ReferencePlan;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public class StepOutputPlan {

   private final BiFunction<ReferencePlan, ItemObjectMutation<TestReport>, ItemPlan<TestReport>> addReferencePlanFunction;

   private final StepRunReport stepRunReport;

   private final String datahouseUrl;

   public StepOutputPlan(ItemPlan<TestReport> rootReportPlan, StepRunReport stepRunReport, String datahouseUrl) {
      this.addReferencePlanFunction = rootReportPlan::addFutureReferencePlan;
      this.stepRunReport = stepRunReport;
      this.datahouseUrl = datahouseUrl;
   }

   public StepOutputPlan addReferencePlan(ReferencePlan referencePlan, ItemObjectMutation<StepRunReport> mutation) {
      if (referencePlan == null) {
         throw new IllegalArgumentException("referencePlan must not be null");
      }
      if (mutation == null) {
         addReferencePlanFunction.apply(referencePlan, null);
         return this;
      }
      addReferencePlanFunction.apply(referencePlan,
            (testReport, referenceName, referenceId) ->
                  mutation.mutateReference(stepRunReport, referenceName, referenceId));

      return this;
   }

   public StepOutputPlan addItemReferencePlan(String outputName, ItemPlan<?> itemPlan) {
      if (outputName == null || outputName.isBlank() || itemPlan == null) {
         throw new IllegalArgumentException("outputName and itemPlan must not be null or blank");
      }
      return addReferencePlan(
            ReferencePlan.forItem(outputName, itemPlan.type(), itemPlan),
            (currentStepReport, referenceName, itemId) ->
                  replaceOutput(referenceName,
                        output -> toItemOutputReference(output, itemPlan.type(), datahouseUrl + "/items/" + itemId),
                        currentStepReport)
      );
   }

   public StepOutputPlan addAttachmentReferencePlan(String outputName, AttachmentPlan attachmentPlan) {
      if (outputName == null || outputName.isBlank() || attachmentPlan == null) {
         throw new IllegalArgumentException("outputName and attachmentPlan must not be null or blank");
      }
      return addReferencePlan(
            ReferencePlan.forAttachment(outputName, attachmentPlan),
            (currentStepReport, referenceName, attachmentId) ->
                  replaceOutput(referenceName,
                        output -> toAttachmentOutputReference(output, "/attachments/" + attachmentId),
                        currentStepReport)
      );
   }

   public String getDatahouseUrl() {
      return datahouseUrl;
   }

   public StepRunReport getStepRunReport() {
      return stepRunReport;
   }

   public static boolean replaceOutput(String outputName, UnaryOperator<Property> replacementFactory,
                                       StepRunReport stepRunReport) {
      if (stepRunReport == null || stepRunReport.getOutputs() == null || outputName == null
              || replacementFactory == null) {
         return false;
      }
      List<Property> outputs = stepRunReport.getOutputs();
      for (int i = 0; i < outputs.size(); i++) {
         Property output = outputs.get(i);
         if (outputName.equals(output.getName())) {
            outputs.set(i, Objects.requireNonNull(replacementFactory.apply(output), "replacement"));
            stepRunReport.setOutputs(outputs);
            return true;
         }
      }
      return false;
   }

   private static Property toItemOutputReference(Property output, String itemType, String reference) {
      if (output instanceof ByteArrayProperty byteArrayProperty) {
         ByteArrayItemProperty replacement = new ByteArrayItemProperty(byteArrayProperty);
         replacement.setValue(null);
         replacement.setItemType(itemType);
         replacement.setReference(reference);
         return replacement;
      }
      return output;
   }

   private static Property toAttachmentOutputReference(Property output, String reference) {
      if (output instanceof ByteArrayProperty byteArrayProperty) {
         ByteArrayItemProperty replacement = new ByteArrayItemProperty(byteArrayProperty);
         replacement.setValue(null);
         replacement.setReference(reference);
         return replacement;
      }
      return output;
   }

}
