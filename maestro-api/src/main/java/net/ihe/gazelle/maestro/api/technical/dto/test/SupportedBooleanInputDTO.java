package net.ihe.gazelle.maestro.api.technical.dto.test;

import com.fasterxml.jackson.annotation.JsonTypeName;
import net.ihe.gazelle.maestro.api.business.test.SupportedBooleanInput;

@JsonTypeName(SupportedBooleanInputDTO.TYPE)
public class SupportedBooleanInputDTO extends SupportedInputDTO<SupportedBooleanInput> {

   public static final String TYPE = "BOOLEAN";

   public SupportedBooleanInputDTO() {
      super(new SupportedBooleanInput());
   }

   public SupportedBooleanInputDTO(SupportedBooleanInput supportedInput) {
      super(supportedInput);
   }

   @Override
   public Class<SupportedBooleanInput> getBusinessObjectClass() {
      return SupportedBooleanInput.class;
   }

}
