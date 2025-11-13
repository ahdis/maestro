package net.ihe.gazelle.maestro.simulation.step.record.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.ihe.gazelle.simulation.business.callback.Message;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
      "name",
      "sender",
      "receiver",
      "referenceName",
      "itemType"
})
public class MessageReferenceDTO {

   private final String name;
   private final String sender;
   private final String receiver;
   private final String referenceName;
   private final String itemType;

   public MessageReferenceDTO(Message message, String referenceName, String itemType) {
      this.name = message.getName();
      this.sender = message.getSender();
      this.receiver = message.getReceiver();
      this.referenceName = referenceName;
      this.itemType = itemType;
   }

   @JsonGetter("name")
   public String getName() {
      return name;
   }

   @JsonGetter("sender")
   public String getSender() {
      return sender;
   }

   @JsonGetter("receiver")
   public String getReceiver() {
      return receiver;
   }

   @JsonGetter("referenceName")
   public String getReferenceName() {
      return referenceName;
   }

   @JsonGetter("itemType")
   public String getItemType() {
      return itemType;
   }
}
