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

package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.test.Step;

import java.util.ArrayList;
import java.util.List;

/**
 * Test implementation of Step interface for unit testing
 */
public class TestStep implements Step {

    private String name;
    private List<Property<?>> properties = new ArrayList<>();

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<Property<?>> getProperties() {
        return properties;
    }

    @Override
    public void setProperties(List<Property<?>> properties) {
        this.properties = properties;
    }

    @Override
    public String getType() {
        return "TEST";
    }
}
