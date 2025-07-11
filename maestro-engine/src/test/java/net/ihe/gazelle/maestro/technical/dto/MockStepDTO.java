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

package net.ihe.gazelle.maestro.technical.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import net.ihe.gazelle.maestro.technical.dto.testrun.StepDTO;

@JsonTypeName(MockStep.TYPE)
public class MockStepDTO extends StepDTO<MockStep> {

    public MockStepDTO() {
        super(new MockStep());
    }

    public MockStepDTO(MockStep mockStep) {
        super(mockStep);
    }

    @Override
    public Class<MockStep> getBusinessObjectClass() {
        return MockStep.class;
    }
}
