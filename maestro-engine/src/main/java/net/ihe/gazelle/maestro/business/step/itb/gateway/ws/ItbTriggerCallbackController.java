package net.ihe.gazelle.maestro.business.step.itb.gateway.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import net.ihe.gazelle.maestro.business.step.itb.gateway.model.report.*;
import net.ihe.gazelle.maestro.business.step.itb.gateway.service.CatchItbReportService;
import net.ihe.gazelle.maestro.business.step.itb.gateway.service.CatchItbReportServiceException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Adapter to push test report to TM
 */
@Path("/itb")
public class ItbTriggerCallbackController {

    ObjectMapper mapper = new ObjectMapper();

    private List<Data> outputs;
    private final Map<String, Map<String, byte[]>> pdfs = new HashMap<>();

    private CatchItbReportService catchItbReportService;

    @Inject
    public ItbTriggerCallbackController( CatchItbReportService catchItbReportService) {
        this.catchItbReportService = catchItbReportService;
    }

    /**
     * Method to trigger report generation
     *
     * @param json test bed's report
     */
    @POST
    @Path("/report")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response triggerReportGeneration(String json) {
        try {
            // Retrieve useful data from ITB request
            JsonNode inputs = mapper.readTree(json).get("inputs");
            JsonNode systemJson = inputs.get(0).get("item");
            JsonNode sessionJson = inputs.get(1).get("item");
            JsonNode report = inputs.get(2).get("value");
            String sutName = systemJson.get(2).get("value").asText();
            String testSuite = sessionJson.get(0).get("value").asText();
            String sessionID = sessionJson.get(2).get("value").asText();
            String testID = catchItbReportService.getTestID(sessionID);
            outputs = new ArrayList<>();
            pdfs.putIfAbsent(testID, new HashMap<>());
            // modify database with test session report replacing test session identifier
            TestReport tr = setUpReport(report, json, sutName, testSuite, sessionID);
            catchItbReportService.setReport(tr, sessionID);
            Map<String, byte[]> pdf = pdfs.get(testID);
            pdf.put(sessionID + ".pdf", catchItbReportService.requestPDFReport(sessionID, tr.getOverview().getTestSuiteName()));
            pdfs.replace(testID, pdf);
            // when all test session reports have been recovered, merge in one report and push it
            if (catchItbReportService.isFinished(testID)) {
                List<TestReport> reports = catchItbReportService.getReportsByID(testID);
                String[] callbackAndToken = catchItbReportService.getCallbackAndToken(testID);
                TestReport internalReport = mergeReports(reports, testID, testSuite);
                ResponseReport rr = new ResponseReport(internalReport.getResult(), internalReport.getDate(), setUpGlobalReport(internalReport));
                catchItbReportService.pushReport(rr, callbackAndToken, pdfs.get(testID));
                pdfs.remove(testID);
            }
            return Response.status(Response.Status.OK).build();
        } catch (JsonProcessingException je) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Bad format for json body. Error message : " + je.getMessage()).build();
        } catch (IOException | ParserConfigurationException | SAXException | URISyntaxException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Bad format for test bed xml report. Error message : " + e.getMessage()).build();
        } catch (CatchItbReportServiceException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Bad format for test report. Error message : " + e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error while manipulating test sessions. Error message : " + e.getMessage()).build();
        }
    }

    private TestReport setUpGlobalReport(TestReport internalReport) {
        String result = internalReport.getResult();
        TestReport globalReport = new TestReport(internalReport.getUuid(), internalReport.getDate(), result);
        globalReport.setSut(internalReport.getSut());
        globalReport.setTestService(internalReport.getTestService());
        ResultOverview ro = internalReport.getOverview();
        ro.setTestResultRecap(new TestResultRecap(1, !result.equals("SKIPPED") ? 1 : 0, result.equals("PASSED") ? 1 : 0, result.equals("FAILED") ? 1 : 0, result.equals("UNDEFINED") ? 1 : 0, result.equals("SKIPPED") ? 1 : 0));
        globalReport.setOverview(ro);
        TestInfo ti = new TestInfo(globalReport.getOverview().getTestSuiteName(), "1.0.0", new Text("Execution of test suite on an external test bed", "en"));
        Result result1 = new Result(ti, result, 0, globalReport.getDate());
        Data internalData = new Data("Internal Report", internalReport);
        outputs.add(internalData);
        Step itbStep = new Step("0", "Test Bed Execution", "DONE");
        itbStep.setOutputs(outputs);
        result1.setSteps(new ArrayList<>(List.of(itbStep)));
        globalReport.setTestRuns(new ArrayList<>(List.of(result1)));
        return globalReport;
    }

    /**
     * Method to convert ITB report in TestReport object
     *
     * @param report    ITB inner xml report
     * @param json      ITB json report
     * @param sutName   name of the system under test
     * @param testSuite nome of the test suite
     * @param sessionID test session identifier
     * @return TestReport object
     * @throws ParserConfigurationException error from ITB xml report parsing
     * @throws IOException                  error from ITB xml report parsing
     * @throws SAXException                 error from ITB xml report parsing
     */
    private TestReport setUpReport(JsonNode report, String json, String sutName, String testSuite, String sessionID) throws ParserConfigurationException, IOException, SAXException {
        NodeList reportContent = getReportContent(report);
        TestReport tr = new TestReport(sessionID, convertUTCToLDT(reportContent.item(5).getTextContent()), reportContent.item(7).getTextContent());
        TestService ts = new TestService(new EntityID("ITB (Interoperability Test Bed)", "1.0.0"), new Text("Disclaimer : this service is an external service developped by EU", "en"));
        SystemUnderTest sut = new SystemUnderTest(new EntityID(sutName, "1.0.0"));
        List<Result> results = new ArrayList<>();
        results.add(setUpTestRun(reportContent, sessionID, json));
        String status = results.getFirst().getStatus();
        TestResultRecap recap = new TestResultRecap(1, 1,
                status.equals("PASSED") ? 1 : 0,
                status.equals("FAILED") ? 1 : 0,
                status.equals("UNDEFINED") ? 1 : 0, 0);
        ResultOverview overview = new ResultOverview(testSuite, recap);
        tr.setTestService(ts);
        tr.setSut(sut);
        tr.setOverview(overview);
        tr.setTestRuns(results);
        return tr;
    }

    /**
     * Method to get xml report content with DOM structure
     *
     * @param report xml test session report
     * @return content of the report
     * @throws ParserConfigurationException if impossible to instantiate a builder
     * @throws IOException                  if IO operations go wrong
     * @throws SAXException                 if parsing goes wrong
     */
    private NodeList getReportContent(JsonNode report) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(report.asText()));
        Document docReport = builder.parse(is);
        return docReport.getDocumentElement().getChildNodes();
    }

    /**
     * Method to get ITB logs of a test session
     *
     * @param sessionID test session identifier
     * @return ITB logs
     */
    private String getTestLogs(String sessionID) {
        try {
            JsonNode jn = mapper.readTree(catchItbReportService.getTestLogs(sessionID));
            return jn.get("sessions").get(0).get("logs").toString();
        } catch (JsonProcessingException je) {
            throw new ClientErrorException("Bad format for json body for logs request. Error message : " + je.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    /**
     * Method to set up a test run result of a ITB test session
     *
     * @param reportContent ITB report content
     * @param sessionID     ITB test session identifier
     * @param json          ITB report string
     * @return Result object
     */
    private Result setUpTestRun(NodeList reportContent, String sessionID, String json) {
        NodeList testServiceSteps = reportContent.item(9).getChildNodes();
        int failed = 0, undefined = 0;
        Result result = new Result();
        result.setDateTime(convertUTCToLDT(reportContent.item(5).getTextContent()));
        Text description = new Text(reportContent.item(1).getChildNodes().item(5).getTextContent(), "en");
        String testCaseName = reportContent.item(1).getChildNodes().item(1).getTextContent();
        result.setTest(new TestInfo(testCaseName, reportContent.item(1).getChildNodes().item(3).getTextContent(), description));
        List<Step> steps = new ArrayList<>();
        // For every ITB test case step, create a Step object and determine the status
        for (int i = 1; i < testServiceSteps.getLength(); i += 2) {
            Node step = testServiceSteps.item(i);
            String id = step.getAttributes().getNamedItem("id").getTextContent();
            NodeList stepReport = step.getChildNodes().item(3).getChildNodes();
            String stepResult = stepReport.item(3).getTextContent();
            steps.add(new Step(id, "ITBStep", stepResult));
            if (stepResult.equals("FAILURE")) failed++;
            else if (stepResult.equals("UNDEFINED")) undefined++;
        }
        // Compute test case global result
        String resultReport = "PASSED";
        if (failed > 0) resultReport = "FAILED";
        else if (undefined > 0) resultReport = "UNDEFINED";
        result.setStatus(resultReport);
        result.setSteps(steps);
        List<Data> data = new ArrayList<>();
        data.add(new Data("ITB test session logs - " + testCaseName, getTestLogs(sessionID)));
        data.add(new Data("ITB test session original report - " + testCaseName, json));
        data.add(new Data("ITB test session PDF report - " + testCaseName, testCaseName + ".pdf"));
        data.add(new Data("ITB test session link - " + testCaseName, System.getenv("TEST_SERVICE") + "/app#/admin/sessions?session=" + sessionID));
        outputs.addAll(data);
        return result;
    }

    /**
     * Method to merge all test session report in one
     *
     * @param reports   list of reports
     * @param testID    TM test identifier
     * @param testSuite name of the test suite
     * @return final test report
     * @throws JsonProcessingException if output conversion goes wrong
     */
    private TestReport mergeReports(List<TestReport> reports, String testID, String testSuite) throws JsonProcessingException {
        // Set up common part
        TestReport finalReport = new TestReport(testID, reports.getLast().getDate());
        finalReport.setTestService(reports.getFirst().getTestService());
        finalReport.setSut(reports.getFirst().getSut());
        List<Result> completeResults = new ArrayList<>();
        int passed = 0, failed = 0, undefined = 0, skipped = 0, run, total;
        int instanceId = 0;
        // For each test report
        for (TestReport testReport : reports) {
            // compute sum of test case steps
            TestResultRecap trr = testReport.getOverview().getTestResultRecap();
            passed += trr.getPassed();
            failed += trr.getFailed();
            undefined += trr.getUndefined();
            skipped += trr.getSkipped();
            // add instance id to every test case
            List<Result> testCaseResults = testReport.getTestRuns();
            for (int i = 0; i < testCaseResults.size(); i++) {
                Result r = testCaseResults.get(i);
                r.setInstanceId(instanceId++);
                testCaseResults.set(i, r);
            }
            completeResults.addAll(testCaseResults);
        }
        // Compute test suite global result
        run = passed + failed + undefined;
        total = run + skipped;
        String result = "PASSED";
        if (reports.isEmpty() || undefined > 0) result = "UNDEFINED";
        else if (failed > 0) result = "FAILED";
        TestResultRecap finalTRR = new TestResultRecap(total, run, passed, failed, undefined, skipped);
        ResultOverview overview = new ResultOverview(testSuite, finalTRR);
        finalReport.setOverview(overview);
        finalReport.setResult(result);
        finalReport.setTestRuns(completeResults);
        catchItbReportService.deleteEndedTest(testID);
        return finalReport;
    }

    private String convertUTCToLDT(String dateUTC) {
        ZonedDateTime utcDateTime = ZonedDateTime.parse(dateUTC, DateTimeFormatter.ISO_DATE_TIME);
        ZonedDateTime localDateTime = utcDateTime.withZoneSameInstant(ZoneId.of("Europe/Paris"));
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }

    public void setPushReportUseCase(CatchItbReportService catchItbReportService) {
        this.catchItbReportService = catchItbReportService;
    }
}
