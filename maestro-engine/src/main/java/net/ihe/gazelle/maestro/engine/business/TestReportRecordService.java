package net.ihe.gazelle.maestro.engine.business;

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

    /**
     * A reference to a recorded item with its id and location.
     *
     * @param id       the id of the item
     * @param location the location URL of the item
     */
    record ItemReference(String id, String location){}

}
