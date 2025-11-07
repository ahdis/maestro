package net.ihe.gazelle.maestro.api.business.testreport.dissection;

import com.kereval.gazelle.datahouse.api.business.record.Item;
import com.kereval.gazelle.datahouse.api.business.record.RefType;
import com.kereval.gazelle.datahouse.api.business.record.Reference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Builder that lets dissectors describe complex hierarchies using symbolic handles instead of raw indexes.
 * <p>
 * Callers register items, link them together via {@link ItemHandle}s, then call {@link #build()} to obtain
 * the {@link StepOutputProjection} expected by the persistence service. The builder transparently orders
 * items so that every parent appears after its children, matching {@link ItemReferenceBinding}'s contract.
 */
public final class StepOutputProjectionBuilder {

   private enum VisitState {UNVISITED, VISITING, VISITED}

   private static final AtomicLong HANDLE_SEQUENCE = new AtomicLong();

   private final Map<ItemHandle, ItemNode> nodes = new LinkedHashMap<>();
   private final List<AttachmentPlan> attachmentPlans = new ArrayList<>();
   private final List<Reference> reportReferences = new ArrayList<>();
   private final List<PropertyMutationRequest> propertyMutationRequests = new ArrayList<>();

   /**
    * Creates a new handle, registers the supplied item, and returns the handle so callers can attach bindings.
    */
   public ItemHandle addItem(Item item) {
      Objects.requireNonNull(item, "item");
      ItemHandle handle = new ItemHandle(HANDLE_SEQUENCE.incrementAndGet());
      nodes.put(handle, new ItemNode(item));
      return handle;
   }

   /**
    * Adds a reference that should be appended to the enclosing TestReport when the item is persisted.
    */
   public void addReportBinding(ItemHandle itemHandle, ReportReferenceBinding binding) {
      ItemNode node = getNode(itemHandle);
      node.reportBindings.add(Objects.requireNonNull(binding, "binding"));
   }

   /**
    * Links a child to its parent. The builder ensures the parent item appears later in the final projection.
    */
   public void linkParent(ItemHandle childHandle,
         ItemHandle parentHandle,
         String referenceName,
         String referenceType,
         RefType refType) {
      ItemNode child = getNode(childHandle);
      ensureHandleExists(parentHandle);
      child.parentBindings.add(new ParentBinding(parentHandle, referenceName, referenceType, refType));
   }

   /**
    * Registers an attachment plan so callers can co-locate attachment wiring with item creation.
    */
   public void addAttachment(AttachmentPlan plan) {
      attachmentPlans.add(Objects.requireNonNull(plan, "plan"));
   }

   /**
    * Adds a reference that must be appended directly to the TestReport (for legacy flows).
    */
   public void addReportReference(Reference reference) {
      reportReferences.add(Objects.requireNonNull(reference, "reference"));
   }

   /**
    * Schedules a property mutation that will swap the specified output with a reference to {@code sourceHandle}.
    */
   public void addPropertyMutation(int outputIndex, ItemHandle sourceHandle, PropertyReferenceFactory factory) {
      if (outputIndex < 0) {
         throw new IllegalArgumentException("outputIndex must be >= 0");
      }
      Objects.requireNonNull(factory, "factory");
      ensureHandleExists(sourceHandle);
      propertyMutationRequests.add(new PropertyMutationRequest(outputIndex, sourceHandle, factory));
   }

   /**
    * Builds the final projection by ordering items, resolving handles to indexes, and materializing bindings.
    */
   public StepOutputProjection build() {
      List<ItemHandle> orderedHandles = topologicallySortHandles();
      Map<ItemHandle, Integer> indexByHandle = indexHandles(orderedHandles);

      List<ItemPlan> itemPlans = orderedHandles.stream()
            .map(handle -> toItemPlan(handle, indexByHandle))
            .toList();
      List<PropertyMutation> propertyMutations = propertyMutationRequests.stream()
            .map(request -> new PropertyMutation(
                  request.outputIndex,
                  requireIndex(indexByHandle, request.sourceHandle),
                  request.factory))
            .toList();

      return new StepOutputProjection(itemPlans, attachmentPlans, propertyMutations, reportReferences);
   }

   private ItemPlan toItemPlan(ItemHandle handle, Map<ItemHandle, Integer> indexByHandle) {
      ItemNode node = getNode(handle);
      List<ItemReferenceBinding> parentBindings = node.parentBindings.stream()
            .map(binding -> new ItemReferenceBinding(
                  requireIndex(indexByHandle, binding.parentHandle),
                  binding.referenceName,
                  binding.referenceType,
                  binding.refType))
            .toList();
      return new ItemPlan(node.item, parentBindings, List.copyOf(node.reportBindings));
   }

   private List<ItemHandle> topologicallySortHandles() {
      if (nodes.isEmpty()) {
         return List.of();
      }
      // attaching for each parent its children
      Map<ItemHandle, List<ItemHandle>> adjacency = new LinkedHashMap<>();
      nodes.keySet().forEach(handle -> adjacency.put(handle, new ArrayList<>()));
      nodes.forEach((childHandle, node) -> node.parentBindings.forEach(binding ->
              adjacency.computeIfAbsent(binding.parentHandle, key -> new ArrayList<>()).add(childHandle))
      );

      Map<ItemHandle, VisitState> states = new HashMap<>();
      List<ItemHandle> ordered = new ArrayList<>(nodes.size());
      for (ItemHandle handle : nodes.keySet()) {
         dfs(handle, adjacency, states, ordered);
      }
      return ordered;
   }

   private void dfs(ItemHandle handle,
         Map<ItemHandle, List<ItemHandle>> adjacency,
         Map<ItemHandle, VisitState> states,
         List<ItemHandle> ordered) {
      VisitState state = states.get(handle);
      if (state == VisitState.VISITING) {
         throw new IllegalStateException("Cycle detected when ordering items");
      }
      if (state == VisitState.VISITED) {
         return;
      }
      states.put(handle, VisitState.VISITING);
      for (ItemHandle child : adjacency.getOrDefault(handle, List.of())) {
         dfs(child, adjacency, states, ordered);
      }
      states.put(handle, VisitState.VISITED);
      ordered.add(handle);
   }

   private Map<ItemHandle, Integer> indexHandles(List<ItemHandle> orderedHandles) {
      Map<ItemHandle, Integer> indexByHandle = new LinkedHashMap<>();
      for (int i = 0; i < orderedHandles.size(); i++) {
         indexByHandle.put(orderedHandles.get(i), i);
      }
      return indexByHandle;
   }

   private int requireIndex(Map<ItemHandle, Integer> indexByHandle, ItemHandle handle) {
      Integer index = indexByHandle.get(handle);
      if (index == null) {
         throw new IllegalStateException("Handle " + handle + " was not registered");
      }
      return index;
   }

   private ItemNode getNode(ItemHandle handle) {
      ItemNode node = nodes.get(handle);
      if (node == null) {
         throw new IllegalArgumentException("Unknown handle: " + handle);
      }
      return node;
   }

   private void ensureHandleExists(ItemHandle handle) {
      if (!nodes.containsKey(handle)) {
         throw new IllegalArgumentException("Unknown handle: " + handle);
      }
   }

   private record ParentBinding(ItemHandle parentHandle,
                                String referenceName,
                                String referenceType,
                                RefType refType) {
   }

   private record PropertyMutationRequest(int outputIndex, ItemHandle sourceHandle, PropertyReferenceFactory factory) {
   }

   // Mutable staging structure mirroring ItemPlan until handles are resolved to indexes.
   private static final class ItemNode {
      private final Item item;
      private final List<ParentBinding> parentBindings = new ArrayList<>();
      private final List<ReportReferenceBinding> reportBindings = new ArrayList<>();

      private ItemNode(Item item) {
         this.item = item;
      }
   }

}
