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
public class Output {

    public static final String FORMAT_XML = "XML";
    public static final String FORMAT_JSON = "JSON";
    public static final String FORMAT_PDF = "PDF";
    public static final String FORMAT_TEXT = "TEXT";


    private String name;
    private byte[] content;
    private String reference;
    private String format;


    public Output() {
    }

    public Output(String name, byte[] content, String format) {
        this.name = name;
        this.content = content;
        this.format = format;
    }

    public Output(String name, String reference, String format) {
        this.name = name;
        this.reference = reference;
        this.format = format;
    }

    public String getName() {
        return name;
    }

    public Output setName(String name) {
        this.name = name;
        return this;
    }

    public byte[] getContent() {
        return content;
    }

    public Output setContent(byte[] content) {
        this.content = content;
        return this;
    }

    public String getReference() {
        return reference;
    }

    public Output setReference(String reference) {
        this.reference = reference;
        return this;
    }

    public String getFormat() {
        return format;
    }

    public Output setFormat(String format) {
        this.format = format;
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

    public boolean isFormatValid() {
        return format != null && !format.isBlank();
    }

    public boolean isValid() {
        return isNameValid() && isContentValid() && isReferenceValid();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Output output)) return false;
        return Objects.equals(name, output.name)
                && Objects.deepEquals(content, output.content)
                && Objects.equals(reference, output.reference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, Arrays.hashCode(content), reference);
    }
}
