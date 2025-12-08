package net.ihe.gazelle.maestro.spi.business.recording;

/**
 * Instruction allowing the persistence layer to replace a step output with a
 * reference to a freshly persisted item.
 *
 * @param outputIndex     index of the output to replace
 * @param sourceItemIndex index of the {@link ItemPlan} providing the item identifier
 * @param factory         factory creating the replacement property
 */
public record PropertyMutation(
      int outputIndex,
      int sourceItemIndex,
      PropertyReferenceFactory factory
) {

   public PropertyMutation {
      if (outputIndex < 0) {
         throw new IllegalArgumentException("outputIndex must be >= 0");
      }
      if (sourceItemIndex < 0) {
         throw new IllegalArgumentException("sourceItemIndex must be >= 0");
      }
      if (factory == null) {
         throw new IllegalArgumentException("factory must not be null");
      }
   }
}
