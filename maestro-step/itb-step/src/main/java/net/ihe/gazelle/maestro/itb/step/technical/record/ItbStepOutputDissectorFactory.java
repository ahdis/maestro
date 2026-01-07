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

package net.ihe.gazelle.maestro.itb.step.technical.record;

import net.ihe.gazelle.maestro.itb.step.business.ItbStepDefinition;
import net.ihe.gazelle.maestro.spi.business.recording.StepOutputDissector;
import net.ihe.gazelle.maestro.spi.business.recording.StepOutputDissectorFactory;

/**
 * Factory class responsible for creating instances of {@link ItbStepOutputDissector}.
 */
public class ItbStepOutputDissectorFactory implements StepOutputDissectorFactory {

    /**
     * Default constructor.
     */
    public ItbStepOutputDissectorFactory() {
        // Empty
    }

    /**
     * Determines if the factory supports the given step type.
     *
     * @param stepType the type of the step to be verified for support.
     * @return true if the specified step type is supported, false otherwise.
     */
    @Override
    public boolean supports(final String stepType) {
        return ItbStepDefinition.TYPE.equals(stepType);
    }

    /**
     * Creates a new instance of {@link ItbStepOutputDissector}.
     *
     * @return a new {@link ItbStepOutputDissector} instance
     */
    @Override
    public StepOutputDissector create() {
        return new ItbStepOutputDissector();
    }
}
