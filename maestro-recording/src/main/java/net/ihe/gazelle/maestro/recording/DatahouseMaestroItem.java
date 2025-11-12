package net.ihe.gazelle.maestro.recording;

import com.kereval.gazelle.datahouse.api.business.record.Item;
import com.kereval.gazelle.datahouse.api.business.record.RefType;
import com.kereval.gazelle.datahouse.api.business.record.Reference;
import net.ihe.gazelle.maestro.api.business.recording.MaestroItem;
import net.ihe.gazelle.maestro.api.business.recording.MaestroRefType;
import net.ihe.gazelle.maestro.api.business.recording.MaestroReference;
import net.ihe.gazelle.security.business.acl.AccessControlList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DatahouseMaestroItem implements MaestroItem {

   private final Item item;

   public DatahouseMaestroItem(MaestroItem item) {
      this.item = new Item();
      this.item.setContent(item.getContent());
      this.item.setType(item.getType());
      this.item.setAccessControlList(item.getAccessControlList());
      this.item.setReferences(
              item.getReferences()
                        .stream()
                        .filter(DatahouseMaestroReference.class::isInstance)
                        .map(DatahouseMaestroReference.class::cast)
                        .map(DatahouseMaestroReference::toDatahouseReference)
                        .toList()
      );
     this.item.getAdditionalParameters().putAll(item.getAdditionalParameters());
   }

   public DatahouseMaestroItem() {
      this.item = new Item();
   }


   public DatahouseMaestroItem setAccessControlList(AccessControlList acl) {
      this.item.setAccessControlList(acl);
      return this;
   }

   public DatahouseMaestroItem setType(String type) {
      this.item.setType(type);
      return this;
   }

   public DatahouseMaestroItem setContent(String content) {
      this.item.setContent(content);
      return this;
   }

   public DatahouseMaestroItem setReferences(List<MaestroReference> references) {
      this.item.getReferences().clear();
      if (references != null) {
         this.item.getReferences().addAll(
                    references.stream()
                            .filter(DatahouseMaestroReference.class::isInstance)
                            .map(DatahouseMaestroReference.class::cast)
                            .map(DatahouseMaestroReference::toDatahouseReference)
                            .toList()
         );
      }
      return this;
   }

   public DatahouseMaestroItem addReference(MaestroReference reference) {
     if( reference instanceof DatahouseMaestroReference datahouseRef) {
        this.item.getReferences().add(datahouseRef.toDatahouseReference());
     } else {
        throw new IllegalArgumentException("Reference must be of type DatahouseMaestroReference");
     }
      return this;
   }

   public DatahouseMaestroItem addAdditionalParameter(String key, String value) {
      this.item.getAdditionalParameters().put(key, value);
      return this;
   }

   @Override
   public AccessControlList getAccessControlList() {
      return this.item.getAccessControlList();
   }

   @Override
   public String getType() {
      return this.item.getType();
   }

   @Override
   public String getContent() {
      return this.item.getContent();
   }

   @Override
   public List<? extends MaestroReference> getReferences() {
        return this.item.getReferences()
                .stream()
                .map(DatahouseMaestroReference::new)
                .toList();
   }

   @Override
   public Map<String, String> getAdditionalParameters() {
      return Map.copyOf(item.getAdditionalParameters());
   }


   public Item toDatahouseItem() {
      return this.item;
   }


}
