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

package net.ihe.gazelle.maestro.api.business.test;

import net.ihe.gazelle.maestro.api.business.property.MissingPropertyException;
import net.ihe.gazelle.maestro.api.business.property.Property;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Step implements Serializable {

   @Serial
   private static final long serialVersionUID = 4126739543481925460L;

   private String name;
   private String type;
   // The base Map interface is not serializable, so we declare properties as ConcurrentHashMap.
   private ConcurrentHashMap<String, Property> properties = new ConcurrentHashMap<>();
   private ConcurrentHashMap<String, String> outputMappings = new ConcurrentHashMap<>();


   public Step() {
      // Empty for serialization
   }

   public Step(Step other) {
      this(other.name, other.type, other.getProperties(), other.getOutputMappings());
   }

   protected Step(String name, String type, List<Property> properties, Map<String, String> outputMappings) {
      this.name = name;
      this.type = type;
      properties.forEach(property -> this.properties.put(property.getName(), property.copy()));
      this.outputMappings = outputMappings != null ?
            new ConcurrentHashMap<>(outputMappings) :
            new ConcurrentHashMap<>();
   }

   public String getName() {
      return name;
   }

   public Step setName(String name) {
      this.name = name;
      return this;
   }

   public String getType() {
      return type;
   }

   public Step setType(String type) {
      this.type = type;
      return this;
   }

   public List<Property> getProperties() {
      return new ArrayList<>(properties.values());
   }

   public boolean hasProperty(String name) {
      return properties.containsKey(name);
   }

   /**
    * Get the property with the given name.
    *
    * @param name the name of the property
    *
    * @return the property
    *
    * @throws MissingPropertyException if no property with the given name is found
    */
   public Property getProperty(String name) {
      return Optional.ofNullable(properties.get(name))
            .orElseThrow(() -> new MissingPropertyException("No property found with name: " + name));
   }

   /**
    * Get the value of the property with the given name.
    *
    * @param name the name of the property
    * @param <T>  the type of the property value
    *
    * @return the value of the property
    *
    * @throws MissingPropertyException if no property with the given name is found
    */
   public synchronized <T> T getPropertyValue(String name) {
      return getProperty(name).getValue();
   }

   public synchronized Step setProperties(List<Property> properties) {
      this.properties = properties != null ? buildMap(properties) : new ConcurrentHashMap<>();
      return this;
   }

   public Map<String, String> getOutputMappings() {
      return new ConcurrentHashMap<>(outputMappings);
   }

   public Step setOutputMappings(Map<String, String> outputMappings) {
      this.outputMappings = outputMappings != null ?
            new ConcurrentHashMap<>(outputMappings) :
            new ConcurrentHashMap<>();
      return this;
   }

   public Step addOutputMapping(String key, String value) {
      this.outputMappings.put(key, value);
      return this;
   }

   public boolean isNameDefined() {
      return name != null && !name.isEmpty();
   }

   public boolean isTypeDefined() {
      return type != null && !type.isEmpty();
   }

   public boolean areOutputMappingsValid() {
      return outputMappings.entrySet().stream()
            .allMatch(Step::isMapEntryDefined);
   }

   protected static ConcurrentHashMap<String, Property> buildMap(List<Property> properties) {
      return properties.stream().collect(
            ConcurrentHashMap::new,
            (map, property) -> map.put(property.getName(), property.copy()),
            ConcurrentHashMap::putAll
      );
   }

   private static boolean isMapEntryDefined(Map.Entry<String, String> entry) {
      return entry.getKey() != null && !entry.getKey().isBlank()
                      && entry.getValue() != null && !entry.getValue().isBlank();
   }

}

