package net.ihe.gazelle.maestro.engine.business.record;

import com.kereval.gazelle.datahouse.api.business.record.Attachment;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.testreport.ByteArrayItemProperty;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.AttachmentPlan;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.PropertyReferenceFactory;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.StepOutputContext;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.StepOutputDissector;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.StepOutputProjection;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Default dissector used when no step specific implementation is provided.
 * Every output is uploaded as a binary attachment.
 */
public class FallbackAttachmentStepOutputDissector implements StepOutputDissector {

   private static final String DEFAULT_MIME_TYPE = "application/octet-stream";

   @Override
   public StepOutputProjection dissect(StepOutputContext context) {
      if (context == null || context.stepRunReport() == null) {
         return StepOutputProjection.empty();
      }
      List<Property> outputs = context.stepRunReport().getOutputs();
      if (outputs == null || outputs.isEmpty()) {
         return StepOutputProjection.empty();
      }

      List<AttachmentPlan> attachments = new ArrayList<>();
      for (int index = 0; index < outputs.size(); index++) {
         Property property = outputs.get(index);
         if (property == null) {
            continue;
         }
         Attachment attachment = buildAttachment(property);
         PropertyReferenceFactory propertyFactory = (id, url) -> {
            ByteArrayItemProperty replacement = new ByteArrayItemProperty(property.getName(), null);
            if (property instanceof ByteArrayProperty byteProp) {
               replacement.setFileName(byteProp.getFileName());
               replacement.setMimeType(byteProp.getMimeType());
            }
            return replacement.setReference(url);
         };
         attachments.add(new AttachmentPlan(
               attachment,
               index,
               propertyFactory,
               property.getName(),
               detectMimeType(property))
         );
      }

      return attachments.isEmpty()
            ? StepOutputProjection.empty()
            : new StepOutputProjection(List.of(), attachments, List.of(), List.of());
   }

   private Attachment buildAttachment(Property property) {
      Attachment attachment = new Attachment()
            .setContent(extractContent(property))
            .setType(detectMimeType(property))
            .setFilename(property instanceof ByteArrayProperty byteProp ? byteProp.getFileName() : null);
      return attachment;
   }

   private byte[] extractContent(Property property) {
      if (property instanceof ByteArrayProperty byteProp) {
         byte[] value = byteProp.getValue();
         return value != null ? value : new byte[0];
      }
      Object value = property.getValue();
      if (value == null) {
         return new byte[0];
      }
      if (value instanceof byte[] bytes) {
         return bytes;
      }
      return value.toString().getBytes(StandardCharsets.UTF_8);
   }

   private String detectMimeType(Property property) {
      if (property instanceof ByteArrayProperty byteProp && byteProp.getMimeType() != null && !byteProp.getMimeType().isBlank()) {
         return byteProp.getMimeType();
      }
      return DEFAULT_MIME_TYPE;
   }
}
