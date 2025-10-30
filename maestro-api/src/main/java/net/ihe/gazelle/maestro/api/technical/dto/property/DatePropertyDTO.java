package net.ihe.gazelle.maestro.api.technical.dto.property;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.ihe.gazelle.maestro.api.business.property.DateProperty;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@JsonTypeName(DatePropertyDTO.TYPE)
public class DatePropertyDTO extends PropertyDTO<DateProperty> {

   public static final String TYPE = "DATE";

   public DatePropertyDTO() {
      super(new DateProperty());
   }

   public DatePropertyDTO(DateProperty property) {
      super(property);
   }

   @JsonProperty(VALUE)
   public String getValue() {
      return ZonedDateTime.ofInstant(property.getValue(), ZoneId.systemDefault())
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
   }

   public void setValue(String value) {
      property.setValue(Instant.from(ZonedDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
   }

   @Override
   public Class<DateProperty> getBusinessObjectClass() {
      return DateProperty.class;
   }
}
