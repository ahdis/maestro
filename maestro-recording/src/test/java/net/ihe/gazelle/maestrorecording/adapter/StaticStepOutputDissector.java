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

package net.ihe.gazelle.maestrorecording.adapter;

import net.ihe.gazelle.maestro.api.business.property.ByteArrayItemProperty;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.spi.business.recording.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

final class StaticStepOutputDissector implements StepOutputDissector {

    static final String ROOT_OUTPUT_NAME = "static-root-output";
    static final String SIBLING_OUTPUT_NAME = "static-sibling-output";
    static final String ATTACHMENT_OUTPUT_NAME = "static-attachment-output";

    static final String ROOT_ITEM_TYPE = "STATIC_ROOT";
    static final String CHILD_ITEM_TYPE = "STATIC_CHILD";
    static final String GRANDCHILD_ITEM_TYPE = "STATIC_GRAND_CHILD";
    static final String SIBLING_ITEM_TYPE = "STATIC_SIBLING";

    static final String CHILD_REFERENCE_NAME = "child-link";
    static final String GRANDCHILD_REFERENCE_NAME = "grandchild-link";
    static final String SIBLING_REFERENCE_NAME = "sibling-link";

    static final String REPORT_REFERENCE_NAME = "static-root-binding";
    static final String REPORT_REFERENCE_TYPE = "STATIC_STEP_REPORT";

    static final String ATTACHMENT_REFERENCE_NAME = "binary-attachment";
    static final String DETACHED_REFERENCE_NAME = "detached-log";
    static final String ATTACHMENT_TYPE = "STATIC_ATTACHMENT";

    private final StepOutputProjection projection;

    StaticStepOutputDissector() {
        this(null);
    }

    StaticStepOutputDissector(StepOutputProjection projection) {
        this.projection = projection;
    }

    @Override
    public StepOutputProjection dissect(StepOutputContext context) {
        if (projection != null) {
            return projection;
        }
        if (context == null || context.stepRunReport() == null) {
            return StepOutputProjection.empty();
        }
        return buildProjection(context.stepRunReport().getOutputs());
    }

    private static StepOutputProjection buildProjection(List<Property> outputs) {
        StepOutputProjectionBuilder graph = new StepOutputProjectionBuilder();

        ItemOutputBuilder root = graph.addItem(item(ROOT_ITEM_TYPE, "{\"level\":0}"));
        root.addReferenceBinding(new ReferenceBinding(
                REPORT_REFERENCE_NAME,
                REPORT_REFERENCE_TYPE,
                MaestroRefType.ITEM_ID
        ));
        root.addPropertyReplacement(ROOT_OUTPUT_NAME, outputs, referenceProperty(ROOT_OUTPUT_NAME));

        SubItemOutputBuilder child = root.addSubItem(
                item(CHILD_ITEM_TYPE, "{\"level\":1}"),
                CHILD_REFERENCE_NAME,
                CHILD_ITEM_TYPE,
                MaestroRefType.ITEM_ID
        );

        child.addSubItem(
                item(GRANDCHILD_ITEM_TYPE, "{\"level\":2}"),
                GRANDCHILD_REFERENCE_NAME,
                GRANDCHILD_ITEM_TYPE,
                MaestroRefType.ITEM_ID
        );

        SubItemOutputBuilder sibling = root.addSubItem(
                item(SIBLING_ITEM_TYPE, "{\"level\":1}"),
                SIBLING_REFERENCE_NAME,
                SIBLING_ITEM_TYPE,
                MaestroRefType.ITEM_ID
        );
        sibling.addPropertyReplacement(1, referenceProperty(SIBLING_OUTPUT_NAME));

        root.addAttachment(new AttachmentOutputBuilder(
                attachment("binary-output.bin", "application/octet-stream", "root-binary"))
                .withReference(ATTACHMENT_REFERENCE_NAME, ATTACHMENT_TYPE)
                .replaceOutput(2, (attachmentId, attachmentUrl) -> new ByteArrayItemProperty(
                        ATTACHMENT_OUTPUT_NAME, null).setReference("/attachments/" + attachmentId)));

        root.addAttachment(new AttachmentOutputBuilder(
                attachment("logs.txt", "text/plain", "log"))
                .withReference(DETACHED_REFERENCE_NAME, ATTACHMENT_TYPE));

        return graph.build();
    }

    private static MaestroItem item(String type, String content) {
        return new MaestroItem(
              null,
              type,
              content,
              new ArrayList<>(),
              new HashMap<>()
        );
    }

    private static MaestroAttachment attachment(String filename, String type, String marker) {
        return new MaestroAttachment(
              marker.getBytes(StandardCharsets.UTF_8),
              type,
              filename
        );
    }

    private static PropertyReferenceFactory referenceProperty(String propertyName) {
        return (itemId, itemUrl) -> new ByteArrayItemProperty(propertyName, null).setReference(itemUrl);
    }
}
