package net.ihe.gazelle.maestro.quarkus.websocket;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.websocket.SendResult;
import jakarta.websocket.Session;
import net.ihe.gazelle.maestro.api.business.message.StepRunFinished;
import net.ihe.gazelle.maestro.api.business.message.TestSuiteRunFinished;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportSummary;
import net.ihe.gazelle.maestro.api.technical.dto.report.StepRunReportDTO;
import net.ihe.gazelle.maestro.api.technical.dto.report.TestReportSummaryDTO;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@ApplicationScoped
public class WebsocketEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(WebsocketEventListener.class);

    private final TextSerDes serDes = new JacksonSerDes();

    public WebsocketEventListener() {
    }

    public void onFinishTestSet(@ObservesAsync TestSuiteRunFinished testSuiteRunFinished) throws IOException {
        String testSetId = testSuiteRunFinished.getTestSuiteRun().getInternalId();
        if (WebsocketSessionsInMemory.hasSession(testSetId)) {
            sendTestRunCompleted(testSuiteRunFinished);
        }
    }

    public void onFinishStep(@ObservesAsync StepRunFinished stepRunFinished) {
        if (WebsocketSessionsInMemory.hasSession(stepRunFinished.getStepRun().testSetId())) {
            sendTestStepCompleted(stepRunFinished);
        }
    }

    private void sendTestRunCompleted(TestSuiteRunFinished testSuiteRunFinished) throws IOException {
        String testSetId = testSuiteRunFinished.getTestSuiteRun().getInternalId();
        TestReportSummary testReportSummary = testSuiteRunFinished.getReportSummary();
        String content = serDes.serializeAsString(new TestReportSummaryDTO(testReportSummary));
        WSMessageDTO message = new WSMessageDTO(testSetId, "testRunCompleted", content);
        String serializedMessage = serDes.serializeAsString(message);
        LOG.info("Sending TestRunReport for testSetId : {}", testSetId);
        Session session = WebsocketSessionsInMemory.getSession(testSetId);
        if (session != null) {
            session.getAsyncRemote()
                    .sendText(serializedMessage, result -> logPotentialException(testSetId, result));
            session.close();
        }
    }

    private void sendTestStepCompleted(StepRunFinished stepRunFinished) {
        String testSetId = stepRunFinished.getStepRun().testSetId();
        StepRunReport stepRunReport = stepRunFinished.getStepRunReport().build();
        String content = serDes.serializeAsString(new StepRunReportDTO(stepRunReport));
        WSMessageDTO message = new WSMessageDTO(testSetId, "testStepCompleted", content);
        String serializedMessage = serDes.serializeAsString(message);
        LOG.info("Sending TestStepRunReport for testSetId : {}", testSetId);
        Session session = WebsocketSessionsInMemory.getSession(testSetId);
        if (session != null) {
            session.getAsyncRemote()
                    .sendText(serializedMessage, result -> logPotentialException(testSetId, result));
        }
    }


    private static void logPotentialException(String testSetId, SendResult result) {
        if (result.getException() != null) {
            LOG.error("Unable to send message to {}: {}", testSetId, result.getException().getMessage());
        }
    }
}
