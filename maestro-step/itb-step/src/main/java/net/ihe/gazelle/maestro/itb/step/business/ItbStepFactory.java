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

package net.ihe.gazelle.maestro.itb.step.business;

import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.spi.business.runner.StepFactory;

import java.util.Map;

// TODO : to delete when StepFactoryProvider will be deleted
public class ItbStepFactory implements StepFactory {

    /**
     * @param name
     * @param properties
     * @return
     * @throws ClassCastException if given properties does not have expected types
     */
    @Override
    @SuppressWarnings("unchecked")
    public Step create(String name, Map<String, Property<?>> properties) {
        return new ItbStep(name,
                (Property<String>) properties.get(ItbStep.TEST_CASE),
                (Property<String>) properties.get(ItbStep.SYSTEM_ID),
                (Property<String>) properties.get(ItbStep.ACTOR_ID),
                properties.entrySet().stream()
                        .filter(entry -> !isCoreProperty(entry.getKey()))
                        .map(Map.Entry::getValue)
                        .toList()
        );
    }

    @Override
    public String getSupportedStep() {
        return ItbStep.TYPE;
    }

    private boolean isCoreProperty(String propertyName) {
        return ItbStep.TEST_CASE.equals(propertyName)
                || ItbStep.SYSTEM_ID.equals(propertyName)
                || ItbStep.ACTOR_ID.equals(propertyName);
    }
}
