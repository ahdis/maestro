package net.ihe.gazelle.maestro.technical.ws.async;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSetRun;
import net.ihe.gazelle.maestro.technical.client.TestReportClient;
import net.ihe.gazelle.maestro.technical.jobs.testrunevents.FinishTestSetEvent;
import net.ihe.gazelle.maestro.technical.jobs.testrunevents.StartTestSetCommand;
import net.ihe.gazelle.maestro.technical.jobs.testrunevents.TestRunEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class AsynchronousMaestro {

    private final Map<String, TestRunStore> runningServices;
    private final Event<TestRunEvent> event;
    private final TestReportClient client;

    @Inject
    public AsynchronousMaestro(Event<TestRunEvent> event, TestReportClient client) {
        this.runningServices = new ConcurrentHashMap<>();
        this.event = event;
        this.client = client;
    }

    public void executeTestSet(String callback, List<TestRun> testRuns) {
        TestRunStore store = new TestRunStore();
        store.setClientCallback(callback);
        TestSetRun testSetRun = new TestSetRun(UUID.randomUUID().toString(), testRuns);
        runningServices.put(testSetRun.testSetId(), store);
        event.fireAsync(new StartTestSetCommand(testSetRun));
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
