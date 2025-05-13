package net.ihe.gazelle.maestro.business.step.itb.gateway.dao;

import net.ihe.gazelle.maestro.business.step.itb.gateway.model.report.TestReport;

import java.util.List;

public interface TestExecutionDAO {

    void writeSessions(String testID, String response, String callbackURL, String token);

    String getTestID(String sessionID);

    void setReport(TestReport testReport, String sessionID);

    boolean isFinished(String testID);

    List<TestReport> getReportsByID(String testID);

    void deleteEndedTest(String testID);

    String[] getCallbackAndToken(String testID);
}
