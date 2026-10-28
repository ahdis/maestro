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

package net.ihe.gazelle.maestro.userinteraction.step.technical.record;

import net.ihe.gazelle.maestro.api.business.property.ByteArrayItemProperty;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.spi.business.recording.StepOutputPlan;
import net.ihe.gazelle.maestro.spi.business.recording.StepOutputPlanDissector;
import net.ihe.gazelle.maestro.spi.business.recording.plan.AttachmentPlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.ReferencePlan;


/** Records uploaded files once and uses the same attachment in report inputs and outputs. */
public class UserInteractionStepOutputDissector implements StepOutputPlanDissector {
    @Override
    public void dissect(StepOutputPlan plan) {
        if (plan == null || plan.getStepRunReport() == null) {
            return;
        }
        for (Property output : plan.getStepRunReport().getOutputs()) {
            if (output instanceof ByteArrayProperty file && !file.isReference()) {
                String name = file.getName();
                ReferencePlan attachment = attachmentPlan(file, name);
                if (attachment == null) {
                    continue;
                }
                plan.addReferencePlan(attachment,
                        (report, _, attachmentId) -> {
                            StepOutputPlan.replaceOutput(name, _ -> attachmentReference(file, attachmentId), report);
                            report.setInputs(report.getInputs().stream().map(input ->
                                    name.equals(input.getName()) && input instanceof ByteArrayProperty
                                            ? attachmentReference(file, attachmentId) : input).toList());
                        });
            }
        }
    }
    private ReferencePlan attachmentPlan(ByteArrayProperty file, String referenceName) {
        if (file instanceof ByteArrayItemProperty existing && existing.getReference() != null
                && existing.getReference().startsWith("/attachments/")) {
            String identifier = existing.getReference().substring("/attachments/".length());
            if (!identifier.isBlank()) {
                return ReferencePlan.forAttachmentId(referenceName, "BYTE_ARRAY", identifier);
            }
        }
        return file.getValue() == null ? null : ReferencePlan.forAttachment(referenceName,
                new AttachmentPlan("BYTE_ARRAY", file.getValue(), file.getFileName()));
    }

    private ByteArrayItemProperty attachmentReference(ByteArrayProperty file, String attachmentId) {
        ByteArrayItemProperty reference = new ByteArrayItemProperty(file).setReference("/attachments/" + attachmentId);
        reference.setValue(null);
        return reference;
    }

}
