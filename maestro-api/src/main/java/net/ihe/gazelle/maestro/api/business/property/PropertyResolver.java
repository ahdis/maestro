package net.ihe.gazelle.maestro.api.business.property;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A utility class for resolving property dependencies in a collection of {@code Property} objects.
 * Properties can hold direct values or reference other properties. The {@code resolveProperties} method
 * ensures all references are correctly resolved to their corresponding values while maintaining type safety
 * and detecting circular dependencies.
 */
public class PropertyResolver {

   private PropertyResolver() {
      // private constructor to prevent instantiation
   }

   /**
    * Resolves all ReferenceValue instances by linking them with directValues from the provided list of properties.
    *
    * @param properties the list of properties to resolve.
    *
    * @throws IllegalStateException    if a circular reference is detected
    * @throws MissingPropertyException if a referenced property is not found
    * @throws PropertyTypeMismatchException if a property references another property of a different type
    */
   public static void resolveProperties(Collection<Property> properties) {
      for (Property property : properties) {
         if (property.isReference()) {
            supplyValue(property, properties, new HashSet<>());
         }
      }
   }

   private static void supplyValue(Property property,
                                   Collection<Property> properties,
                                   Set<String> visited) {
      ReferenceValue referenceValue = (ReferenceValue) property.getValueHolder();
      if (!referenceValue.isSupplied()) {
         String refName = referenceValue.getReference();
         assertNotALoop(visited, refName);
         Property referencedProperty = getPropertyByName(properties, refName);
         assertTypeMatch(property, referencedProperty);
         // once we identify the referenced property, we immediatly resolve it to detect if there is a circular
         // reference
         if (referencedProperty.isReference()) {
            supplyValue(referencedProperty, properties, visited);
         }
         referenceValue.supply(referencedProperty::getValue);
         visited.remove(refName);
      }
   }

   private static Property getPropertyByName(Collection<Property> properties, String name) {
      return properties.stream()
            .filter(p -> p.getName().equals(name)).findFirst()
            .orElseThrow(() -> new MissingPropertyException("No property found with name: " + name));
   }

   private static void assertTypeMatch(Property property, Property referencedProperty) {
      if(!property.getClass().equals(referencedProperty.getClass())) {
         throw new PropertyTypeMismatchException("Property '" + property.getName() + "' of type " +
                                                 property.getClass().getSimpleName() +
                                                 " cannot reference property '" + referencedProperty.getName() + "' of type " +
                                                 referencedProperty.getClass().getSimpleName());
      }
   }

   private static void assertNotALoop(Set<String> visited, String refName) {
      if (!visited.add(refName)) {
         throw new IllegalStateException("Property references are forming a loop " + toString(visited, refName));
      }
   }

   private static String toString(Set<String> elements, String repeatingElement) {
      return "[" + String.join(", ", elements) + ", " + repeatingElement + "]";
   }

}
