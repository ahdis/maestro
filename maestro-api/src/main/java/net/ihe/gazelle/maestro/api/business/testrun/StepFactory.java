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

package net.ihe.gazelle.maestro.api.business.testrun;

import net.ihe.gazelle.maestro.api.business.test.Step;

import java.util.Map;

public interface StepFactory {

    /**
     * @param name step's name
     * @param properties a map containing properties with property's name as key
     * @return a Step with attribute name and all its properties set corresponding to properties
     * @throws ClassCastException if given properties does not have expected types
     */
    Step create(String name, Map<String, Property<?>> properties);

    /**
     * @return a string of provided step type
     */
    String getSupportedStep();
}
