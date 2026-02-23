/*
 * Copyright 2026 IHE International.
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

package net.ihe.gazelle.maestro.api.business.property;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Ephemeral payload used during recording to keep provenance bindings between step properties and reported targets.
 */
public class PropertyBindingPayload implements Serializable {

   /**
    * Name of the ephemeral step output property carrying this payload.
    */
   public static final String PROPERTY_NAME = "propertyBinding";

   /**
    * Current schema version for serialization.
    */
   public static final int SCHEMA_VERSION = 1;

   @Serial
   private static final long serialVersionUID = 5301730568159111578L;

   private int schemaVersion = SCHEMA_VERSION;
   private String stepType;
   private List<Binding> bindings = new ArrayList<>();

   public int getSchemaVersion() {
      return schemaVersion;
   }

   public PropertyBindingPayload setSchemaVersion(int schemaVersion) {
      this.schemaVersion = schemaVersion;
      return this;
   }

   public String getStepType() {
      return stepType;
   }

   public PropertyBindingPayload setStepType(String stepType) {
      this.stepType = stepType;
      return this;
   }

   public List<Binding> getBindings() {
      return new ArrayList<>(bindings);
   }

   public PropertyBindingPayload setBindings(List<Binding> bindings) {
      this.bindings = bindings == null ? new ArrayList<>() : new ArrayList<>(bindings);
      return this;
   }

   public PropertyBindingPayload addBinding(Binding binding) {
      if (binding != null) {
         bindings.add(binding);
      }
      return this;
   }

   /**
    * One binding entry between a target in an external report and a local step property.
    */
   public static class Binding implements Serializable {
      @Serial
      private static final long serialVersionUID = 3190953897475394209L;

      private String targetKind;
      private String targetId;
      private String referenceName;
      private String propertyName;
      private String fileName;
      private String mimeType;

      public String getTargetKind() {
         return targetKind;
      }

      public Binding setTargetKind(String targetKind) {
         this.targetKind = targetKind;
         return this;
      }

      public String getTargetId() {
         return targetId;
      }

      public Binding setTargetId(String targetId) {
         this.targetId = targetId;
         return this;
      }

      public String getReferenceName() {
         return referenceName;
      }

      public Binding setReferenceName(String referenceName) {
         this.referenceName = referenceName;
         return this;
      }

      public String getPropertyName() {
         return propertyName;
      }

      public Binding setPropertyName(String propertyName) {
         this.propertyName = propertyName;
         return this;
      }

      public String getFileName() {
         return fileName;
      }

      public Binding setFileName(String fileName) {
         this.fileName = fileName;
         return this;
      }

      public String getMimeType() {
         return mimeType;
      }

      public Binding setMimeType(String mimeType) {
         this.mimeType = mimeType;
         return this;
      }
   }
}
