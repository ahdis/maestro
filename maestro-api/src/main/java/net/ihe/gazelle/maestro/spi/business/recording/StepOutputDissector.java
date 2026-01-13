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

/**
 * SPI allowing a step implementation to describe how its outputs should be
 * persisted inside Datahouse.
 */
public interface StepOutputDissector {

   /**
    * Translate the step outputs into a persistence-ready projection.
    *
    * @param context immutable view of the step run, its surrounding test run and the ACL to apply
    * @return never {@code null}; use {@link StepOutputProjection#empty()} when nothing must be persisted
    */
   StepOutputProjection dissect(StepOutputContext context);
}
