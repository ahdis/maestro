package net.ihe.gazelle.maestro.api.business.recording;

import net.ihe.gazelle.maestro.api.business.testreport.TestReport;

/**
 * Service to record a TestReport and all its references.
 */
public interface TestReportRecordService {

    /**
     * Records a TestReport and all its references.
     *
     * @param report the TestReport to record
     * @throws TestReportRecordException if it can't record the TestReport
     * @return the id of the recorded TestReport
     */
    ItemReference recordTestReportWithReferences(TestReport report);

}
