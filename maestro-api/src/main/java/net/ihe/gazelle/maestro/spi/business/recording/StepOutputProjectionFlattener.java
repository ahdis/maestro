package net.ihe.gazelle.maestro.spi.business.recording;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Internal service turning the builder graph into the projection consumed by the persistence layer.
 */
final class StepOutputProjectionFlattener {

   StepOutputProjection flatten(List<ItemOutputBuilder> roots,
         List<MaestroReference> reportReferences) {
      List<GraphNodeBuilder> orderedNodes = new ArrayList<>();
      if (roots != null) {
         for (ItemOutputBuilder root : roots) {
            dfs(root, orderedNodes);
         }
      }

      Map<GraphNodeBuilder, Integer> indexByNode = new LinkedHashMap<>();
      for (int i = 0; i < orderedNodes.size(); i++) {
         indexByNode.put(orderedNodes.get(i), i);
      }

      List<ItemPlan> itemPlans = orderedNodes.stream()
            .map(node -> toItemPlan(node, indexByNode))
            .toList();

      List<PropertyMutation> propertyMutations = new ArrayList<>();
      for (GraphNodeBuilder node : orderedNodes) {
         Integer sourceIndex = indexByNode.get(node);
         for (PropertyReplacement replacement : node.propertyReplacements()) {
            propertyMutations.add(new PropertyMutation(
                  replacement.outputIndex(),
                  sourceIndex,
                  replacement.factory()));
         }
      }

      List<AttachmentPlan> attachmentPlans = new ArrayList<>();
      orderedNodes.stream()
            .flatMap(node -> node.attachments().stream())
            .map(AttachmentOutputBuilder::build)
            .forEach(attachmentPlans::add);

      return new StepOutputProjection(itemPlans, attachmentPlans, propertyMutations, reportReferences);
   }

   private ItemPlan toItemPlan(GraphNodeBuilder node,
         Map<GraphNodeBuilder, Integer> indexByNode) {
      List<ItemRelationBinding> relationBindings = relationBindings(node, indexByNode);
      List<ReferenceBinding> reportBindings = node instanceof ItemOutputBuilder root
            ? root.reportBindings()
            : List.of();
      return new ItemPlan(node.item(), relationBindings, reportBindings, node.contentMutator());
   }

   private List<ItemRelationBinding> relationBindings(GraphNodeBuilder node,
         Map<GraphNodeBuilder, Integer> indexByNode) {
      if (node instanceof SubItemOutputBuilder subItem) {
         Integer referencingIndex = indexByNode.get(subItem.referencingNode());
         if (referencingIndex == null) {
            throw new IllegalStateException("Referencing node was not registered in the builder graph");
         }
         return List.of(new ItemRelationBinding(
               referencingIndex,
               subItem.referenceName(),
               subItem.referenceType(),
               subItem.refType()));
      }
      return List.of();
   }

   private void dfs(GraphNodeBuilder node,
         List<GraphNodeBuilder> orderedNodes) {
      for (SubItemOutputBuilder child : node.subItems()) {
         dfs(child, orderedNodes);
      }
      orderedNodes.add(node);
   }
}
