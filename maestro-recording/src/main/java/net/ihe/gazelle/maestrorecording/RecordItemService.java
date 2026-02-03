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

package net.ihe.gazelle.maestrorecording;

import com.kereval.gazelle.datahouse.api.business.record.Attachment;
import com.kereval.gazelle.datahouse.api.business.record.Item;
import com.kereval.gazelle.datahouse.api.business.record.RefType;
import com.kereval.gazelle.datahouse.api.business.record.Reference;
import com.kereval.gazelle.datahouse.technical.rest.client.RecordItemClient;
import net.ihe.gazelle.maestro.spi.business.recording.plan.AttachmentPlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.FutureReferencePlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.ItemPlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.MaestroRefType;
import net.ihe.gazelle.maestro.spi.business.recording.plan.PersistencePlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.ReferencePlan;
import net.ihe.gazelle.security.business.acl.AccessControlList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordItemService {

   private final RecordItemClient recordItemClient;

   public RecordItemService(RecordItemClient recordItemClient) {
      this.recordItemClient = recordItemClient;
   }

   public <T> String recordPlan(PersistencePlan<T> persistencePlan) {
      return recordItemPlan(persistencePlan.getRootItem(), persistencePlan.getAcl(), new Recorded());
   }

   private <T> String recordItemPlan(ItemPlan<T> itemPlan, AccessControlList acl, Recorded recorded) {
      if (recorded.items.containsKey(itemPlan)) {
         return recorded.items.get(itemPlan);
      }
      Item item = initItem(itemPlan, acl);
      for (FutureReferencePlan<T> futureReferencePlan : itemPlan.futureReferencePlans()) {
         ReferencePlan referencePlan = futureReferencePlan.referencePlan();
         String referenceId = recordReferencedObject(referencePlan, acl, recorded);
         if (futureReferencePlan.mutation() != null) {
            futureReferencePlan.mutation().mutateReference(itemPlan.itemObject(), referencePlan.getName(), referenceId);
         }
         item.addReference(asReference(referencePlan, referenceId));
      }
      item.setContent(itemPlan.textMarshaller().marshallAsString(itemPlan.itemObject()));
      String id = recordItemClient.recordItem(item);
      recorded.items.put(itemPlan, id);
      return id;
   }

   private String recordReferencedObject(ReferencePlan referencePlan, AccessControlList acl, Recorded recorded) {
      return switch (referencePlan.getRefType()) {
         case ITEM_ID -> recordItemPlan((ItemPlan<?>) referencePlan.getTarget(), acl, recorded);
         case ATTACHMENT -> recordAttachmentPlan((AttachmentPlan) referencePlan.getTarget(), recorded);
         case URL -> (String) referencePlan.getTarget();
         default -> throw new IllegalStateException("Unexpected RefType: " + referencePlan.getRefType());
      };
   }

   private String recordAttachmentPlan(AttachmentPlan attachmentPlan, Recorded recorded) {
      if (recorded.attachments.containsKey(attachmentPlan)) {
         return recorded.attachments.get(attachmentPlan);
      }
      String id = recordItemClient.uploadAttachments(List.of(
            new Attachment()
                  .setType(attachmentPlan.type())
                  .setFilename(attachmentPlan.filename())
                  .setContent(attachmentPlan.content())
      )).getFirst();
      recorded.attachments.put(attachmentPlan, id);
      return id;
   }

   private static <T> Item initItem(ItemPlan<T> itemPlan, AccessControlList acl) {
      return new Item()
            .setType(itemPlan.type())
            .setAccessControlList(acl)
            .setAdditionalParameters(itemPlan.additionalParameters());
   }

   private static Reference asReference(ReferencePlan referencePlan, String referenceId) {
      return new Reference()
            .setName(referencePlan.getName())
            .setRefType(toDatahouseRefType(referencePlan.getRefType()))
            .setType(referencePlan.getTargetType())
            .setValue(referenceId);
   }

   private static RefType toDatahouseRefType(MaestroRefType refType) {
      return switch (refType) {
         case ITEM_ID -> RefType.ITEM_ID;
         case ATTACHMENT -> RefType.ATTACHMENT;
         case URL -> RefType.URL;
      };
   }

   private static class Recorded {
      private final Map<ItemPlan<?>, String> items = new HashMap<>();
      private final Map<AttachmentPlan, String> attachments = new HashMap<>();
   }
}
