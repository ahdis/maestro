package net.ihe.gazelle.maestro.technical.jobs;

import net.ihe.gazelle.maestro.api.business.testrun.runner.StepMessage;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class StepMockEmitter implements Emitter<StepMessage> {

    ExecutorService executorService = new ScheduledThreadPoolExecutor(5);
    List<StepRunEventListener> listeners = new ArrayList<>();

    public StepMockEmitter addListener(StepRunEventListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
        return this;
    }

    @Override
    public CompletionStage<Void> send(StepMessage msg) {
        listeners.forEach(listener ->
                executorService.submit(() -> listener.receiveStepEvent(msg))
        );
        return null;
    }

    @Override
    public <M extends Message<? extends StepMessage>> void send(M msg) {
        // mocked method
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
