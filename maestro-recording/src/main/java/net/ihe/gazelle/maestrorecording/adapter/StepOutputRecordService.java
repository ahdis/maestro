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

import com.kereval.gazelle.datahouse.api.business.record.Item;
import com.kereval.gazelle.datahouse.api.business.record.RefType;
import com.kereval.gazelle.datahouse.api.business.record.Reference;
import com.kereval.gazelle.datahouse.technical.rest.client.RecordItemClient;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.spi.business.recording.*;
import net.ihe.gazelle.security.business.acl.AccessControlList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class StepOutputRecordService {

   private final String datahouseUrl;


   private final RecordItemClient recordItemClient;

   public StepOutputRecordService(RecordItemClient recordItemClient, String datahouseUrl) {
      this.recordItemClient = recordItemClient;
      this.datahouseUrl = datahouseUrl;
   }


   /**
    * Dissects step outputs, persists items, mutates outputs to point to persisted items, and collects references.
    *
    * @param stepRunReport         The report of the step run to update with outputs.
    * @param stepOutputProjection  The projection containing items and mutations.
    * @param acl                   The access control list to apply to persisted items.
    * @param attachmentsReferences The list to collect attachment references into.
    * @param updatedOutputs        The list to populate with updated output properties.
    *
    * @return The list of all attachment references including newly added ones.
    */
   List<Reference> dissectAndRecord(StepRunReport stepRunReport,
                                           StepOutputProjection stepOutputProjection,
                                           AccessControlList acl,
                                           List<Reference> attachmentsReferences,
                                           Map<Integer, List<Reference>> attachmentReferencesByOwningItemIndex,
                                           List<Property> updatedOutputs) {

      // persist step outputs as items
      ItemPersistenceState persistenceState = persistItems(stepOutputProjection.items(), attachmentsReferences,
            attachmentReferencesByOwningItemIndex, acl);
      // mutate step outputs to point to persisted items to avoid inline payloads
      applyPropertyMutations(stepOutputProjection.propertyMutations(), updatedOutputs, persistenceState);

      stepOutputProjection.reportReferences().stream()
            .map(this::toDatahouseReference)
            .forEach(attachmentsReferences::add);
      stepRunReport.setOutputs(updatedOutputs);
      return attachmentsReferences;
   }

   String constructUrlToDatahouse(String itemId) {
      return datahouseUrl + "/items/" + itemId;
   }


   // Persists the ordered items, wiring relation/report references as we go, and returns the ids/urls for later
   // mutations.
   private ItemPersistenceState persistItems(List<ItemPlan> itemPlans,
                                             List<Reference> reportReferences,
                                             Map<Integer, List<Reference>> attachmentReferencesByOwningItemIndex,
                                             AccessControlList acl) {
      if (itemPlans.isEmpty()) {
         return ItemPersistenceState.EMPTY;
      }

      Map<String, String> itemIdsByReferenceName = new HashMap<>();
      Map<String, String> attachmentIdsByReferenceName = new HashMap<>();
      updateReferenceMapsFrom(reportReferences, itemIdsByReferenceName, attachmentIdsByReferenceName);
      Map<Integer, List<Reference>> safeAttachmentReferencesByOwningItemIndex =
            attachmentReferencesByOwningItemIndex != null ? attachmentReferencesByOwningItemIndex : Map.of();

      List<Item> datahouseItems = new ArrayList<>(itemPlans.size());
      datahouseItems.addAll(itemPlans.stream()
            .map(ItemPlan::item)
            .map(this::toDatahouseItem)
            .toList()
      );
      String[] ids = new String[itemPlans.size()];
      String[] urls = new String[itemPlans.size()];

      for (int i = 0; i < itemPlans.size(); i++) {
         ItemPlan itemPlan = itemPlans.get(i);
         Item item = prepareItemAclAndReferences(datahouseItems.get(i), acl);
         List<Reference> attachmentRefsForItem = safeAttachmentReferencesByOwningItemIndex.get(i);
         if (attachmentRefsForItem != null && !attachmentRefsForItem.isEmpty()) {
            item.getReferences().addAll(attachmentRefsForItem);
         }
         updateReferenceMapsFrom(item.getReferences(), itemIdsByReferenceName, attachmentIdsByReferenceName);
         applyContentMutation(itemPlan, item, itemIdsByReferenceName, attachmentIdsByReferenceName);
         String itemId = recordItemClient.recordItem(item);
         ids[i] = itemId;
         urls[i] = constructUrlToDatahouse(itemId);
         appendReportBindings(reportReferences, itemPlan, itemId);
         updateReferenceMapsFrom(reportReferences, itemIdsByReferenceName, attachmentIdsByReferenceName);
         registerRelationBindingReferenceNames(itemPlan, itemId, itemIdsByReferenceName, attachmentIdsByReferenceName);
         wireParentBindings(itemPlans, datahouseItems, i, itemId, itemPlan);
      }

      return new ItemPersistenceState(ids, urls);
   }

   // Rewrites step outputs so they now point at freshly persisted items instead of inline payloads.
   private void applyPropertyMutations(List<PropertyMutation> mutations,
                                       List<Property> outputs,
                                       ItemPersistenceState state) {
      for (PropertyMutation mutation : mutations) {
         String[] ids = state.itemIds();
         String[] urls = state.itemUrls();
         int sourceIndex = mutation.sourceItemIndex();
         if (sourceIndex < 0 || sourceIndex >= ids.length) {
            throw new IllegalStateException("Invalid source item index: " + sourceIndex);
         }
         Property replacement = mutation.factory().create(ids[sourceIndex], urls[sourceIndex]);
         setOutput(outputs, mutation.outputIndex(), replacement);
      }
   }

   private void setOutput(List<Property> outputs, int index, Property replacement) {
      while (outputs.size() <= index) {
         outputs.add(null);
      }
      outputs.set(index, replacement);
   }

   private Item prepareItemAclAndReferences(Item item, AccessControlList fallbackAcl) {
      if (item.getAccessControlList() == null) {
         item.setAccessControlList(fallbackAcl);
      }
      if (item.getReferences() == null) {
         item.setReferences(new ArrayList<>());
      }
      return item;
   }

   private void appendReportBindings(List<Reference> reportReferences, ItemPlan itemPlan, String itemId) {
      for (ReferenceBinding binding : itemPlan.reportBindings()) {
         reportReferences.add(new Reference()
               .setName(binding.referenceName())
               .setType(binding.referenceType())
               .setRefType(toDatahouseRefType(binding.refType()))
               .setValue(itemId));
      }
   }

   private void updateReferenceMapsFrom(List<Reference> references,
                                        Map<String, String> itemIdsByReferenceName,
                                        Map<String, String> attachmentIdsByReferenceName) {
      if (references == null) {
         return;
      }
      for (Reference reference : references) {
         switch (reference.getRefType()) {
            case ITEM_ID -> itemIdsByReferenceName.put(reference.getName(), reference.getValue());
            case ATTACHMENT -> attachmentIdsByReferenceName.put(reference.getName(), reference.getValue());
            case URL -> {
               // no-op
            }
         }
      }
   }

   private void registerRelationBindingReferenceNames(ItemPlan itemPlan,
                                                      String itemId,
                                                      Map<String, String> itemIdsByReferenceName,
                                                      Map<String, String> attachmentIdsByReferenceName) {
      for (ItemRelationBinding binding : itemPlan.relationBindings()) {
         switch (binding.refType()) {
            case ITEM_ID -> itemIdsByReferenceName.put(binding.referenceName(), itemId);
            case ATTACHMENT -> attachmentIdsByReferenceName.put(binding.referenceName(), itemId);
            case URL -> {
               // no-op
            }
         }
      }
   }

   private void applyContentMutation(ItemPlan itemPlan,
                                     Item item,
                                     Map<String, String> itemIdsByReferenceName,
                                     Map<String, String> attachmentIdsByReferenceName) {
      if (itemPlan.contentMutator() == null || item.getContent() == null) {
         return;
      }
      ContentMutationContext context = new ContentMutationContext(itemIdsByReferenceName, attachmentIdsByReferenceName,
            datahouseUrl);
      String mutatedContent = itemPlan.contentMutator().mutate(item.getContent(), context);
      item.setContent(mutatedContent);
   }

   private void wireParentBindings(List<ItemPlan> itemPlans, List<Item> datahouseItems, int currentIndex,
                                   String childItemId, ItemPlan childPlan) {
      for (ItemRelationBinding binding : childPlan.relationBindings()) {
         validateReferencingIndex(binding.referencingItemIndex(), currentIndex, itemPlans.size());
         Item referencingItem = datahouseItems.get(binding.referencingItemIndex());
         if (referencingItem.getReferences() == null) {
            referencingItem.setReferences(new ArrayList<>());
         }
         referencingItem.addReference(new Reference()
               .setName(binding.referenceName())
               .setType(binding.referenceType())
               .setRefType(toDatahouseRefType(binding.refType()))
               .setValue(childItemId));
      }
   }

   private void validateReferencingIndex(int referencingIndex, int childIndex, int maxSize) {
      if (referencingIndex <= childIndex || referencingIndex >= maxSize) {
         throw new IllegalStateException("Referencing item index must point to an item persisted after the child");
      }
   }

   private Item toDatahouseItem(MaestroItem maestroItem) {
      return new Item()
            .setContent(maestroItem.content())
            .setType(maestroItem.type())
            .setAccessControlList(maestroItem.acl())
            .setReferences(
              maestroItem.references().stream()
                        .map(this::toDatahouseReference)
                        .collect(Collectors.toCollection(ArrayList::new))
            )
            .setAdditionalParameters(maestroItem.additionalParameters());
   }

   private Reference toDatahouseReference(MaestroReference maestroReference) {
      return new Reference()
            .setName(maestroReference.name())
            .setType(maestroReference.type())
            .setRefType(toDatahouseRefType(maestroReference.refType()))
            .setValue(maestroReference.value());
   }

   private RefType toDatahouseRefType(MaestroRefType maestroRefType) {
      return switch (maestroRefType) {
         case ATTACHMENT -> RefType.ATTACHMENT;
         case URL -> RefType.URL;
         case ITEM_ID -> RefType.ITEM_ID;
      };
   }

}
