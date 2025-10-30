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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * <p>
 * An ITB Step is an automated step used to execute a test in ITB.
 * This step has 7 properties.
 */
public class ItbStep implements Step {

    public static final String TEST_CASE = "testCase";
    public static final String SYSTEM_ID = "systemID";
    public static final String ACTOR_ID = "actorID";

    public static final String TYPE = "ITB";

    private String name;
    private Property<String> testCase;
    private Property<String> systemID;
    private Property<String> actorID;
    private List<Property<?>> inputProperties;

    public ItbStep() {
        inputProperties = new ArrayList<>();
    }

    public ItbStep(String name, Property<String> testCase, Property<String> systemID, Property<String> actorID, List<? extends Property<?>> inputs){
        this.name = name;
        this.testCase = testCase;
        this.systemID = systemID;
        this.actorID = actorID;
        this.inputProperties = new ArrayList<>(inputs);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Property<String> getTestCase() {
        return testCase;
    }
    public Property<String> getSystemID() {
        return systemID;
    }
    public Property<String> getActorID() {
        return actorID;
    }

    @Override
    public List<Property<?>> getProperties() {
        List<Property<?>> props = new ArrayList<>();
        if (testCase != null) {
            props.add(testCase);
        }
        if (systemID != null) {
            props.add(systemID);
        }
        if (actorID != null) {
            props.add(actorID);
        }
        props.addAll(inputProperties);
        return props;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setProperties(List<Property<?>> properties) {
        if (properties == null) {
            throw new IllegalArgumentException("property shouldn't be null");
        }
        LinkedHashMap<String, Property<?>> inputsByName = new LinkedHashMap<>();
        for (Property<?> input : inputProperties) {
            inputsByName.put(input.getName(), input);
        }

        properties.forEach(property -> {
            switch (property.getName()) {
                case TEST_CASE -> testCase = (Property<String>) property;
                case SYSTEM_ID -> systemID = (Property<String>) property;
                case ACTOR_ID -> actorID = (Property<String>) property;
                default -> inputsByName.put(property.getName(), property);
            }
        });

        inputProperties = new ArrayList<>(inputsByName.values());
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
