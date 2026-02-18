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

package net.ihe.gazelle.maestro.spi.business;

import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.property.PropertyResolver;
import net.ihe.gazelle.maestro.api.business.test.Step;

import java.io.Serial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * StepRun is the execution of a Step with the test context to be able to resolve reference-properties.
 */
public class StepRun extends Step {

   @Serial
   private static final long serialVersionUID = 6529685098267757690L;
   private final Map<String, String> referenceNameByPropertyName;

   /**
    * Creates a new {@code StepRun} based on an existing {@link Step} and the provided context.
    *
    * @param step the step to execute
    * @param context the test context used to resolve reference-properties
    */
   public StepRun(Step step, List<Property> context) {
      this(step, prepareInitializationData(context, step.getProperties()));
   }

   private StepRun(Step step, InitializationData initializationData) {
      super(
            step.getName(),
            step.getType(),
            step.getTimeout(),
            initializationData.resolvedProperties(),
            step.getOutputMappings()
      );
      this.referenceNameByPropertyName = initializationData.referenceNameByPropertyName();
   }

   private static List<Property> aggregateProperties(List<Property> context, List<Property> stepProperties) {
      // we create the map from the context first, so that it can be overridden by the step properties
      Map<String, Property> aggregatedProperties = buildMap(context);
      // Then Step properties are also copied to avoid modification of original properties via shared references
      stepProperties.forEach(property ->
            aggregatedProperties.put(property.getName(), property.copy())
      );
      return new ArrayList<>(aggregatedProperties.values());
   }

   private static List<Property> resolveProperties(final List<Property> properties) {
      PropertyResolver.resolveProperties(properties);
      return properties;
   }

   private static InitializationData prepareInitializationData(List<Property> context, List<Property> stepProperties) {
      List<Property> aggregatedProperties = aggregateProperties(context, stepProperties);
      List<Property> resolvedProperties = resolveProperties(aggregatedProperties);
      Map<String, String> references = extractReferenceNames(aggregatedProperties);
      return new InitializationData(resolvedProperties, references);
   }

   private static Map<String, String> extractReferenceNames(List<Property> properties) {
      Map<String, String> extracted = new HashMap<>();
      if (properties == null) {
         return extracted;
      }
      for (Property property : properties) {
         if (property != null && property.getReferenceName() != null) {
            extracted.put(property.getName(), property.getReferenceName());
         }
      }
      return extracted;
   }

   /**
    * Returns the reference name originally declared for the given property, when available.
    *
    * @param propertyName property to inspect
    * @return the referenced property name, or {@code null} when not a reference
    */
   public String getPropertyReferenceName(String propertyName) {
      return referenceNameByPropertyName.get(propertyName);
   }

   private record InitializationData(List<Property> resolvedProperties, Map<String, String> referenceNameByPropertyName) {
   }

}
