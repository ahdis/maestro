package net.ihe.gazelle.maestro.api.business.testreport.dissection;

import net.ihe.gazelle.maestro.api.business.recording.MaestroItem;

import java.util.List;

interface GraphNodeBuilder {

   MaestroItem item();

   List<AttachmentOutputBuilder> attachments();

   List<SubItemOutputBuilder> subItems();

   List<PropertyReplacement> propertyReplacements();
}
