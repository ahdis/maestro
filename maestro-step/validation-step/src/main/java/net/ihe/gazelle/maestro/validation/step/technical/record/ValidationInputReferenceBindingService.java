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
import net.ihe.gazelle.maestro.spi.business.recording.plan.ItemPlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.ReferencePlan;
import net.ihe.gazelle.validation.v2.api.business.Input;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
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
    * Builds a map of report target id -> binding metadata from propertyBinding payload.
    *
    * @param payload parsed binding payload
    * @param acceptedTargetKinds accepted binding kinds
    * @return map of target id to binding metadata
    */
   public Map<String, BindingInfo> toBindingInfoByTargetId(PropertyBindingPayload payload,
                                                           Collection<String> acceptedTargetKinds) {
      Map<String, BindingInfo> bindingInfoByTargetId = new LinkedHashMap<>();
      if (payload == null || payload.getBindings() == null || acceptedTargetKinds == null || acceptedTargetKinds.isEmpty()) {
         return bindingInfoByTargetId;
      }
      Set<String> kinds = Set.copyOf(acceptedTargetKinds);
      for (PropertyBindingPayload.Binding binding : payload.getBindings()) {
         if (isAcceptedBinding(binding, kinds)) {
            bindingInfoByTargetId.put(
                  binding.getTargetId(),
                  new BindingInfo(
                        binding.getReferenceName(),
                        binding.getFileName(),
                        binding.getPropertyName(),
                        binding.getMimeType()
                  )
            );
         }
      }
      return bindingInfoByTargetId;
   }

   private static boolean isAcceptedBinding(PropertyBindingPayload.Binding binding, Set<String> acceptedKinds) {
      if (binding == null) {
         return false;
      }
      if (binding.getTargetId() == null || binding.getTargetId().isBlank()) {
         return false;
      }
      return acceptedKinds.contains(binding.getTargetKind());
   }

   /**
    * Registers persistence of validation report inputs, preferring external references when property bindings exist.
    *
    * @param stepOutputPlan step output plan used to subscribe to resolved references
    * @param validationReportPlan validation report plan to mutate
    * @param validationReport report business object
    * @param bindingInfoByTargetId target id -> source binding metadata
    * @param attachmentType attachment type to use for fallback uploads
    */
   public void registerInputPersistence(StepOutputPlan stepOutputPlan,
                                        ItemPlan<ValidationReport> validationReportPlan,
                                        ValidationReport validationReport,
                                        Map<String, BindingInfo> bindingInfoByTargetId,
                                        String attachmentType) {
      if (validationReport == null) {
         return;
      }
      Map<String, BindingInfo> bindings = bindingInfoByTargetId == null ? Map.of() : bindingInfoByTargetId;
      validationReport.setInputs(synthesizeInputsFromBindings(bindings));
      if (validationReport.getInputs() == null || validationReport.getInputs().isEmpty()) {
         return;
      }
      for (Input input : validationReport.getInputs()) {
         String inputId = resolveInputName(input.getId());
         BindingInfo bindingInfo = bindings.get(inputId);
         String referenceName = bindingInfo != null ? bindingInfo.referenceName() : null;
         if (referenceName == null || referenceName.isBlank()) {
            continue;
         }
         stepOutputPlan.onFutureReferenceResolved(referenceName,
               (refName, referenceId) -> {
                  mutateInputReference(input, referenceId);
                  addAttachmentReferenceIfMissing(validationReportPlan, inputId, referenceId, attachmentType);
               });
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

   private List<Input> synthesizeInputsFromBindings(Map<String, BindingInfo> bindingInfoByTargetId) {
      List<Input> inputs = new ArrayList<>();
      if (bindingInfoByTargetId == null || bindingInfoByTargetId.isEmpty()) {
         return inputs;
      }
      for (String targetId : bindingInfoByTargetId.keySet()) {
         inputs.add(new Input().setId(targetId));
      }
      return inputs;
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

   /**
    * Internal binding metadata extracted from propertyBinding output.
    *
    * @param referenceName resolved or direct source reference name
    * @param fileName source filename when available
    * @param propertyName source property name
    * @param mimeType source mime type
    */
   public record BindingInfo(String referenceName, String fileName, String propertyName, String mimeType) {
   }
}
