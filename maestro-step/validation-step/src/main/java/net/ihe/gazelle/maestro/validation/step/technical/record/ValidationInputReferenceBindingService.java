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

package net.ihe.gazelle.maestro.validation.step.technical.record;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.property.PropertyBindingPayload;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.spi.business.recording.StepOutputPlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.AttachmentPlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.ItemPlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.ReferencePlan;
import net.ihe.gazelle.validation.v2.api.business.Input;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Shared service responsible for translating propertyBinding metadata into validation input persistence rules.
 */
public class ValidationInputReferenceBindingService {

   private static final String DEFAULT_INPUT_NAME = "validation-input";
   private final ObjectMapper objectMapper;

   /**
    * Creates the service with a default mapper.
    */
   public ValidationInputReferenceBindingService() {
      this(new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false));
   }

   /**
    * Creates the service with an externally provided mapper.
    *
    * @param objectMapper object mapper used to deserialize propertyBinding payloads
    */
   public ValidationInputReferenceBindingService(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
   }

   /**
    * Consumes the ephemeral propertyBinding output from a step report.
    *
    * @param stepRunReport step report
    * @return parsed payload, or {@code null} when absent/invalid
    */
   public PropertyBindingPayload consumePropertyBinding(StepRunReport stepRunReport) {
      if (stepRunReport == null || stepRunReport.getOutputs() == null) {
         return null;
      }
      List<Property> filteredOutputs = new ArrayList<>();
      PropertyBindingPayload payload = null;
      for (Property output : stepRunReport.getOutputs()) {
         if (PropertyBindingPayload.PROPERTY_NAME.equals(output.getName()) && output instanceof StringProperty stringProperty) {
            payload = deserializePropertyBinding(stringProperty.getValue());
            continue;
         }
         filteredOutputs.add(output);
      }
      stepRunReport.setOutputs(filteredOutputs);
      return payload;
   }

   /**
    * Builds a map of report target id -> source reference name from propertyBinding payload.
    *
    * @param payload parsed binding payload
    * @param acceptedTargetKinds accepted binding kinds
    * @return map of target id to reference name
    */
   public Map<String, String> toReferenceByTargetId(PropertyBindingPayload payload, Collection<String> acceptedTargetKinds) {
      Map<String, String> referenceByTargetId = new HashMap<>();
      if (payload == null || payload.getBindings() == null || acceptedTargetKinds == null || acceptedTargetKinds.isEmpty()) {
         return referenceByTargetId;
      }
      Set<String> kinds = Set.copyOf(acceptedTargetKinds);
      for (PropertyBindingPayload.Binding binding : payload.getBindings()) {
         if (!kinds.contains(binding.getTargetKind())) {
            continue;
         }
         if (binding.getTargetId() == null || binding.getTargetId().isBlank()) {
            continue;
         }
         if (binding.getReferenceName() == null || binding.getReferenceName().isBlank()) {
            continue;
         }
         referenceByTargetId.put(binding.getTargetId(), binding.getReferenceName());
      }
      return referenceByTargetId;
   }

   /**
    * Registers persistence of validation report inputs, preferring external references when property bindings exist.
    *
    * @param stepOutputPlan step output plan used to subscribe to resolved references
    * @param validationReportPlan validation report plan to mutate
    * @param validationReport report business object
    * @param referenceByTargetId target id -> source reference map
    * @param attachmentType attachment type to use for fallback uploads
    */
   public void registerInputPersistence(StepOutputPlan stepOutputPlan,
                                        ItemPlan<ValidationReport> validationReportPlan,
                                        ValidationReport validationReport,
                                        Map<String, String> referenceByTargetId,
                                        String attachmentType) {
      ensureInputsFromBindings(validationReport, referenceByTargetId);
      if (validationReport.getInputs() == null || validationReport.getInputs().isEmpty()) {
         return;
      }
      for (Input input : validationReport.getInputs()) {
         String inputId = resolveInputName(input.getId());
         String referenceName = referenceByTargetId.get(inputId);
         if (referenceName != null && !referenceName.isBlank()) {
            stepOutputPlan.onFutureReferenceResolved(referenceName,
                  (refName, referenceId) -> {
                     mutateInputReference(input, referenceId);
                     addAttachmentReferenceIfMissing(validationReportPlan, inputId, referenceId, attachmentType);
                  });
            continue;
         }
         if (input.getContent() != null) {
            registerInputAttachmentFallback(validationReportPlan, input, inputId, attachmentType);
         }
      }
   }

   private PropertyBindingPayload deserializePropertyBinding(String payload) {
      if (payload == null || payload.isBlank()) {
         return null;
      }
      try {
         PropertyBindingPayload parsed = objectMapper.readValue(payload, PropertyBindingPayload.class);
         if (parsed.getSchemaVersion() != PropertyBindingPayload.SCHEMA_VERSION) {
            return null;
         }
         return parsed;
      } catch (IOException e) {
         return null;
      }
   }

   private void ensureInputsFromBindings(ValidationReport validationReport, Map<String, String> referenceByTargetId) {
      if (referenceByTargetId == null || referenceByTargetId.isEmpty()) {
         return;
      }
      if (validationReport.getInputs() == null) {
         validationReport.setInputs(new ArrayList<>());
      }
      Map<String, Input> inputsById = new HashMap<>();
      for (Input existing : validationReport.getInputs()) {
         inputsById.put(resolveInputName(existing.getId()), existing);
      }
      for (String targetId : referenceByTargetId.keySet()) {
         if (!inputsById.containsKey(targetId)) {
            validationReport.addInput(new Input().setId(targetId));
         }
      }
   }

   private void registerInputAttachmentFallback(ItemPlan<ValidationReport> validationReportPlan,
                                                Input input,
                                                String inputId,
                                                String attachmentType) {
      validationReportPlan.addFutureReferencePlan(
            ReferencePlan.forAttachment(
                  inputId,
                  new AttachmentPlan(attachmentType, input.getContent(), input.getId())
            ),
            (report, refName, attachmentId) -> {
               for (Input candidate : report.getInputs()) {
                  if (resolveInputName(candidate.getId()).equals(refName)) {
                     mutateInputReference(candidate, attachmentId);
                  }
               }
            }
      );
      input.setContent(null);
   }

   private void addAttachmentReferenceIfMissing(ItemPlan<ValidationReport> validationReportPlan,
                                                String inputId,
                                                String attachmentId,
                                                String attachmentType) {
      boolean alreadyPresent = validationReportPlan.futureReferencePlans().stream()
            .anyMatch(plan -> plan.referencePlan().getName().equals(inputId));
      if (alreadyPresent) {
         return;
      }
      validationReportPlan.addFutureReferencePlan(
            ReferencePlan.forAttachmentId(inputId, attachmentType, attachmentId),
            null
      );
   }

   private void mutateInputReference(Input input, String attachmentId) {
      input.setItemId(attachmentId);
      input.setLocation("/attachments/" + attachmentId);
      input.setContent(null);
   }

   private String resolveInputName(String candidate) {
      if (candidate == null || candidate.isBlank()) {
         return DEFAULT_INPUT_NAME;
      }
      return candidate;
   }
}
