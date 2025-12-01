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

package net.ihe.gazelle.maestro.api.technical.dto.property;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.modelmarshaller.technical.dto.SubTypingDTO;

import java.util.function.Function;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "type",
        "value"
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

   @JsonProperty("name")
   public String getName() {
      return property.getName();
   }

   public void setName(String name) {
      property.setName(name);
   }


   protected boolean isReference(String value) {
      return value != null && value.startsWith("${") && value.endsWith("}");
   }

   <R> void setResolvedValue(Class<R> expectedType, Function<String, R> parser, Object value) {
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
