package net.ihe.gazelle.maestro.api.business.property;

import java.io.Serial;

public class ByteArrayProperty extends Property {

   @Serial
   private static final long serialVersionUID = -2541331305238610805L;

   private String fileName;
   private String mimeType;

   public ByteArrayProperty() {
      super();
   }

   public ByteArrayProperty(String name, Object value) {
      super(name, value);
   }

   public String getFileName() {
      return fileName;
   }

   public ByteArrayProperty setFileName(String fileName) {
      this.fileName = fileName;
      return this;
   }

   public String getMimeType() {
      return mimeType;
   }

   public ByteArrayProperty setMimeType(String mimeType) {
      this.mimeType = mimeType;
      return this;
   }

   @Override
   public Property copy() {
      return new ByteArrayProperty(getName(), getValue())
            .setFileName(getFileName())
            .setMimeType(getMimeType());
   }
}
