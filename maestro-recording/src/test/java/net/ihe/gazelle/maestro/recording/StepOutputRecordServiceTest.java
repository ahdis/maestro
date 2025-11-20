package net.ihe.gazelle.maestro.recording;

import com.kereval.gazelle.datahouse.api.business.record.Item;
import com.kereval.gazelle.datahouse.api.business.record.RefType;
import com.kereval.gazelle.datahouse.api.business.record.Reference;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.ItemPlan;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.ItemRelationBinding;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.PropertyMutation;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.ReferenceBinding;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.StepOutputProjection;
import net.ihe.gazelle.security.business.acl.AccessControlList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StepOutputRecordServiceTest {

   private final RecordItemClientMock recordItemClient = new RecordItemClientMock();
   private final StepOutputRecordService service = new StepOutputRecordService(recordItemClient, "http://dh");

   @Test
   void shouldPersistItemsWireRelationsAndMutateOutputs() {
      StepRunReport step = new StepRunReport()
            .setStepName("rel-step")
            .addOutput(new StringProperty("report", "inline"));

      ItemPlan childPlan = new ItemPlan(
            new DatahouseMaestroItem().setType("CHILD").setContent("{\"level\":1}").setReferences(new ArrayList<>()),
            List.of(new ItemRelationBinding(1, "child-link", "CHILD", null)),
            List.of(),
            null);
      ItemPlan rootPlan = new ItemPlan(
            new DatahouseMaestroItem().setType("ROOT").setContent("{\"level\":0}").setReferences(new ArrayList<>()),
            List.of(),
            List.of(new ReferenceBinding("report-link", "ROOT", null)),
            null);

      PropertyMutation mutation = new PropertyMutation(0, 1,
            (itemId, itemUrl) -> new StringProperty("report", itemUrl));

      StepOutputProjection projection = new StepOutputProjection(
            List.of(childPlan, rootPlan),
            List.of(),
            List.of(mutation),
            List.of());

      List<Property> updatedOutputs = new ArrayList<>(step.getOutputs());
      List<Reference> reportReferences = new ArrayList<>();
      List<Reference> returnedRefs = service.dissectAndRecord(
            step,
            projection,
            new AccessControlList(),
            reportReferences,
            updatedOutputs);

      assertThat(returnedRefs).hasSize(1);
      Reference reportRef = returnedRefs.getFirst();
      assertEquals("report-link", reportRef.getName());
      assertEquals("ROOT", reportRef.getType());

      assertThat(step.getOutputs()).hasSize(1);
      Property mutated = step.getOutputs().getFirst();
      assertEquals("report", mutated.getName());
      assertEquals("http://dh/items/itm-2", mutated.getValue());

      assertThat(recordItemClient.recordedItems).hasSize(2);
      Item recordedRoot = (Item) recordItemClient.recordedItems.get(1);
      assertThat(recordedRoot.getReferences())
            .extracting(Reference::getName)
            .contains("child-link");
   }

   @Test
   void shouldMutateContentUsingResolvedReferences() {
      StepRunReport step = new StepRunReport()
            .setStepName("content-step")
            .addOutput(new StringProperty("noop", "noop"));

      ItemPlan childPlan = new ItemPlan(
            new DatahouseMaestroItem().setType("CHILD").setContent("attachment=log-ref").setReferences(new ArrayList<>()),
            List.of(new ItemRelationBinding(1, "child-ref", "CHILD", null)),
            List.of(),
            (content, ctx) -> content.replace("log-ref", ctx.findAttachmentId("log-ref")));
      ItemPlan parentPlan = new ItemPlan(
            new DatahouseMaestroItem().setType("PARENT").setContent("child=child-ref").setReferences(new ArrayList<>()),
            List.of(),
            List.of(),
            (content, ctx) -> content.replace("child-ref", ctx.findItemId("child-ref")));

      StepOutputProjection projection = new StepOutputProjection(
            List.of(childPlan, parentPlan),
            List.of(),
            List.of(),
            List.of());

      List<Property> updatedOutputs = new ArrayList<>(step.getOutputs());
      List<Reference> reportReferences = new ArrayList<>();
      reportReferences.add(new Reference()
            .setName("log-ref")
            .setType("LOG")
            .setRefType(RefType.ATTACHMENT)
            .setValue("att-7"));

      service.dissectAndRecord(step, projection, new AccessControlList(), reportReferences, updatedOutputs);

      assertThat(recordItemClient.recordedItems).hasSize(2);
      Item recordedChild = (Item) recordItemClient.recordedItems.get(0);
      assertEquals("attachment=att-7", recordedChild.getContent());
      Item recordedParent = (Item) recordItemClient.recordedItems.get(1);
      assertEquals("child=itm-1", recordedParent.getContent());
   }

   @Test
   void shouldFailWhenRelationTargetsEarlierItem() {
      StepRunReport step = new StepRunReport()
            .setStepName("invalid-rel")
            .addOutput(new StringProperty("report", "inline"));

      ItemPlan childPlan = new ItemPlan(
            new DatahouseMaestroItem().setType("CHILD").setContent("{}").setReferences(new ArrayList<>()),
            List.of(new ItemRelationBinding(0, "loop", "TYPE", null)),
            List.of(),
            null);

      StepOutputProjection projection = new StepOutputProjection(
            List.of(childPlan),
            List.of(),
            List.of(),
            List.of());

      List<Property> updatedOutputs = new ArrayList<>(step.getOutputs());

      AccessControlList acl = new AccessControlList();
      ArrayList<Reference> attachmentsReferences = new ArrayList<>();
      assertThrows(IllegalStateException.class, () -> service.dissectAndRecord(
            step,
            projection,
            acl,
            attachmentsReferences,
            updatedOutputs));
   }
}
