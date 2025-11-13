package net.ihe.gazelle.maestro.simulation.step.record;

public final class ReferenceNames {

   private ReferenceNames() {
      // utility
   }

   public static String resolve(String candidate, String fallback) {
      if (candidate == null || candidate.isBlank()) {
         return fallback;
      }
      return candidate;
   }
}
