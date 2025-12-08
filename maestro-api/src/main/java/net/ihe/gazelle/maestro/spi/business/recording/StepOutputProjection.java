package net.ihe.gazelle.maestro.spi.business.recording;

import java.util.List;
import java.util.Objects;

/**
 * Result produced by a {@link StepOutputDissector}.
 *
 * @param items             ordered list of descriptors describing the items to persist
 * @param attachments       binary payloads that must become attachments
 * @param propertyMutations instructions to update step outputs once item ids are known
 * @param reportReferences  references that must be appended to the enclosing TestReport item
 */
public record StepOutputProjection(
      List<ItemPlan> items,
      List<AttachmentPlan> attachments,
      List<PropertyMutation> propertyMutations,
      List<MaestroReference> reportReferences
) {

   public StepOutputProjection {
      items = List.copyOf(Objects.requireNonNullElse(items, List.of()));
      attachments = List.copyOf(Objects.requireNonNullElse(attachments, List.of()));
      propertyMutations = List.copyOf(Objects.requireNonNullElse(propertyMutations, List.of()));
      reportReferences = List.copyOf(Objects.requireNonNullElse(reportReferences, List.of()));
   }

   public static StepOutputProjection empty() {
      return new StepOutputProjection(List.of(), List.of(), List.of(), List.of());
   }
}
