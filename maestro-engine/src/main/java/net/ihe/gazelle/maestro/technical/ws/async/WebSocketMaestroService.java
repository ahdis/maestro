package net.ihe.gazelle.maestro.technical.ws.async;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSetRun;
import net.ihe.gazelle.maestro.technical.jobs.testrunevents.StartTestSetCommand;
import net.ihe.gazelle.maestro.technical.jobs.testrunevents.TestRunEvent;

import java.util.List;

@ApplicationScoped
public class WebSocketMaestroService {

    private final Event<TestRunEvent> event;

    @Inject
    public WebSocketMaestroService(Event<TestRunEvent> event) {
        this.event = event;
    }

    public void executeTestSet(String testSetId, List<TestRun> testRuns) {
        TestSetRun testSetRun = new TestSetRun(testSetId, testRuns);
        event.fireAsync(new StartTestSetCommand(testSetRun));
    }
}
