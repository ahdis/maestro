package net.ihe.gazelle.maestro.api.technical.dto;

import net.ihe.gazelle.errorhandling.business.UnexpectedError;
import net.ihe.gazelle.errorhandling.technical.UnexpectedErrorDTO;
import net.ihe.gazelle.maestro.api.business.testreport.Result;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportSummary;
import net.ihe.gazelle.maestro.api.technical.dto.report.TestReportSummaryDTO;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TestReportSummaryDTOTest {

   private static final TextSerDes SER_DES = new JacksonSerDes();
   private static final Path TEST_REPORT_SUMMARY_JSON = Path.of("src/test/resources/testReport/test-report-summary.json");

   @Test
   void shouldExposeFormattedDateTimeAndUnexpectedErrors() {
      Instant instant = Instant.parse("2025-05-13T10:15:30Z");
      TestReportSummary summary = new TestReportSummary()
            .setId("summary-1")
            .setLocation("archive")
            .setDateTime(instant)
            .setResult(Result.PASSED)
            .addUnexpectedError(new UnexpectedError().setName("warning").setMessage("minor issue"));

      TestReportSummaryDTO dto = new TestReportSummaryDTO(summary);

      assertEquals("summary-1", dto.getId());
      assertEquals("archive", dto.getLocation());
      assertEquals(Result.PASSED, dto.getResult());

      Instant parsedInstant = ZonedDateTime.parse(dto.getDateTime(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant();
      assertEquals(instant, parsedInstant);

      List<UnexpectedErrorDTO> unexpectedErrorDTOs = dto.getUnexpectedErrors();
      assertEquals(1, unexpectedErrorDTOs.size());
      UnexpectedErrorDTO errorDTO = unexpectedErrorDTOs.getFirst();
      assertEquals("warning", errorDTO.getName());
      assertEquals("minor issue", errorDTO.getMessage());
   }

   @Test
   void shouldPopulateBusinessObjectFromSerializedValues() {
      TestReportSummaryDTO dto = new TestReportSummaryDTO();
      dto.setId("summary-2");
      dto.setLocation("remote");

      String dateTime = "2025-01-01T12:00:00+02:00";
      dto.setDateTime(dateTime);
      dto.setResult(Result.FAILED);

      UnexpectedErrorDTO unexpectedErrorDTO = new UnexpectedErrorDTO(
            new UnexpectedError().setName("failure").setMessage("critical issue"));
      dto.setUnexpectedErrors(List.of(unexpectedErrorDTO));

      TestReportSummary businessObject = dto.getBusinessObject();
      assertEquals("summary-2", businessObject.getId());
      assertEquals("remote", businessObject.getLocation());
      assertEquals(Result.FAILED, businessObject.getResult());

      Instant expectedInstant = ZonedDateTime.parse(dateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant();
      assertEquals(expectedInstant, businessObject.getDateTime());

      assertEquals(1, businessObject.getUnexpectedErrors().size());
      UnexpectedError unexpectedError = businessObject.getUnexpectedErrors().getFirst();
      assertEquals("failure", unexpectedError.getName());
      assertEquals("critical issue", unexpectedError.getMessage());

      dto.setUnexpectedErrors(List.of());
      assertNotNull(businessObject.getUnexpectedErrors());
      assertTrue(businessObject.getUnexpectedErrors().isEmpty());
   }

   @Test
   @SuppressWarnings("unchecked")
   void shouldSerializeToJson() {
      TestReportSummary summary = new TestReportSummary()
            .setId("summary-serialize")
            .setLocation("storage")
            .setResult(Result.PASSED)
            .setDateTime(Instant.parse("2025-06-01T08:30:00Z"))
            .setUnexpectedErrors(List.of(
                  new UnexpectedError().setName("info").setMessage("for reference")
            ));

      String json = assertDoesNotThrow(() -> SER_DES.serializeAsString(new TestReportSummaryDTO(summary)));
      Map<String, Object> payload = assertDoesNotThrow(() -> SER_DES.deserialize(json, Map.class));

      assertEquals("summary-serialize", payload.get("id"));
      assertEquals("storage", payload.get("location"));
      assertEquals("PASSED", payload.get("result"));
      String serializedDateTime = (String) payload.get("dateTime");
      Instant serializedInstant = ZonedDateTime.parse(serializedDateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant();
      assertEquals(Instant.parse("2025-06-01T08:30:00Z"), serializedInstant);

      List<Map<String, Object>> unexpectedErrors = (List<Map<String, Object>>) payload.get("unexpectedErrors");
      assertEquals(1, unexpectedErrors.size());
      assertEquals("info", unexpectedErrors.getFirst().get("name"));
   }

   @Test
   void shouldDeserializeFromFixtureJson() {
      String json = assertDoesNotThrow(() -> Files.readString(TEST_REPORT_SUMMARY_JSON));
      TestReportSummaryDTO dto = assertDoesNotThrow(() -> SER_DES.deserialize(json, TestReportSummaryDTO.class));

      TestReportSummary restored = dto.getBusinessObject();
      assertEquals("summary-fixture", restored.getId());
      assertEquals("fixture-location", restored.getLocation());
      assertEquals(Result.FAILED, restored.getResult());
      assertEquals(Instant.parse("2025-04-12T09:15:30Z"), restored.getDateTime()); // timezone handling
      assertEquals(1, restored.getUnexpectedErrors().size());
      UnexpectedError unexpectedError = restored.getUnexpectedErrors().getFirst();
      assertEquals("error", unexpectedError.getName());
      assertEquals("something failed", unexpectedError.getMessage());
   }
}
