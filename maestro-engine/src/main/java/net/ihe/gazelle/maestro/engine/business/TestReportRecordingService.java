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
    * @return the location of the recorded TestReport
    * @throws TestReportRecordingException if it can't record the TestReport
    */
   String recordTestReport(TestReport report);

}
