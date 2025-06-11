package net.ihe.gazelle.maestro.technical.ws.async;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSetRun;
import net.ihe.gazelle.maestro.technical.client.TestReportClient;
import net.ihe.gazelle.maestro.technical.jobs.testrunevents.FinishTestSetEvent;
import net.ihe.gazelle.maestro.technical.jobs.testrunevents.StartTestSetCommand;
import net.ihe.gazelle.maestro.technical.jobs.testrunevents.TestRunEvent;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class AsynchronousMaestro {

    private final Map<String, TestRunStore> runningServices;
    private final Emitter<TestRunEvent> emitter;
    private final TestReportClient client;

    @Inject
    public AsynchronousMaestro(@Channel("test-run") Emitter<TestRunEvent> emitter, TestReportClient client) {
        this.runningServices = new ConcurrentHashMap<>();
        this.emitter = emitter;
        this.client = client;
    }

    public void executeTestSet(String callback, List<TestRun> testRuns) {
        TestRunStore store = new TestRunStore();
        store.setClientCallback(callback);
        TestSetRun testSetRun = new TestSetRun(UUID.randomUUID().toString(), testRuns);
        runningServices.put(testSetRun.testSetId(), store);
        emitter.send(new StartTestSetCommand(testSetRun));
    }

    public void callback(FinishTestSetEvent finishTestSetEvent) {
        TestRunStore store = runningServices.get(finishTestSetEvent.getTestSetRun().testSetId());
        client.sendTestReport(
                store.getCallbackAuthorization(),
                store.getClientCallback(),
                finishTestSetEvent.getTestReport()
        );
    }

    public boolean isServiceRunning(String testSetId) {
        return runningServices.containsKey(testSetId);
    }
}
