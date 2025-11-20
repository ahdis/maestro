package net.ihe.gazelle.maestro.api.business.testreport.dissection;

import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.recording.MaestroAttachment;
import net.ihe.gazelle.maestro.api.business.recording.MaestroItem;
import net.ihe.gazelle.maestro.api.business.recording.MaestroRefType;
import net.ihe.gazelle.maestro.api.business.recording.MaestroReference;
import net.ihe.gazelle.security.business.acl.AccessControlList;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StepOutputProjectionBuilderTest {

   @Test
   void buildsProjectionWithRelationsAttachmentsAndMutations() {
      StepOutputProjectionBuilder graph = new StepOutputProjectionBuilder();
      ItemOutputBuilder root = graph.addItem(new SimpleItem("ROOT", "{\"value\":1}"));
      List<net.ihe.gazelle.maestro.api.business.property.Property> outputs = List.of(
            new ByteArrayProperty("report", new byte[]{1}));

      root.addReferenceBinding(new ReferenceBinding("REPORT", "TYPE", MaestroRefType.ITEM_ID));
      root.addPropertyReplacement("report", outputs, (itemId, itemUrl) -> new StringProperty("report", itemUrl));
      root.addAttachment(new AttachmentOutputBuilder(new SimpleAttachment("root.log"))
            .withReference("root-attachment", "LOG"));

      SubItemOutputBuilder child = root.addSubItem(
            new SimpleItem("CHILD", "{\"value\":2}"),
            "child-link",
            "CHILD_TYPE",
            MaestroRefType.ITEM_ID);
      child.addAttachment(new AttachmentOutputBuilder(new SimpleAttachment("child.log"))
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
      graph.addReportReference(new SimpleReference("report-link", "REPORT", MaestroRefType.ITEM_ID, "itm-1"));

      StepOutputProjection projection = graph.build();

      assertEquals(1, projection.reportReferences().size());
      assertEquals("report-link", projection.reportReferences().getFirst().getName());
   }

   @Test
   void attachmentBuilderRequiresReferenceBeforeBuild() {
      AttachmentOutputBuilder builder = new AttachmentOutputBuilder(new SimpleAttachment("dangling.bin"));
      assertThrows(IllegalStateException.class, builder::build);
   }

   @Test
   void rejectsNegativePropertyReplacementIndex() {
      StepOutputProjectionBuilder graph = new StepOutputProjectionBuilder();
      ItemOutputBuilder root = graph.addItem(new SimpleItem("ROOT", "{}"));
      StringProperty stringProperty = new StringProperty("prop", "value");
      List<Property> outputs = List.of();
      assertThrows(IllegalArgumentException.class,
            () -> root.addPropertyReplacement("missing", outputs, (id, url) -> stringProperty));

      SubItemOutputBuilder child = root.addSubItem(new SimpleItem("CHILD", "{}"), "link", "TYPE");
      assertThrows(IllegalArgumentException.class,
            () -> child.addPropertyReplacement(-2, (id, url) -> stringProperty));
   }

   private record SimpleItem(String type, String content) implements MaestroItem {

      @Override
         public AccessControlList getAccessControlList() {
         return null;
         }

         @Override
         public List<? extends MaestroReference> getReferences() {
            return List.of();
         }

         @Override
         public Map<String, String> getAdditionalParameters() {
            return Map.of();
         }
      }

   private record SimpleAttachment(String filename) implements MaestroAttachment {

      @Override
         public byte[] getContent() {
            return filename.getBytes();
         }

         @Override
         public String getType() {
            return "attachment";
         }
      }

   private record SimpleReference(String name,
                                  String type,
                                  MaestroRefType refType,
                                  String value) implements MaestroReference {
      @Override
      public String getName() {
         return name;
      }

      @Override
      public String getType() {
         return type;
      }

      @Override
      public MaestroRefType getRefType() {
         return refType;
      }

      @Override
      public String getValue() {
         return value;
      }
   }
}
