package net.ihe.gazelle.maestro.api.technical.dto.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.ihe.gazelle.maestro.api.business.test.SupportedFileInput;
import net.ihe.gazelle.maestro.api.business.test.SupportedTextInput;

import java.util.List;

@JsonTypeName(SupportedFileInputDTO.TYPE)
public class SupportedFileInputDTO extends SupportedInputDTO<SupportedFileInput> {

   public static final String TYPE = "FILE";

   public SupportedFileInputDTO() {
      super(new SupportedFileInput());
   }

   public SupportedFileInputDTO(SupportedFileInput supportedInput) {
      super(supportedInput);
   }

   @Override
   public Class<SupportedFileInput> getBusinessObjectClass() {
      return SupportedFileInput.class;
   }

}
