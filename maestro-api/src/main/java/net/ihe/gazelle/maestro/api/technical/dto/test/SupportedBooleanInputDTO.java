package net.ihe.gazelle.maestro.api.technical.dto.test;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.ihe.gazelle.maestro.api.business.test.SupportedBooleanInput;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
      name = "SupportedBooleanInput",
      description = "A boolean input that can be used as a step or test input."
)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName(SupportedBooleanInputDTO.TYPE)
public class SupportedBooleanInputDTO extends SupportedInputDTO<SupportedBooleanInput> {

   @JsonIgnore
   public static final String TYPE = "BOOLEAN";

   public SupportedBooleanInputDTO() {
      super(new SupportedBooleanInput());
   }

   public SupportedBooleanInputDTO(SupportedBooleanInput supportedInput) {
      super(supportedInput);
   }

   @Override
   @JsonIgnore
   public Class<SupportedBooleanInput> getBusinessObjectClass() {
      return SupportedBooleanInput.class;
   }

}
