package net.ihe.gazelle.maestro.api.technical.dto.test;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.ihe.gazelle.maestro.api.business.test.SupportedFileInput;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
      name = "SupportedFileInput",
      description = "A File input that can be used as a step or test input."
)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName(SupportedFileInputDTO.TYPE)
public class SupportedFileInputDTO extends SupportedInputDTO<SupportedFileInput> {

   @JsonIgnore
   public static final String TYPE = "FILE";

   public SupportedFileInputDTO() {
      super(new SupportedFileInput());
   }

   public SupportedFileInputDTO(SupportedFileInput supportedInput) {
      super(supportedInput);
   }

   @Override
   @JsonIgnore
   public Class<SupportedFileInput> getBusinessObjectClass() {
      return SupportedFileInput.class;
   }

}
