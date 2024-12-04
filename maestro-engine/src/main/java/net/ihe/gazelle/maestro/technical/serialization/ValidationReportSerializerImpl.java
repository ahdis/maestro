package net.ihe.gazelle.maestro.technical.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ihe.gazelle.maestro.business.step.validation.spi.ValidationReportSerializer;
import net.ihe.gazelle.validation.api.domain.report.structure.ValidationReport;
import net.ihe.gazelle.validation.interlay.dto.report.ValidationReportDTO;

import java.io.IOException;

/**
 * Provided implementation for serializing validation report in Validation Step runner
 */
public class ValidationReportSerializerImpl implements ValidationReportSerializer {

    @Override
    public byte[] toByteArray(ValidationReport report) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ValidationReportDTO dto = new ValidationReportDTO(report);

            return mapper.writeValueAsString(dto).getBytes();
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
}
