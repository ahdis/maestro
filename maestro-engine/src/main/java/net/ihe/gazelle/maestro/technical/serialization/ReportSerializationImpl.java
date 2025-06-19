package net.ihe.gazelle.maestro.technical.serialization;

import net.ihe.gazelle.maestro.business.step.spi.ReportSerialization;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.serialization.SerDes;
import net.ihe.gazelle.modelmarshaller.technical.serialization.SerializationException;
import net.ihe.gazelle.simulation.business.callback.SimulationReport;
import net.ihe.gazelle.simulation.technical.jaxrs.dto.callback.SimulationReportDTO;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;
import net.ihe.gazelle.validation.v2.api.technical.dto.report.ValidationReportDTO;

/**
 * Provided implementation for serializing validation report in Validation Step runner
 */
public class ReportSerializationImpl implements ReportSerialization {

    @Override
    public byte[] validationReportToByteArray(ValidationReport report) {
        try {
            SerDes serDes = new JacksonSerDes();
            ValidationReportDTO dto = new ValidationReportDTO(report);

            return serDes.serialize(dto);
        } catch (SerializationException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    @Override
    public byte[] simulationReportToByteArray(SimulationReport report) {
        try {
            SerDes serDes = new JacksonSerDes();
            SimulationReportDTO dto = new SimulationReportDTO(report);

            return serDes.serialize(dto);
        } catch (SerializationException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
}
