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

package net.ihe.gazelle.maestro.api.business.testreport;

import java.util.Objects;

/**
 * Business rules <br>
 * id is mandatory
 */
public class Test {

    private String id;
    private String name;
    private String version;
    private String description;

    public Test() {
    }

    public Test(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Test setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Test setName(String name) {
        this.name = name;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public Test setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Test setDescription(String description) {
        this.description = description;
        return this;
    }

    public boolean isIdValid() {
        return id != null && !id.isBlank();
    }

    public boolean isNameValid() {
        return name == null || !name.isBlank();
    }

    public boolean isVersionValid() {
        return version == null || !version.isBlank();
    }

    public boolean isDescriptionValid() {
        return description == null || !description.isBlank();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Test test)) return false;
        return Objects.equals(id, test.id)
                && Objects.equals(name, test.name)
                && Objects.equals(version, test.version)
                && Objects.equals(description, test.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, version, description);
    }
}
