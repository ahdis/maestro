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
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Step implements Serializable {

   @Serial
   private static final long serialVersionUID = 4126739543481925460L;

   private String id;
   private String name;
   private String type;
   // The base Map interface is not serializable, so we declare properties as ConcurrentHashMap.
   protected ConcurrentHashMap<String, Property> properties = new ConcurrentHashMap<>();

   public Step() {
      // Empty for serialization
   }

   public Step(Step other) {
      this.id = other.id;
      this.name = other.name;
      this.type = other.type;
      // not only we copied the map, but also each property to avoid shared references.
      this.properties = other.properties.values().stream().collect(
            ConcurrentHashMap::new,
            (map, property) -> map.put(property.getName(), property.copy()),
            ConcurrentHashMap::putAll
      );
   }

   public String getId() {
      return id;
   }

   public Step setId(String id) {
      this.id = id;
      return this;
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
   public <T> T getPropertyValue(String name) {
      return getProperty(name).getValue();
   }

   public synchronized Step setProperties(List<Property> properties) {
      this.properties = properties != null ? buildMap(properties) : new ConcurrentHashMap<>();
      return this;
   }

   public synchronized Step addProperties(List<Property> properties) {
      properties.forEach(property -> this.properties.put(property.getName(), property));
      return this;
   }

   public boolean isIdDefined() {
      return id != null && !id.isEmpty();
   }

   public boolean isNameDefined() {
      return name != null && !name.isEmpty();
   }

   public boolean isTypeDefined() {
      return type != null && !type.isEmpty();
   }

   private ConcurrentHashMap<String, Property> buildMap(List<Property> properties) {
      return properties.stream().collect(
            ConcurrentHashMap::new,
            (map, property) -> map.put(property.getName(), property),
            ConcurrentHashMap::putAll
      );
   }
}

