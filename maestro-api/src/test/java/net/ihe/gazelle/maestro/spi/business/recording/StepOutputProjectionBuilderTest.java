package net.ihe.gazelle.maestro.spi.business.recording;

import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StepOutputProjectionBuilderTest {

   @Test
   void buildsProjectionWithRelationsAttachmentsAndMutations() {
      StepOutputProjectionBuilder graph = new StepOutputProjectionBuilder();
      ItemOutputBuilder root = graph.addItem(getSimpleItem("ROOT", "{\"value\":1}"));
      List<net.ihe.gazelle.maestro.api.business.property.Property> outputs = List.of(
            new ByteArrayProperty("report", new byte[]{1}));

      root.addReferenceBinding(new ReferenceBinding("REPORT", "TYPE", MaestroRefType.ITEM_ID));
      root.addPropertyReplacement("report", outputs, (itemId, itemUrl) -> new StringProperty("report", itemUrl));
      root.addAttachment(new AttachmentOutputBuilder(getSimpleAttachment("root.log"))
            .withReference("root-attachment", "LOG"));

      SubItemOutputBuilder child = root.addSubItem(
            getSimpleItem("CHILD", "{\"value\":2}"),
            "child-link",
            "CHILD_TYPE",
            MaestroRefType.ITEM_ID);
      child.addAttachment(new AttachmentOutputBuilder(getSimpleAttachment("child.log"))
            .withReference("child-attachment", "LOG"));

      StepOutputProjection projection = graph.build();

      assertEquals(2, projection.items().size(), "Child must appear before the root to allow relation wiring");
      ItemPlan childPlan = projection.items().getFirst();
      assertEquals("CHILD", childPlan.item().type());
      assertEquals(1, childPlan.relationBindings().size());
      ItemRelationBinding relation = childPlan.relationBindings().getFirst();
      assertEquals(1, relation.referencingItemIndex());
      assertEquals("child-link", relation.referenceName());

      ItemPlan rootPlan = projection.items().get(1);
      assertEquals("ROOT", rootPlan.item().type());
      assertEquals(1, rootPlan.reportBindings().size());
      assertEquals("REPORT", rootPlan.reportBindings().getFirst().referenceName());

      assertEquals(1, projection.propertyMutations().size());
      assertEquals(1, projection.propertyMutations().getFirst().sourceItemIndex());

      assertEquals(List.of("child-attachment", "root-attachment"),
            projection.attachments().stream().map(AttachmentPlan::referenceName).toList());
   }

   @Test
   void addsReportReferencesToProjection() {
      StepOutputProjectionBuilder graph = new StepOutputProjectionBuilder();
      graph.addReportReference(getSimpleReference("report-link", "REPORT", MaestroRefType.ITEM_ID, "itm-1"));

      StepOutputProjection projection = graph.build();

      assertEquals(1, projection.reportReferences().size());
      assertEquals("report-link", projection.reportReferences().getFirst().name());
   }

   @Test
   void attachmentBuilderRequiresReferenceBeforeBuild() {
      AttachmentOutputBuilder builder = new AttachmentOutputBuilder(getSimpleAttachment("dangling.bin"));
      assertThrows(IllegalStateException.class, builder::build);
   }

   @Test
   void rejectsNegativePropertyReplacementIndex() {
      StepOutputProjectionBuilder graph = new StepOutputProjectionBuilder();
      ItemOutputBuilder root = graph.addItem(getSimpleItem("ROOT", "{}"));
      StringProperty stringProperty = new StringProperty("prop", "value");
      List<Property> outputs = List.of();
      assertThrows(IllegalArgumentException.class,
            () -> root.addPropertyReplacement("missing", outputs, (id, url) -> stringProperty));

      SubItemOutputBuilder child = root.addSubItem(getSimpleItem("CHILD", "{}"), "link", "TYPE");
      assertThrows(IllegalArgumentException.class,
            () -> child.addPropertyReplacement(-2, (id, url) -> stringProperty));
   }

   private MaestroItem getSimpleItem(String type, String content) {
      return new MaestroItem(null, type, content, new ArrayList<>(), new HashMap<>());
   }

   private MaestroAttachment getSimpleAttachment(String filename) {
      return new MaestroAttachment(filename.getBytes(), "attachment", filename);
   }

   private MaestroReference getSimpleReference(String name,
                                                String type,
                                                MaestroRefType refType,
                                                String value) {
      return new MaestroReference(type, name, refType, value);
   }
}
