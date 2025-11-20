package net.ihe.gazelle.maestro.recording;

import com.kereval.gazelle.datahouse.api.business.record.Item;
import com.kereval.gazelle.datahouse.api.business.record.Reference;
import com.kereval.gazelle.datahouse.technical.rest.client.RecordItemClient;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.*;
import net.ihe.gazelle.security.business.acl.AccessControlList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StepOutputRecordService {

    private final String datahouseUrl;


    private final RecordItemClient recordItemClient;

    public StepOutputRecordService(RecordItemClient recordItemClient, String datahouseUrl) {
        this.recordItemClient = recordItemClient;
        this.datahouseUrl = datahouseUrl;
    }



    /**     * Dissects step outputs, persists items, mutates outputs to point to persisted items, and collects references.
     *
     * @param stepRunReport          The report of the step run to update with outputs.
     * @param stepOutputProjection   The projection containing items and mutations.
     * @param acl                    The access control list to apply to persisted items.
     * @param attachmentsReferences  The list to collect attachment references into.
     * @param updatedOutputs         The list to populate with updated output properties.
     * @return The list of all attachment references including newly added ones.
     */
    public List<Reference> dissectAndRecord(StepRunReport stepRunReport,
                                            StepOutputProjection stepOutputProjection,
                                            AccessControlList acl,
                                            List<Reference> attachmentsReferences,
                                            List<Property> updatedOutputs) {

        // persist step outputs as items
        ItemPersistenceState persistenceState = persistItems(stepOutputProjection.items(), attachmentsReferences, acl);
        // mutate step outputs to point to persisted items to avoid inline payloads
        applyPropertyMutations(stepOutputProjection.propertyMutations(), updatedOutputs, persistenceState);

        stepOutputProjection.reportReferences().stream()
                .filter(DatahouseMaestroReference.class::isInstance)
                .map(DatahouseMaestroReference.class::cast)
                .map(DatahouseMaestroReference::toDatahouseReference)
                .forEach(attachmentsReferences::add);
        stepRunReport.setOutputs(updatedOutputs);
        return attachmentsReferences;
    }

    public String constructUrlToDatahouse(String itemId) {
        return datahouseUrl + "/items/" + itemId;
    }


    // Persists the ordered items, wiring relation/report references as we go, and returns the ids/urls for later mutations.
    private ItemPersistenceState persistItems(List<ItemPlan> itemPlans,
                                              List<Reference> reportReferences,
                                              AccessControlList acl) {
        if (itemPlans.isEmpty()) {
            return ItemPersistenceState.EMPTY;
        }

        Map<String, String> itemIdsByReferenceName = new HashMap<>();
        Map<String, String> attachmentIdsByReferenceName = new HashMap<>();
        updateReferenceMapsFrom(reportReferences, itemIdsByReferenceName, attachmentIdsByReferenceName);

        List<Item> datahouseItems = new ArrayList<>(itemPlans.size());
        datahouseItems.addAll(itemPlans.stream()
                .map(ItemPlan::item)
                .filter(DatahouseMaestroItem.class::isInstance)
                .map(DatahouseMaestroItem.class::cast)
                .map(DatahouseMaestroItem::toDatahouseItem)
                .toList()
        );
        String[] ids = new String[itemPlans.size()];
        String[] urls = new String[itemPlans.size()];

        for (int i = 0; i < itemPlans.size(); i++) {
            ItemPlan itemPlan = itemPlans.get(i);
            Item item = prepareItemAclAndReferences(datahouseItems.get(i), acl);
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
                    .setRefType(DatahouseMaestroReference.toDatahouseRefType(binding.refType()))
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
        ContentMutationContext context = new ContentMutationContext(itemIdsByReferenceName, attachmentIdsByReferenceName);
        String mutatedContent = itemPlan.contentMutator().mutate(item.getContent(), context);
        item.setContent(mutatedContent);
    }

    private void wireParentBindings(List<ItemPlan> itemPlans, List<Item> datahouseItems, int currentIndex, String childItemId, ItemPlan childPlan) {
        for (ItemRelationBinding binding : childPlan.relationBindings()) {
            validateReferencingIndex(binding.referencingItemIndex(), currentIndex, itemPlans.size());
            Item referencingItem = datahouseItems.get(binding.referencingItemIndex());
            if (referencingItem.getReferences() == null) {
                referencingItem.setReferences(new ArrayList<>());
            }
            referencingItem.addReference(new Reference()
                    .setName(binding.referenceName())
                    .setType(binding.referenceType())
                    .setRefType(DatahouseMaestroReference.toDatahouseRefType(binding.refType()))
                    .setValue(childItemId));
        }
    }

    private void validateReferencingIndex(int referencingIndex, int childIndex, int maxSize) {
        if (referencingIndex <= childIndex || referencingIndex >= maxSize) {
            throw new IllegalStateException("Referencing item index must point to an item persisted after the child");
        }
    }

}
