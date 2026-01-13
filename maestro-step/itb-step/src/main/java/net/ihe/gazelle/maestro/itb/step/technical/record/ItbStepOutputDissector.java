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

package net.ihe.gazelle.maestro.itb.step.technical.record;

import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.itb.step.business.ItbStepDefinition;
import net.ihe.gazelle.maestro.spi.business.recording.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The ItbStepOutputDissector class is an implementation of the {@link StepOutputDissector} interface.
 */
public class ItbStepOutputDissector implements StepOutputDissector {

    private static final String ITB_XML_REPORT = "ITB_XML_REPORT";
    private static final String ITB_PDF_REPORT = "ITB_PDF_REPORT";
    private static final String ITB_LOGS = "ITB_LOGS";

    /**
     * Default constructor.
     */
    public ItbStepOutputDissector() {
        // Empty
    }

    @Override
    public StepOutputProjection dissect(StepOutputContext context) {
        if (context == null || context.stepRunReport() == null) {
            return StepOutputProjection.empty();
        }
        StepRunReport stepRunReport = context.stepRunReport();
        List<Property> outputs = stepRunReport.getOutputs();
        if (outputs == null || outputs.isEmpty()) {
            return StepOutputProjection.empty();
        }

        List<AttachmentOutputBuilder> attachments = extractItbOutputAttachments(outputs);

        return null;
    }

    private List<AttachmentOutputBuilder> extractItbOutputAttachments(List<Property> outputs) {
        List<AttachmentOutputBuilder> plans = new ArrayList<>();
        ByteArrayProperty xmlReportProperty = (ByteArrayProperty) outputs.stream()
                .filter(output -> ItbStepDefinition.XML_REPORT.equals(output.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("XML report output property not found"));

        ByteArrayProperty pdfReportProperty = (ByteArrayProperty) outputs.stream()
                .filter(output -> ItbStepDefinition.PDF_REPORT.equals(output.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("PDF report output property not found"));

        ByteArrayProperty logsProperty = (ByteArrayProperty) outputs.stream()
                .filter(output -> ItbStepDefinition.LOGS.equals(output.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Logs output property not found"));

        plans.add(createAttachment(ITB_XML_REPORT, xmlReportProperty));
        plans.add(createAttachment(ITB_PDF_REPORT, pdfReportProperty));
        plans.add(createAttachment(ITB_LOGS, logsProperty));
        return plans;
    }


    private AttachmentOutputBuilder createAttachment(String type, ByteArrayProperty property) {
        MaestroAttachment attachment = new MaestroAttachment(
              property.getValue(),
              type,
              property.getFileName()
        );
        return new AttachmentOutputBuilder(attachment)
                .withReference(type, type);
    }
}
