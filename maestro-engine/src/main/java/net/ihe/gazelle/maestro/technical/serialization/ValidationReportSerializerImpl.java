package net.ihe.gazelle.maestro.technical.serialization;

import net.ihe.gazelle.maestro.business.step.validation.spi.ValidationReportSerializer;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.serialization.SerDes;
import net.ihe.gazelle.modelmarshaller.technical.serialization.SerializationException;
import net.ihe.gazelle.validation.api.business.report.ValidationReport;
import net.ihe.gazelle.validation.api.technical.dto.report.ValidationReportDTO;

/**
 * Provided implementation for serializing validation report in Validation Step runner
 */
public class ValidationReportSerializerImpl implements ValidationReportSerializer {

    @Override
    public byte[] toByteArray(ValidationReport report) {
        try {
            SerDes serDes = new JacksonSerDes();
            ValidationReportDTO dto = new ValidationReportDTO(report);

            return serDes.serialize(dto);
        } catch (SerializationException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
}
