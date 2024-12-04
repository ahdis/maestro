package net.ihe.gazelle.maestro.technical.jobs;

import net.ihe.gazelle.maestro.technical.jobs.testrunevents.TestRunEvent;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class TestRunMockEmitter implements Emitter<TestRunEvent> {

    ExecutorService executorService = new ScheduledThreadPoolExecutor(5);
    List<TestRunEventListener> listeners = new ArrayList<>();

    public TestRunMockEmitter() {
        // mocked method
    }

    public TestRunMockEmitter addListener(TestRunEventListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
        return this;
    }

    @Override
    public <M extends Message<? extends TestRunEvent>> void send(M msg) {
        // mocked method
    }

    @Override
    public CompletionStage<Void> send(TestRunEvent testRunEvent) {
        listeners.forEach(listener ->
                executorService.submit(() -> listener.receiveTestRun(testRunEvent))
        );
        return null;
    }

    @Override
    public void complete() {
        // mocked method
    }

    @Override
    public void error(Exception e) {
        // mocked method
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean hasRequests() {
        return false;
    }
}
