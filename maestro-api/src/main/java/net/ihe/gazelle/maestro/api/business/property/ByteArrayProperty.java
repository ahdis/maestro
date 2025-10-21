package net.ihe.gazelle.maestro.api.business.property;

public class ByteArrayProperty extends Property {

   private String fileName;

   public ByteArrayProperty() {
      super();
   }

   public ByteArrayProperty(String name, Object value) {
      super(name, value);
   }

   @Override
   public byte[] getValue() {
      return super.getValue();
   }

   public String getFileName() {
      return fileName;
   }

   public ByteArrayProperty setFileName(String fileName) {
      this.fileName = fileName;
      return this;
   }

}
