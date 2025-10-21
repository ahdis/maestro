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

import net.ihe.gazelle.maestro.api.business.property.Property;

import java.util.ArrayList;
import java.util.List;

public abstract class Step {

   private String name;
   private List<Property> properties = new ArrayList<>();

   protected Step() {
      // Empty for serialization
   }

   public String getName() {
      return name;
   }

   public Step setName(String name) {
      this.name = name;
      return this;
   }

   public List<Property> getProperties() {
      return properties;
   }

   public Step setProperties(List<Property> properties) {
      this.properties = properties != null ? new ArrayList<>(properties) : new ArrayList<>();
      return this;
   }

}

