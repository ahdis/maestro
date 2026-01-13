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

package net.ihe.gazelle.maestrorecording.adapter;

import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.spi.business.recording.StepOutputDissector;

/**
 * Abstraction responsible for selecting the appropriate {@link StepOutputDissector}
 * for a given {@link StepRunReport}. Implementations may rely on SPI lookups or
 * contextual information to choose the most suitable dissector.
 */
public interface StepOutputDissectorProvider {

   /**
    * Resolve the dissector to use for the supplied step.
    *
    * @param stepRunReport the step run report to inspect
    * @return never {@code null}; implementations should fall back to a default dissector if no match exists
    */
   StepOutputDissector getDissector(StepRunReport stepRunReport);

}
