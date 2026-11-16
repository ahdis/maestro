/*
 * Copyright 2025-2026 IHE International.
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

package net.ihe.gazelle.maestro.api.business.message;

import net.ihe.gazelle.maestro.api.business.property.Property;

import java.io.Serial;
import java.time.Instant;
import java.util.List;

/**
 * Message indicating that output properties produced by a step are available.
 *
 * This message identifies the test and step that produced the outputs, records
 * the time at which the outputs became available, and carries the resulting
 * properties. The output list is defensively copied during construction and
 * stored as an unmodifiable list.
 *
 * @param testId the identifier of the test associated with the step outputs
 * @param stepName the name of the step that produced the outputs
 * @param dateTime the date and time when the outputs became available
 * @param outputs the output properties produced by the step
 */
public record StepOutputsAvailable(
        String testId, String stepName, Instant dateTime, List<Property> outputs)
        implements Message {
    @Serial
    private static final long serialVersionUID = 1L;

    public StepOutputsAvailable {
        outputs = List.copyOf(outputs);
    }
}
