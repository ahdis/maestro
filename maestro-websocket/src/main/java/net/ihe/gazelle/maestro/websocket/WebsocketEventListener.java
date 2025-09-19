package net.ihe.gazelle.maestro.websocket;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import jakarta.websocket.SendResult;
import jakarta.websocket.Session;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportSummary;
import net.ihe.gazelle.maestro.api.business.testrun.runner.FinishedStepEvent;
import net.ihe.gazelle.maestro.api.technical.dto.report.StepRunReportDTO;
import net.ihe.gazelle.maestro.api.technical.dto.report.TestReportSummaryDTO;
import net.ihe.gazelle.maestro.api.technical.event.FinishTestSetEvent;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.jackson.ObjectMapperBuilder;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class WebsocketEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(WebsocketEventListener.class);

    private final TextSerDes serDes = new JacksonSerDes(new ObjectMapperBuilder().build());

    private final String datahouseUr;

    @Inject
    public WebsocketEventListener(@ConfigProperty(name = "datahouse.url") String datahouseUr) {
        this.datahouseUr = datahouseUr;
    }


    public void onFinishTestSet(@ObservesAsync FinishTestSetEvent finishTestSetEvent){
        String testSetId = finishTestSetEvent.getTestSetRun().getTestSetId();
        if (SessionsStorageInMemory.hasSession(testSetId)) {
            sendTestRunCompleted(finishTestSetEvent);
        }
    }

    public void onFinishStep(@ObservesAsync FinishedStepEvent finishedStepEvent) {
        if (SessionsStorageInMemory.hasSession(finishedStepEvent.getStepRun().testSetId())) {
            sendTestStepCompleted(finishedStepEvent);
        }

    }

    private void sendTestRunCompleted(FinishTestSetEvent finishTestSetEvent) {
        String testSetId = finishTestSetEvent.getTestSetRun().getTestSetId();
        TestReportSummary testReportSummary = finishTestSetEvent.getReportSummary();
        String locationPrefix = datahouseUr + "/items/" + testSetId;
        String content = serDes.serializeAsString(new TestReportSummaryDTO(testReportSummary, locationPrefix));
        WSMessageDTO message = new WSMessageDTO(testSetId, "testRunCompleted", content);
        String serializedMessage = serDes.serializeAsString(message);
        LOG.info("Sending TestRunReport for testSetId : {}", testSetId);
        Session session = SessionsStorageInMemory.getSession(testSetId);
        if (session != null) {
            session.getAsyncRemote()
                    .sendText(serializedMessage, result -> logPotentialException(testSetId, result));
        }
    }

    private void sendTestStepCompleted(FinishedStepEvent finishedStepEvent) {
        String testSetId = finishedStepEvent.getStepRun().testSetId();
        StepRunReport stepRunReport = finishedStepEvent.getStepRunReport().build();
        String content = serDes.serializeAsString(new StepRunReportDTO(stepRunReport));
        WSMessageDTO message = new WSMessageDTO(testSetId, "testStepCompleted", content);
        String serializedMessage = serDes.serializeAsString(message);
        LOG.info("Sending TestStepRunReport for testSetId : {}", testSetId);
        Session session = SessionsStorageInMemory.getSession(testSetId);
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
