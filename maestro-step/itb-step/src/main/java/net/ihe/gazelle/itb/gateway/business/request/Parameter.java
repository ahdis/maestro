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

package net.ihe.gazelle.itb.gateway.business.request;

/**
 * Model for a single input of a RunTestsUseCase implementation
 */
public class Parameter {

    private String name;
    private String type;
    private String value;

    /**
     * Creates an empty parameter.
     */
    public Parameter() {
    }

    /**
     * Creates a parameter with name and value.
     *
     * @param name parameter name
     * @param value parameter value
     */
    public Parameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Creates a fully specified parameter.
     *
     * @param name parameter name
     * @param type parameter type
     * @param value parameter value
     */
    public Parameter(String name, String type, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    /**
     * Returns parameter name.
     *
     * @return parameter name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets parameter name.
     *
     * @param name parameter name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns parameter type.
     *
     * @return parameter type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets parameter type.
     *
     * @param type parameter type
     */
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    /**
     * Returns parameter value.
     *
     * @return parameter value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets parameter value.
     *
     * @param value parameter value
     */
    public void setValue(String value) {
        this.value = value;
    }
}
