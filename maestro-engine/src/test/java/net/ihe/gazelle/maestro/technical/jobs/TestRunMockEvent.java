/*
 * Copyright 2025 Kereval.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
