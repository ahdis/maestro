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

import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.spi.business.recording.plan.FutureReferencePlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.ItemObjectMutation;
import net.ihe.gazelle.maestro.spi.business.recording.plan.ItemPlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.ReferencePlan;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class StepOutputPlan {


   private final ItemPlan<TestReport> rootReportPlan;

   private final StepRunReport stepRunReport;

   private final String datahouseUrl;

   public StepOutputPlan(ItemPlan<TestReport> rootReportPlan, StepRunReport stepRunReport, String datahouseUrl) {
      this.rootReportPlan = rootReportPlan;
      this.stepRunReport = stepRunReport;
      this.datahouseUrl = datahouseUrl;
   }

   public StepOutputPlan addReferencePlan(ReferencePlan referencePlan, ItemObjectMutation<StepRunReport> mutation) {
      if (referencePlan == null || mutation == null) {
         throw new IllegalArgumentException("referencePlan and mutation must not be null");
      }
      rootReportPlan.addFutureReferencePlan(
              referencePlan,
              (testReport, referenceName, referenceId) ->
                      mutation.mutateReference(stepRunReport, referenceName, referenceId)

      );

      return this;
   }

   public String getDatahouseUrl() {
      return datahouseUrl;
   }

   public StepRunReport getStepRunReport() {
      return stepRunReport;
   }

   public static boolean replaceOutput(String outputName, Function<Property, Property> replacementFactory,
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

}
