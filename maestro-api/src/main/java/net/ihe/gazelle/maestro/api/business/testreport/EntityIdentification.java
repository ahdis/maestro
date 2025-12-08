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

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Business rules: <br> name is mandatory
 */
public class EntityIdentification implements Serializable {

   @Serial
   private static final long serialVersionUID = 8403054616731250869L;

   private String version;
   private String name;

   public EntityIdentification() {
   }

   public EntityIdentification(String name) {
      this.name = name;
   }

   public EntityIdentification(String version, String name) {
      this(name);
      this.version = version;
   }

   public String getVersion() {
      return version;
   }

   public EntityIdentification setVersion(String version) {
      this.version = version;
      return this;
   }

   public String getName() {
      return name;
   }

   public EntityIdentification setName(String name) {
      this.name = name;
      return this;
   }

   public boolean isVersionDefinedIfPresent() {
      return version == null || !version.isBlank();
   }

   public boolean isNameDefined() {
      return name != null && !name.isBlank();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (!(o instanceof EntityIdentification that)) {
         return false;
      }
      return Objects.equals(version, that.version)
             && Objects.equals(name, that.name);
   }

   @Override
   public int hashCode() {
      return Objects.hash(version, name);
   }
}
