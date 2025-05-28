package net.ihe.gazelle.maestro.business.step.itb.gateway.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.testreport.ItemBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.runner.FinishedStepEvent;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepMessage;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;
import net.ihe.gazelle.maestro.business.step.itb.gateway.client.ItbHttpClient;
import net.ihe.gazelle.maestro.business.step.itb.gateway.dao.TestExecutionDAOStatic;
import net.ihe.gazelle.maestro.business.step.itb.gateway.model.report.*;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.io.IOException;
import java.util.*;

@Dependent
@Default
public class CatchItbReportService {

    private final Emitter<StepMessage> emitter;
    private final ItbHttpClient itbHttpClient;

    @Inject
    public CatchItbReportService(@Channel("step-run") Emitter<StepMessage> emitter, ItbHttpClient itbHttpClient) {
        this.itbHttpClient = itbHttpClient;
        this.emitter = emitter;
    }

    /**
     * Method to validate an identifier format for test report
     * @param eid identifier to validate
     * @param name identifier description
     */
    private void validateEntityID(EntityID eid, String name) {
        if(Objects.isNull(eid)) throw new CatchItbReportServiceException(name+" ID object is missing");
        if(Objects.isNull(eid.getId()) || eid.getId().isEmpty()) throw new CatchItbReportServiceException(name+" ID is missing");
        if(Objects.isNull(eid.getVersion())) throw new CatchItbReportServiceException(name+" version is missing");
        if(!eid.getVersion().matches("^([1-9]\\d*|0)(\\.(([1-9]\\d*)|0)){1,2}$"))  throw new CatchItbReportServiceException("Wrong format for "+name+" version");
    }

    /**
     * Method to validate format of a text component of the report
     * @param txt component to validate
     * @param name text description
     */
    private void validateText(Text txt, String name) {
        if(Objects.isNull(txt)) throw new CatchItbReportServiceException(name+" is missing");
        if(Objects.isNull(txt.getValue()) || txt.getValue().isEmpty()) throw new CatchItbReportServiceException(name+" text is missing");
        if(Objects.nonNull(txt.getLang())) {
            if(txt.getLang().length() != 2) throw new CatchItbReportServiceException("Wrong format for "+name+" language");
        }
    }

    /**
     * Method to validate test service description in the report
     * @param ts test service description
     */
    private void validateTestService(TestService ts) throws CatchItbReportServiceException {
        if(Objects.isNull(ts)) throw new CatchItbReportServiceException("Test service declaration is missing");
        validateEntityID(ts.getServiceID(), "Test Service");
        validateText(ts.getDisclaimer(),"Test service disclaimer");

    }

    /**
     * Method to validate SUT description in the report
     * @param sut SUT description
     */
    private void validateSUT(SystemUnderTest sut) {
        if(Objects.isNull(sut)) throw new CatchItbReportServiceException("SUT is missing");
        validateEntityID(sut.getSystemID(), "System Under Test");
        List<String> macAddresses = sut.getMacAddresses();
        if(Objects.nonNull(macAddresses) && !macAddresses.isEmpty()) {
            for(String sutAddress : macAddresses) {
                if(!sutAddress.matches("^([0-9A-Fa-f]{2}(:[0-9A-Fa-f]{2}){5}|[0-9A-Fa-f]{2}(-[0-9A-Fa-f]{2}){5})$")) throw new CatchItbReportServiceException("Wrong format for system under test MAC address : "+sutAddress);
            }
        }

        List<String> ipAddresses = sut.getIpAddresses();
        if(Objects.nonNull(ipAddresses) && !ipAddresses.isEmpty()) {
            for(String sutAddress : ipAddresses) {
                if(!sutAddress.matches("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")
                        && !sutAddress.matches("^((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){1,6}:)|(([0-9A-Fa-f]{1,4}:){1,5}(:[0-9A-Fa-f]{1,4}){1,2})|(([0-9A-Fa-f]{1,4}:){1,4}(:[0-9A-Fa-f]{1,4}){1,3})|(([0-9A-Fa-f]{1,4}:){1,3}(:[0-9A-Fa-f]{1,4}){1,4})|(([0-9A-Fa-f]{1,4}:){1,2}(:[0-9A-Fa-f]{1,4}){1,5})|([0-9A-Fa-f]{1,4}:((:[0-9A-Fa-f]{1,4}){1,6}))|(:(:[0-9A-Fa-f]{1,4}){1,7}|:))$")) throw new CatchItbReportServiceException("Wrong format for system under test IP address : "+sutAddress);
            }
        }

        List<String> hostnames = sut.getHostNames();
        if(Objects.nonNull(hostnames) && !hostnames.isEmpty()) {
            for(String hostname : hostnames) {
                if(!hostname.matches("^(?=.{1,253}$)(?:(?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.?)+$")) throw new CatchItbReportServiceException("Wrong format for system under test hostname : "+hostname);
            }
        }
    }

    /**
     * Method to validate results overview format in the test report
     * @param ro result overview to validate
     */
    private void validateOverview(ResultOverview ro)  {
        if(Objects.isNull(ro)) throw new CatchItbReportServiceException("Result overview is missing");
        if(Objects.isNull(ro.getTestSuiteName()) || ro.getTestSuiteName().isEmpty()) throw new CatchItbReportServiceException("Test suite name is missing");
        TestResultRecap recap = ro.getTestResultRecap();
        if(Objects.isNull(recap)) throw new CatchItbReportServiceException("Result recap is missing");
        if(recap.getTotal() != recap.getRun() + recap.getSkipped()) throw new CatchItbReportServiceException("Inconsistent value for the total number of tests");
        if(recap.getRun() != recap.getPassed() + recap.getFailed() + recap.getUndefined()) throw new CatchItbReportServiceException("Inconsistent value for the number of tests run");
        if(Objects.nonNull(ro.getNotes()) && !ro.getNotes().isEmpty()) {
            for(Text txt : ro.getNotes()) {
                validateText(txt, "Overview Note");
            }
        }
        if(Objects.nonNull(ro.getUrlToTestSuiteResult())) {
            if(!ro.getUrlToTestSuiteResult().matches("^(http|https)://([A-Za-z0-9.-]+)(:[0-9]+)?(/[^=?]+)?(\\?([a-z]*=[a-zA-Z0-9]*)(&[a-zA-Z0-9]*=[a-zA-Z0-9]*)*)?$")) throw new CatchItbReportServiceException("Wrong format for url to test result");
        }
    }

    /**
     * Method to validate a single test case information in results
     * @param ti test information to validate
     */
    private void validateTestInfo(TestInfo ti) {
        if(Objects.isNull(ti)) throw new CatchItbReportServiceException("Test case info is missing");
        if(Objects.isNull(ti.getName()) || ti.getName().isEmpty()) throw new CatchItbReportServiceException("Test case name is missing");
        if(!ti.getVersion().matches("^([1-9]\\d*|0)(\\.(([1-9]\\d*)|0)){1,2}$"))  throw new CatchItbReportServiceException("Wrong format for test case version");
        validateText(ti.getDescription(), "Test case description");
    }

    /**
     * Method to validate detailed results format in a test report
     * @param results test results to validate
     */
    private void validateResults(List<Result> results) {
        if(Objects.nonNull(results) && !results.isEmpty()) {
            for(Result rs : results) {
                validateTestInfo(rs.getTest());
                if(Objects.nonNull(rs.getSimulatorID())) {
                    validateEntityID(rs.getSimulatorID(),"Used simulator");
                }
                if(!Arrays.asList("PASSED","FAILED","SKIPPED","UNDEFINED").contains(rs.getStatus())) throw new CatchItbReportServiceException("Wrong format for test case status");
                if(Objects.nonNull(rs.getNotes()) && !rs.getNotes().isEmpty()) {
                    for(Text txt : rs.getNotes()) {
                        validateText(txt, "Test Case Note");
                    }
                }
                if(Objects.nonNull(rs.getUrlToTestInstance())) {
                    if(!rs.getUrlToTestInstance().matches("^(http|https)://([A-Za-z0-9.-]+)(:[0-9]+)?(/[^=?]+)?(\\?([a-z]*=[a-zA-Z0-9]*)(&[a-zA-Z0-9]*=[a-zA-Z0-9]*)*)?$")) throw new CatchItbReportServiceException("Wrong format for url to test case result");
                }
                if(!rs.getDateTime().matches("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(\\.[0-9]+)?([Zz]|([+-])([01]\\d|2[0-3]):?([0-5]\\d)?)?")) throw new CatchItbReportServiceException("Wrong format for datetime test case property");
            }
        }
    }

    /**
     * Method to validate a test report
     * @param report test report to validate
     */
    private void validateReport(TestReport report) {
        if(Objects.isNull(report)) throw new CatchItbReportServiceException("Report is null");
        if(Objects.isNull(report.getUuid()) || report.getUuid().isEmpty()) throw new CatchItbReportServiceException("Test session ID is missing");
        try {
            UUID.fromString(report.getUuid());
        } catch (IllegalArgumentException e) {
            throw new CatchItbReportServiceException("Wrong format for test session ID : "+e);
        }

        if(Objects.isNull(report.getDate())) throw new CatchItbReportServiceException("Date is missing");
        if(!report.getDate().matches("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(\\.[0-9]+)?([Zz]|([+-])([01]\\d|2[0-3]):?([0-5]\\d)?)?")) throw new CatchItbReportServiceException("Wrong format for datetime test session property");

        if(Objects.isNull(report.getResult()) || report.getResult().isEmpty()) throw new CatchItbReportServiceException("Test suite status is missing");
        if(!Arrays.asList("PASSED","FAILED","UNDEFINED").contains(report.getResult())) throw new CatchItbReportServiceException("Wrong format for test suite status");

        validateTestService(report.getTestService());
        validateSUT(report.getSut());
        validateOverview(report.getOverview());
        validateResults(report.getTestRuns());
    }

    public ItbHttpClient getItbClient() {
        return itbHttpClient;
    }

    public String getTestID(String sessionID) {
        return TestExecutionDAOStatic.getStepRun(sessionID).testRunId();
    }

    public StepRun getStepRun(String sessionID) {
        return TestExecutionDAOStatic.getStepRun(sessionID);
    }

    public byte[] requestPDFReport(String sessionID, String testCaseName) {
       return getItbClient().requestPDFReport(sessionID, testCaseName);
    }

    public String getTestLogs(String sessionID) {
        return getItbClient().getTestLogs(sessionID);
    }

    /**
     * Method to push report to the messaging channel
     * @param responseReport test report to push
     */
    public void pushReport(ResponseReport responseReport, String[] callbackAndToken, Map<String, byte[]> pdfs) throws IOException {
        validateReport(responseReport.getReport());
        StepRunReportBuilder stepRunReportBuilder = new StepRunReportBuilder()
                .setResult(StepResult.PASSED);

        for (Map.Entry<String, byte[]> entry : pdfs.entrySet()) {
            String testCaseName = entry.getKey();
            byte[] pdfContent = entry.getValue();
            ItemBuilder itemBuilder = new ItemBuilder()
                    .setContent(pdfContent)
                    .setName(testCaseName);
            stepRunReportBuilder.addOutput(itemBuilder);
        }

        TestExecutionDAOStatic.getStepRun(responseReport.getReport().getUuid());
        StepRun stepRun = new StepRun(null,null);
          emitter.send(new FinishedStepEvent(stepRunReportBuilder, stepRun));
    }

    public void pushReport(StepRun stepRun) {
        StepRunReportBuilder stepRunReportBuilder = new StepRunReportBuilder()
                .setResult(StepResult.PASSED);

        emitter.send(new FinishedStepEvent(stepRunReportBuilder, stepRun));
    }
}
