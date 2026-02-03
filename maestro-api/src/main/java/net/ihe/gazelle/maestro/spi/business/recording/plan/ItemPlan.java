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

package net.ihe.gazelle.maestro.spi.business.recording.plan;

import net.ihe.gazelle.modelmarshaller.technical.marshalling.TextMarshaller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ItemPlan<T> {

   private final String type;
   private final T itemObject;
   private final TextMarshaller<T> textMarshaller;
   private final List<FutureReferencePlan<T>> futureReferencePlans = new ArrayList<>();
   private final Map<String, String> additionalParameters = new HashMap<>();

   public ItemPlan(String type, T itemObject, TextMarshaller<T> textMarshaller) {
      this.type = type;
      this.itemObject = itemObject;
      this.textMarshaller = textMarshaller;
   }

   public String type() {
      return type;
   }

   public T itemObject() {
      return itemObject;
   }

   public TextMarshaller<T> textMarshaller() {
      return textMarshaller;
   }

   public List<FutureReferencePlan<T>> futureReferencePlans() {
      return new ArrayList<>(futureReferencePlans);
   }

   public ItemPlan<T> withReference(String referenceName, ItemPlan<?> itemPlan) {
      return withReference(referenceName, itemPlan, null);
   }

   public ItemPlan<T> withReference(String referenceName, ItemPlan<?> itemPlan, ItemObjectMutation<T> mutation) {
      return addFutureReferencePlan(ReferencePlan.forItem(referenceName, itemPlan), mutation);
   }

   public ItemPlan<T> withReference(String referenceName, AttachmentPlan attachmentPlan) {
      return withReference(referenceName, attachmentPlan, null);
   }

   public ItemPlan<T> withReference(String referenceName,
                                    AttachmentPlan attachmentPlan,
                                    ItemObjectMutation<T> mutation) {
      return addFutureReferencePlan(ReferencePlan.forAttachment(referenceName, attachmentPlan), mutation);
   }

   public ItemPlan<T> addDetachedAttachment(String referenceName,
                                            String type,
                                            byte[] content,
                                            String filename) {
      return addDetachedAttachment(referenceName, type, content, filename, null);
   }

   public ItemPlan<T> addDetachedAttachment(String referenceName,
                                            String type,
                                            byte[] content,
                                            String filename,
                                            ItemObjectMutation<T> mutation) {
      AttachmentPlan attachmentPlan = new AttachmentPlan(type, content, filename);
      return withReference(referenceName, attachmentPlan, mutation);
   }

   /**
    * Add a child reference plan, with an optional mutation to apply to the item once the target of the reference has
    * been persisted.
    *
    * @param referencePlan the plan describing the reference to add
    * @param mutation Optional mutation to perform once the target of the reference has been persisted.
    *
    * @return this item plan
    */
   public ItemPlan<T> addFutureReferencePlan(ReferencePlan referencePlan, ItemObjectMutation<T> mutation) {
      this.futureReferencePlans.add(new FutureReferencePlan<>(referencePlan, mutation));
      return this;
   }

   public ItemPlan<T> addFutureReferencePlan(FutureReferencePlan<T> futureReferencePlan) {
      this.futureReferencePlans.add(futureReferencePlan);
      return this;
   }

   public Map<String, String> additionalParameters() {
      return new HashMap<>(additionalParameters);
   }

   public ItemPlan<T> addAdditionalParameter(String key, String value) {
      this.additionalParameters.put(key, value);
      return this;
   }

   public ItemPlan<T> addAdditionalParameters(Map<String, String> additionalParameters) {
      this.additionalParameters.putAll(additionalParameters);
      return this;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof ItemPlan<?> itemPlan)) {
         return false;
      }
      return Objects.equals(type, itemPlan.type) && Objects.equals(itemObject, itemPlan.itemObject)
            && Objects.equals(textMarshaller, itemPlan.textMarshaller)
            && Objects.equals(futureReferencePlans, itemPlan.futureReferencePlans)
            && Objects.equals(additionalParameters, itemPlan.additionalParameters);
   }

   @Override
   public int hashCode() {
      return Objects.hash(type, itemObject, textMarshaller, futureReferencePlans, additionalParameters);
   }

}
