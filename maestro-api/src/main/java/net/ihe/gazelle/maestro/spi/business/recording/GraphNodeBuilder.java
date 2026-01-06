package net.ihe.gazelle.maestro.spi.business.recording;

import java.util.List;

interface GraphNodeBuilder {

   MaestroItem item();

   List<AttachmentOutputBuilder> attachments();

   List<SubItemOutputBuilder> subItems();

   List<PropertyReplacement> propertyReplacements();

   ContentMutator contentMutator();
}
