package net.ihe.gazelle.maestro.business.step.validation.spi;

import net.ihe.gazelle.validation.api.domain.report.structure.ValidationReport;

public interface ValidationReportSerializer {

    /**
     * Convert a ValidationReport object to byte array
     * @param report object to convert
     * @return same report as byte array
     * @throws IllegalStateException if report can't be converted
     */
    byte[] toByteArray(ValidationReport report);
}
