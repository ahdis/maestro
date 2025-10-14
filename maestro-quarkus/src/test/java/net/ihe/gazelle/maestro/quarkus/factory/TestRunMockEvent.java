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

package net.ihe.gazelle.maestro.quarkus.factory;

import jakarta.enterprise.event.NotificationOptions;
import jakarta.enterprise.util.TypeLiteral;
import net.ihe.gazelle.maestro.api.business.message.Message;
import net.ihe.gazelle.maestro.api.business.message.StartStepRun;
import net.ihe.gazelle.maestro.api.business.message.StartTestRun;
import net.ihe.gazelle.maestro.api.business.message.StartTestSuiteRun;
import net.ihe.gazelle.maestro.api.business.message.StepRunFinished;
import net.ihe.gazelle.maestro.api.business.message.TestRunFinished;
import net.ihe.gazelle.maestro.api.business.message.TestSuiteRunFinished;
import net.ihe.gazelle.maestro.quarkus.jobs.EventListener;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class TestRunMockEvent implements jakarta.enterprise.event.Event<Message> {

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
    public void fire(Message message) {
        if (message instanceof StartTestSuiteRun startTestSuiteRun) {
            listeners.forEach(listener ->
                    executorService.submit(() -> listener.onStartTestSet(startTestSuiteRun))
            );
        }
        if (message instanceof TestSuiteRunFinished testSuiteRunFinished) {
            listeners.forEach(listener ->
                    executorService.submit(() -> listener.onFinishTestSet(testSuiteRunFinished))
            );
        }
        if (message instanceof StartTestRun startTestRun) {
            listeners.forEach(listener ->
                    executorService.submit(() -> listener.onStartTestRun(startTestRun))
            );
        }
        if (message instanceof TestRunFinished testRunFinished) {
            listeners.forEach(listener ->
                    executorService.submit(() -> listener.onFinishTestRun(testRunFinished))
            );
        }
        if(message instanceof StartStepRun startStepRun) {
            listeners.forEach(listener ->
                    executorService.submit(() -> listener.onStartStep(startStepRun))
            );
        }
        if(message instanceof StepRunFinished stepRunFinished) {
            listeners.forEach(listener ->
                    executorService.submit(() -> listener.onFinishStep(stepRunFinished))
            );
        }
    }

    @Override
    public <U extends Message> CompletionStage<U> fireAsync(U testRunEvent) {
        fire(testRunEvent);
        return null;
    }

    @Override
    public <U extends Message> CompletionStage<U> fireAsync(U u, NotificationOptions notificationOptions) {
        return null;
    }

    @Override
    public jakarta.enterprise.event.Event<Message> select(Annotation... annotations) {
        return null;
    }

    @Override
    public <U extends Message> jakarta.enterprise.event.Event<U> select(Class<U> aClass, Annotation... annotations) {
        return null;
    }

    @Override
    public <U extends Message> jakarta.enterprise.event.Event<U> select(TypeLiteral<U> typeLiteral, Annotation... annotations) {
        return null;
    }
}
