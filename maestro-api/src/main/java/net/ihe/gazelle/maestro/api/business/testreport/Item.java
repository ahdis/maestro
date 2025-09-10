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

import java.util.Arrays;
import java.util.Objects;

/**
 * Business rules: <br>
 * name is mandatory <br>
 * content xor reference <br>
 */
public class Item {

    private String name;
    private byte[] content;
    private String reference;

    public Item() {
    }

    public Item(String name, byte[] content) {
        this.name = name;
        this.content = content;
    }

    public Item(String name, String reference) {
        this.name = name;
        this.reference = reference;
    }

    public String getName() {
        return name;
    }

    public Item setName(String name) {
        this.name = name;
        return this;
    }

    public byte[] getContent() {
        return content;
    }

    public Item setContent(byte[] content) {
        this.content = content;
        return this;
    }

    public String getReference() {
        return reference;
    }

    public Item setReference(String reference) {
        this.reference = reference;
        return this;
    }

    public boolean isNameValid() {
        return name != null && !name.isBlank();
    }

    public boolean isContentValid() {
        if (content != null && content.length > 0) {
            return reference == null;
        } else {
            return reference != null;
        }
    }

    public boolean isReferenceValid() {
        if (reference != null && !reference.isBlank()) {
            return content == null;
        } else {
            return content != null;
        }
    }

    public boolean isValid() {
        return isNameValid() && isContentValid() && isReferenceValid();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item item)) return false;
        return Objects.equals(name, item.name)
                && Objects.deepEquals(content, item.content)
                && Objects.equals(reference, item.reference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, Arrays.hashCode(content), reference);
    }
}
