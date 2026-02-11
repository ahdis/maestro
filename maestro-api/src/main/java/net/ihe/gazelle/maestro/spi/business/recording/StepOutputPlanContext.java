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
import net.ihe.gazelle.security.business.acl.AccessControlList;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Immutable data exposed to v2 StepOutputPlanDissector implementations.
 */
public record StepOutputPlanContext(StepRunReport stepRunReport,
                                    AccessControlList accessControlList,
                                    String datahouseUrl) {

    public <T extends Property> T output(String outputName, Class<T> type) {
        if (stepRunReport == null || stepRunReport.getOutputs() == null || outputName == null) {
            return null;
        }
        for (Property output : stepRunReport.getOutputs()) {
            if (outputName.equals(output.getName()) && type.isInstance(output)) {
                return type.cast(output);
            }
        }
        return null;
    }


}
