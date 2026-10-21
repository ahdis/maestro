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

package net.ihe.gazelle.maestro.api.technical.dto.property;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.modelmarshaller.technical.dto.SubTypingDTO;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

@Schema(
        name = "Property",
        description = "A property containing a typed value."
)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "type",
        "value",
        "userId",
        "dateTime"
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public abstract class PropertyDTO<T extends Property> implements SubTypingDTO<T> {

    public static final String VALUE = "value";

    @JsonIgnore
    protected T property;

    protected PropertyDTO(T property) {
        initWithBusinessObject(property);
    }

    @Override
    public T getBusinessObject() {
        return property;
    }

    @Override
    public SubTypingDTO<T> initWithBusinessObject(T property) {
        this.property = property;
        return this;
    }

    @Schema(
            name = "name",
            description = "The name of the property.",
            examples = "stepInput",
            required = true
    )
    @JsonProperty("name")
    public String getName() {
        return property.getName();
    }

    public void setName(String name) {
        property.setName(name);
    }

    @Schema(
            name = "userId",
            description = "The id of the user who created the property."
    )
    @JsonProperty("userId")
    public String getUserId() {
        return property.getUserId();
    }

    public void setUserId(String userId) {
        property.setUserId(userId);
    }

    @Schema(
            name = "dateTime",
            description = "The date time of the property creation."
    )
    @JsonProperty("dateTime")
    public String getDateTime() {
        return ZonedDateTime.ofInstant(property.getDateTime(), ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public void setDateTime(String dateTime) {
        property.setDateTime(
                Instant.from(ZonedDateTime.parse(dateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME))
        );
    }

    /**
     * Returns the Jackson polymorphic type name.
     */
    @JsonIgnore
    public abstract String getType();

    /**
     * Returns the value of the property.
     */
    public abstract <U> U getValue();

    protected boolean isReference(String value) {
        return value != null && value.startsWith("${") && value.endsWith("}");
    }

    <R extends Serializable> void setResolvedValue(Class<R> expectedType, Function<String, R> parser, Serializable value) {
        if (value == null) {
            property.setValue(null);
            return;
        }
        if (value instanceof String strValue) {
            if (isReference(strValue)) {
                property.setValue(strValue);
            } else {
                property.setValue(parser.apply(strValue));
            }
            return;
        }
        if (expectedType.isInstance(value)) {
            property.setValue(expectedType.cast(value));
            return;
        }
        throw new IllegalArgumentException("Unsupported value type for " + getClass() + ": " + value.getClass());
    }

}
