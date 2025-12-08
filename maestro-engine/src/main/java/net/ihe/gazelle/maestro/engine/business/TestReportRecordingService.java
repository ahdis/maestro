package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.maestro.api.business.testreport.TestReport;

/**
 * Service to record a TestReport and all its references.
 */
public interface TestReportRecordingService {

    /**
     * Records a TestReport and all its references.
     *
     * @param report the TestReport to record
     * @throws TestReportRecordingException if it can't record the TestReport
     * @return the location of the recorded TestReport
     */
    String recordTestReport(TestReport report);

}
