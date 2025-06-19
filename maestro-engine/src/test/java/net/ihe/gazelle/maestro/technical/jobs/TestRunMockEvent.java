package net.ihe.gazelle.maestro.technical.jobs;

import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.NotificationOptions;
import jakarta.enterprise.util.TypeLiteral;
import net.ihe.gazelle.maestro.technical.jobs.testrunevents.*;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class TestRunMockEvent implements Event<TestRunEvent> {

    ExecutorService executorService = new ScheduledThreadPoolExecutor(5);
    List<EventListener> listeners = new ArrayList<>();

    public TestRunMockEvent() {
        // mocked method
    }

    public TestRunMockEvent addListener(EventListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
        return this;
    }

    @Override
    public void fire(TestRunEvent testRunEvent) {
        if (testRunEvent instanceof StartTestSetCommand startTestSetCommand) {
            listeners.forEach(listener ->
                    executorService.submit(() -> listener.onStartTestSet(startTestSetCommand))
            );
        }
        if (testRunEvent instanceof FinishTestSetEvent finishTestSetEvent) {
            listeners.forEach(listener ->
                    executorService.submit(() -> listener.onFinishTestSet(finishTestSetEvent))
            );
        }
        if (testRunEvent instanceof StartTestRunCommand startTestRunCommand) {
            listeners.forEach(listener ->
                    executorService.submit(() -> listener.onStartTestRun(startTestRunCommand))
            );
        }
        if (testRunEvent instanceof FinishTestRunEvent finishTestRunEvent) {
            listeners.forEach(listener ->
                    executorService.submit(() -> listener.onFinishTestRun(finishTestRunEvent))
            );
        }
    }

    @Override
    public <U extends TestRunEvent> CompletionStage<U> fireAsync(U testRunEvent) {
        fire(testRunEvent);
        return null;
    }

    @Override
    public <U extends TestRunEvent> CompletionStage<U> fireAsync(U u, NotificationOptions notificationOptions) {
        return null;
    }

    @Override
    public Event<TestRunEvent> select(Annotation... annotations) {
        return null;
    }

    @Override
    public <U extends TestRunEvent> Event<U> select(Class<U> aClass, Annotation... annotations) {
        return null;
    }

    @Override
    public <U extends TestRunEvent> Event<U> select(TypeLiteral<U> typeLiteral, Annotation... annotations) {
        return null;
    }
}
