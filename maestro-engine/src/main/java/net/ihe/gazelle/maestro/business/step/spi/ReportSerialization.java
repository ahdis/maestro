package net.ihe.gazelle.maestro.business.step.spi;

import net.ihe.gazelle.simulation.business.callback.SimulationReport;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;

public interface ReportSerialization {

    /**
     * Convert a ValidationReport object to byte array
     * @param report object to convert
     * @return same report as byte array
     * @throws IllegalStateException if report can't be converted
     */
    byte[] validationReportToByteArray(ValidationReport report);

    /**
     * Convert a ValidationReport object to byte array
     * @param report object to convert
     * @return same report as byte array
     * @throws IllegalStateException if report can't be converted
     */
    byte[] simulationReportToByteArray(SimulationReport report);
}
