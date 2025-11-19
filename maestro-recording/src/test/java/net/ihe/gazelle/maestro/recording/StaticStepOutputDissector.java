package net.ihe.gazelle.maestro.recording;

import net.ihe.gazelle.maestro.api.business.recording.MaestroRefType;
import net.ihe.gazelle.maestro.api.business.testreport.ByteArrayItemProperty;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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
        this(buildProjection());
    }

    StaticStepOutputDissector(StepOutputProjection projection) {
        this.projection = projection;
    }

    @Override
    public StepOutputProjection dissect(StepOutputContext context) {
        return projection;
    }

    private static StepOutputProjection buildProjection() {
        StepOutputGraphBuilder graph = new StepOutputGraphBuilder();

        ItemOutputBuilder root = graph.addItem(item(ROOT_ITEM_TYPE, "{\"level\":0}"));
        root.addReferenceBinding(new ReferenceBinding(
                REPORT_REFERENCE_NAME,
                REPORT_REFERENCE_TYPE,
                MaestroRefType.ITEM_ID
        ));
        root.addPropertyReplacement(0, referenceProperty(ROOT_OUTPUT_NAME));

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

    private static DatahouseMaestroItem item(String type, String content) {
        return new DatahouseMaestroItem()
                .setType(type)
                .setContent(content)
                .setReferences(new ArrayList<>());
    }

    private static DatahouseMaestroAttachment attachment(String filename, String type, String marker) {
        return new DatahouseMaestroAttachment()
                .setFilename(filename)
                .setType(type)
                .setContent(marker.getBytes(StandardCharsets.UTF_8));
    }

    private static PropertyReferenceFactory referenceProperty(String propertyName) {
        return (itemId, itemUrl) -> new ByteArrayItemProperty(propertyName, null).setReference(itemUrl);
    }
}
