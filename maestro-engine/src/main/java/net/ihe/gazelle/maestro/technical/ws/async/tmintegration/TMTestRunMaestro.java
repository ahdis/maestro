package net.ihe.gazelle.maestro.technical.ws.async.tmintegration;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSetRun;
import net.ihe.gazelle.maestro.technical.client.TestReportClient;
import net.ihe.gazelle.maestro.technical.jobs.testrunevents.FinishTestSetEvent;
import net.ihe.gazelle.maestro.technical.jobs.testrunevents.StartTestSetCommand;
import net.ihe.gazelle.maestro.technical.jobs.testrunevents.TestRunEvent;
import net.ihe.gazelle.maestro.technical.ws.async.TestRunStore;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is temporary, used to execute Maestro engine asynchronously when called by TM
 * <br> It emits a test-run channel start event and listen for test-run finish event to send report to TM
 */
@ApplicationScoped
public class TMTestRunMaestro {

    private final Map<String, TestRunStore> tmRunningServices;
    private final Emitter<TestRunEvent> emitter;
    private final TestReportClient client;

    @Inject
    public TMTestRunMaestro(@Channel("test-run") Emitter<TestRunEvent> emitter, TestReportClient client) {
        this.tmRunningServices = new ConcurrentHashMap<>();
        this.emitter = emitter;
        this.client = client;
    }

    public void startTestSet(JsonNode testRunNodes) {
        TestRunStore store = new TestRunStore();
        List<TestRun> testRuns = TMScriptMapper.convertToTestRun(testRunNodes, store);
        TestSetRun testSetRun = new TestSetRun(UUID.randomUUID().toString(), testRuns);
        tmRunningServices.put(testSetRun.testSetId(), store);
        emitter.send(new StartTestSetCommand(testSetRun));
    }

    public void callbackTM(FinishTestSetEvent finishTestSetEvent) {
        TestRunStore store = tmRunningServices.get(finishTestSetEvent.getTestSetRun().testSetId());
        client.sendTestReport(
                store.getCallbackAuthorization(),
                store.getClientCallback(),
                finishTestSetEvent.getTestReport()
        );
    }

    public boolean isServiceRunning(String testSetId) {
        return tmRunningServices.containsKey(testSetId);
    }
}
