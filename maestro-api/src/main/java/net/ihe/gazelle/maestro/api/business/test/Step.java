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

import net.ihe.gazelle.maestro.api.business.testrun.Property;

import java.util.List;

/**
 * Define an automated step to execute with all its required information.
 * A step contains has some properties representing its different required inputs.
 * {@link Property}
 */
public class Step {

   private String name;
   private String type;
   private List<Property<?>> properties;

   public Step() {
      // Empty for serialization
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

   public List<Property<?>> getProperties() {
      return properties;
   }

   public Step setProperties(List<Property<?>> properties) {
      this.properties = properties;
      return this;
   }

}

