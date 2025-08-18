package net.ihe.gazelle.maestro.business;

import net.ihe.gazelle.maestro.api.business.testreport.TestReport;

public interface TestReportRecordService {

    /**
     * Records a TestReport and all its references.
     *
     * @param report the TestReport to record
     * @throws TestReportRecordException if it can't record the TestReport
     * @return the id of the recorded TestReport
     */

    String recordTestReportWithReferences(TestReport report);
}
