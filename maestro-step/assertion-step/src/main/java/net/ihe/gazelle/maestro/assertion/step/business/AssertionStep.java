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

package net.ihe.gazelle.maestro.assertion.step.business;

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.Step;

import java.util.ArrayList;
import java.util.List;

/**
 * Assertion Step is not implemented. It exists just for test purpose
 */
public class AssertionStep implements Step {

    public static final String ASSERTION = "assertion";
    public static final String ASSERTION_SERVICE = "assertionService";


    public static final String TYPE = "ASSERTION";

    private String name;
    private Property<byte[]> currentAssertion;
    private Property<String> assertionService;

    public AssertionStep() {
        // Empty for serialization
    }

    public AssertionStep(String name,
                         Property<byte[]> currentAssertion,
                         Property<String> assertionService) {
        this.currentAssertion = currentAssertion;
        this.assertionService = assertionService;
        this.name = name;
    }

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
        List<Property<?>> props = new ArrayList<>();
        props.add(currentAssertion);
        props.add(assertionService);
        return props;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setProperties(List<Property<?>> properties) {
        if (properties == null) {
            throw new IllegalArgumentException("Property shouldn't be null");
        }
        properties.forEach(property -> {
            switch (property.getName()) {
                case ASSERTION -> currentAssertion = (Property<byte[]>) property;
                case ASSERTION_SERVICE -> assertionService = (Property<String>) property;
                default -> throw new IllegalArgumentException("Property " + property.getName()
                        + " is not an assertion step property");
            }
        });
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
