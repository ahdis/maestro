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

package net.ihe.gazelle.maestro.api.technical.dto;

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.test.Step;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MockStep implements Step {

    public static final String TYPE = "MOCK";
    public static final String STRING_PROPERTY = "stringProperty";
    public static final String INTEGER_PROPERTY = "integerProperty";
    public static final String BOOLEAN_PROPERTY = "booleanProperty";
    public static final String DOUBLE_PROPERTY = "doubleProperty";
    public static final String BYTE_ARRAY_PROPERTY = "byteArrayProperty";
    public static final String DATE_PROPERTY = "dateProperty";

    private String name;
    private Property<String> stringProperty;
    private Property<Integer> integerProperty;
    private Property<Boolean> booleanProperty;
    private Property<Double> doubleProperty;
    private Property<byte[]> byteArrayProperty;
    private Property<Date> dateProperty;

    public MockStep() {
    }

    public MockStep(String name, Property<String> stringProperty, Property<Integer> integerProperty, Property<Boolean> booleanProperty, Property<Double> doubleProperty, Property<byte[]> byteArrayProperty, Property<Date> dateProperty) {
        this.name = name;
        this.stringProperty = stringProperty;
        this.integerProperty = integerProperty;
        this.booleanProperty = booleanProperty;
        this.doubleProperty = doubleProperty;
        this.byteArrayProperty = byteArrayProperty;
        this.dateProperty = dateProperty;
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
        props.add(stringProperty);
        props.add(integerProperty);
        props.add(booleanProperty);
        props.add(doubleProperty);
        props.add(byteArrayProperty);
        props.add(dateProperty);
        return props;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setProperties(List<Property<?>> properties) {
        if (properties == null) {
            throw new IllegalArgumentException("property shouldn't be null");
        }
        properties.forEach(property -> {
            switch (property.getName()) {
                case STRING_PROPERTY -> stringProperty = (Property<String>) property;
                case INTEGER_PROPERTY -> integerProperty = (Property<Integer>) property;
                case BOOLEAN_PROPERTY -> booleanProperty = (Property<Boolean>) property;
                case DOUBLE_PROPERTY -> doubleProperty = (Property<Double>) property;
                case BYTE_ARRAY_PROPERTY -> byteArrayProperty = (Property<byte[]>) property;
                case DATE_PROPERTY -> dateProperty = (Property<Date>) property;
                default -> throw new IllegalArgumentException("property " + property.getName()
                        + " is not a mock step property");
            }
        });
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
