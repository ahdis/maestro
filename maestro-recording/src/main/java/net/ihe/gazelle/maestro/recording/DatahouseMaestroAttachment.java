package net.ihe.gazelle.maestro.recording;

import com.kereval.gazelle.datahouse.api.business.record.Attachment;
import net.ihe.gazelle.maestro.api.business.recording.MaestroAttachment;

import java.util.Arrays;

public class DatahouseMaestroAttachment implements MaestroAttachment {

   private final Attachment attachment;

   public DatahouseMaestroAttachment(Attachment attachment) {
      this.attachment = attachment;
   }

   public DatahouseMaestroAttachment() {
     this.attachment = new Attachment();
   }

   public DatahouseMaestroAttachment setContent(byte[] content) {
      this.attachment.setContent(content != null ? Arrays.copyOf(content, content.length) : null);
      return this;
   }

   public DatahouseMaestroAttachment setType(String type) {
      this.attachment.setType(type);
      return this;
   }

   public DatahouseMaestroAttachment setFilename(String filename) {
      this.attachment.setFilename(filename);
      return this;
   }

   @Override
   public byte[] getContent() {
      byte[] content = this.attachment.getContent();
      return content != null ? Arrays.copyOf(content, content.length) : null;
   }

   @Override
   public String getType() {
      return attachment.getType();
   }

   @Override
   public String filename() {
      return attachment.getFilename();
   }

   public Attachment toDatahouseAttachment() {
      return this.attachment;
   }

}
