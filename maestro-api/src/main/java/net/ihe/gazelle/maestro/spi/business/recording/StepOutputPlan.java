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

import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.spi.business.recording.plan.FutureReferencePlan;

import java.util.List;
import java.util.Objects;

public record StepOutputPlan(List<FutureReferencePlan<TestReport>> reportReferencePlans) {

   public StepOutputPlan {
      reportReferencePlans = List.copyOf(Objects.requireNonNullElse(reportReferencePlans, List.of()));
   }

   public static StepOutputPlan empty() {
      return new StepOutputPlan(List.of());
   }
}
