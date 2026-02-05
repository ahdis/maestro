package net.ihe.gazelle.maestro.client;

import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;

import java.net.URI;

public interface MaestroClient {

    TestReport executeTest(TestRun testRun, boolean persist);

    TestReport executeTestSuite(TestSuiteRun testSuiteRun, boolean persist);

    void executeTestAsync(TestRun testRun, boolean persist, URI callback);

    void executeTestSuiteAsync(TestSuiteRun testSuiteRun, boolean persist, URI callback);

}
