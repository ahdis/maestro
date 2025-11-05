package net.ihe.gazelle.maestro.api.business.property;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PropertyResolverTest {

   @Test
   void shouldResolveDirectReference() {
      Property source = new StringProperty("source", "value");
      Property reference = new StringProperty("reference", "${source}");

      PropertyResolver.resolveProperties(List.of(source, reference));

      assertEquals("value", reference.getValue());
   }

   @Test
   void shouldResolveChainedReferences() {
      Property base = new StringProperty("base", "resolved");
      Property middle = new StringProperty("middle", "${base}");
      Property top = new StringProperty("top", "${middle}");

      PropertyResolver.resolveProperties(List.of(base, middle, top));

      assertEquals("resolved", middle.getValue());
      assertEquals("resolved", top.getValue());
   }

   @Test
   void shouldDetectCircularReferences() {
      Property first = new StringProperty("first", "${second}");
      Property second = new StringProperty("second", "${first}");

      assertThrows(IllegalStateException.class, () ->
            PropertyResolver.resolveProperties(List.of(first, second))
      );
   }

   @Test
   void shouldDetectMissingReference() {
      Property orphan = new StringProperty("orphan", "${missing}");

      assertThrows(MissingPropertyException.class, () ->
            PropertyResolver.resolveProperties(List.of(orphan))
      );
   }

   @Test
   void shouldDetectTypeMismatch() {
      Property integerProperty = new IntegerProperty("number", 42);
      Property stringReference = new StringProperty("stringRef", "${number}");

      assertThrows(PropertyTypeMismatchException.class, () ->
            PropertyResolver.resolveProperties(List.of(integerProperty, stringReference))
      );
   }
}

