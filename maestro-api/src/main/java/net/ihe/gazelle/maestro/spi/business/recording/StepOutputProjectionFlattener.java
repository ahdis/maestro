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
      for (GraphNodeBuilder node : orderedNodes) {
         Integer owningIndex = indexByNode.get(node);
         if (owningIndex == null) {
            throw new IllegalStateException("Attachment owner node was not registered in the builder graph");
         }
         for (AttachmentOutputBuilder attachment : node.attachments()) {
            attachmentPlans.add(attachment.build(owningIndex));
         }
      }

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
