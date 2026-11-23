/*
 * Copyright 2026 IHE International.
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

package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.maestro.api.business.message.StartStepRun;
import net.ihe.gazelle.maestro.api.business.message.StartTestRun;
import net.ihe.gazelle.maestro.api.business.message.StepRunFinished;
import net.ihe.gazelle.maestro.api.business.message.TestRunFinished;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.test.GradingMethod;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.test.Test;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.engine.business.context.TestRunSession;
import net.ihe.gazelle.maestro.engine.technical.dao.SessionStoreCache;
import net.ihe.gazelle.maestro.spi.business.StepRun;
import net.ihe.gazelle.security.business.acl.AccessControlList;
import org.junit.jupiter.api.BeforeEach;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class TestRunnerTest {

    private SessionStoreCache<TestRunSession> sessionStore;
    private RecordingTestRunEventProducer eventProducer;
    private TestRunner testRunner;
    private Step firstStep;
    private TestRun testRun;

    @BeforeEach
    void setUp() {
        sessionStore = new SessionStoreCache<>();
        eventProducer = new RecordingTestRunEventProducer();
        testRunner = new TestRunner(sessionStore, eventProducer);

        firstStep = new Step().setName("First").setType("SIM").addOutputMapping("payload", "firstPayload");
        Step secondStep = new Step().setName("Second").setType("SIM");

        Test test = new Test()
                .setId("test-1")
                .setName("Test 1")
                .setGradingMethod(GradingMethod.AUTOMATED)
                .setSteps(List.of(firstStep, secondStep));

        testRun = new TestRun()
                .setTest(test)
                .setInputs(List.of(new StringProperty("input", "value")))
                .setAccessControlList(new AccessControlList().setPublic(true));
    }

    @org.junit.jupiter.api.Test
    void runStoresSessionAndStartsFirstStep() {
        testRunner.run(new StartTestRun("session-1", testRun));

        assertThat(sessionStore.hasSession("session-1"), is(true));
        assertThat(eventProducer.getCommands(), hasSize(1));

        StartStepRun command = eventProducer.getCommands().getFirst();
        assertThat(command.getStepRun().hasProperty("input"), is(true));
        assertThat(command.getTestId(), equalTo(testRun.getTest().getId()));
    }

    @org.junit.jupiter.api.Test
    void listenAddsOutputsAndTriggersNextStep() {
        testRunner.run(new StartTestRun("session-ctx", testRun));
        StartStepRun firstCommand = eventProducer.getCommands().getFirst();

        StepRunReport report = new StepRunReport()
                .setStepName("First")
                .setType("SIM")
                .setResult(StepResult.PASSED)
                .setOutputs(List.of(new StringProperty("payload", "abc")));

        testRunner.listen(
                new StepRunFinished("session-ctx", testRun.getTest().getId(), "First", firstCommand.getStepRun(), report)
        );

        assertThat(sessionStore.getSession("session-ctx").getContext(), hasItem(
                allOf(hasProperty("name", equalTo("firstPayload")), hasProperty("value", equalTo("abc")))
        ));
        assertThat(eventProducer.getCommands(), hasSize(2));
        StartStepRun secondCommand = eventProducer.getCommands().get(1);
        assertThat(secondCommand.getStepRun().hasProperty("firstPayload"), is(true));
    }

    @org.junit.jupiter.api.Test
    void listenOnLastStepBuildsTestRunReportAndClearsSession() {
        testRunner.run(new StartTestRun("session-last", testRun));
        StartStepRun firstCommand = eventProducer.getCommands().getFirst();

        StepRunReport firstReport = new StepRunReport()
                .setStepName("First")
                .setType("SIM")
                .setResult(StepResult.PASSED)
                .setOutputs(List.of(new StringProperty("payload", "abc")));

        testRunner.listen(
                new StepRunFinished("session-last", testRun.getTest().getId(), "First", firstCommand.getStepRun(), firstReport)
        );

        StartStepRun secondCommand = eventProducer.getCommands().get(1);
        StepRunReport secondReport = new StepRunReport()
                .setStepName("Second")
                .setType("SIM")
                .setResult(StepResult.PASSED);

        testRunner.listen(
                new StepRunFinished("session-last", testRun.getTest().getId(), "Second", secondCommand.getStepRun(), secondReport)
        );

        assertThat(sessionStore.hasSession("session-last"), is(false));
        assertThat(eventProducer.getNotifications(), hasSize(1));
        TestRunFinished notification = eventProducer.getNotifications().getFirst();
        assertThat(notification.getTestRunReport().getStepRunReports(), hasSize(2));
    }

    @org.junit.jupiter.api.Test
    void invalidTestRunShouldAddUnexpectedError() {
        TestRun invalid = new TestRun().setTest(new Test().setId("invalid"));
        testRunner.run(new StartTestRun("session-last", invalid));

        assertThat(eventProducer.getNotifications(), hasSize(1));
        TestRunFinished notification = eventProducer.getNotifications().getFirst();
        assertThat(notification.getTestRunReport().getUnexpectedErrors(), is(not(empty())));
    }

    @org.junit.jupiter.api.Test
    void listenWithoutSessionDoesNothing() {
        StepRunReport report = new StepRunReport().setStepName("Any").setType("SIM");
        testRunner.listen(new StepRunFinished("missing", "test", "Any",
                new StepRun(firstStep, new ArrayList<>()), report));
        assertThat(eventProducer.getCommands(), empty());
        assertThat(eventProducer.getNotifications(), empty());
    }

    @ParameterizedTest
    @CsvSource({"FAILED,true", "FAILED,false", "UNDEFINED,true", "UNDEFINED,false"})
    void waitsForDecisionAndPreservesUnsuccessfulStep(StepResult result, boolean proceed) throws Exception {
        CompletableFuture<Boolean> answer = new CompletableFuture<>();
        testRun.getTest().getSteps().getLast().setType("GRADING");
        testRunner = new TestRunner(sessionStore, eventProducer, request -> {
            assertThat(request.result(), equalTo(result));
            assertThat(request.stepName(), equalTo("First"));
            return answer;
        });
        testRunner.run(new StartTestRun("decision", testRun).setPromptOnStepFailure(true));
        StepRunReport firstReport = new StepRunReport().setStepName("First").setType("SIM").setResult(result);
        testRunner.listen(new StepRunFinished("decision", "test-1", "First",
                eventProducer.getCommands().getFirst().getStepRun(), firstReport));
        assertThat(eventProducer.getCommands(), hasSize(1));
        assertThat(eventProducer.getNotifications(), empty());
        answer.complete(proceed);
        if (proceed) {
            eventProducer.nextStepReady.get(4, TimeUnit.SECONDS);
            assertThat(eventProducer.getCommands(), hasSize(2));
            StepRunReport secondReport = new StepRunReport().setStepName("Second").setType("GRADING").setResult(StepResult.PASSED);
            testRunner.listen(new StepRunFinished("decision", "test-1", "Second",
                    eventProducer.getCommands().getLast().getStepRun(), secondReport));
        } else {
            assertThat(eventProducer.getCommands(), hasSize(1));
        }
        eventProducer.notificationReady.get(4, TimeUnit.SECONDS);
        assertThat(eventProducer.getNotifications(), hasSize(1));
        var reports = eventProducer.getNotifications().getFirst().getTestRunReport().getStepRunReports();
        assertThat(reports, hasSize(proceed ? 2 : 1));
        assertThat(reports.getFirst().getResult(), equalTo(result));
        assertThat(eventProducer.getNotifications().getFirst().getTestRunReport().getResult().name(),
                equalTo(result.name()));
    }

    @org.junit.jupiter.api.Test
    void aDecisionForARemovedRunCannotRestartIt() throws Exception {
        CompletableFuture<Void> observedMissingSession = new CompletableFuture<>();
        sessionStore = new SessionStoreCache<>() {
            @Override
            public synchronized TestRunSession getSession(String id) {
                TestRunSession found = super.getSession(id);
                if (found == null) observedMissingSession.complete(null);
                return found;
            }
        };
        CompletableFuture<Boolean> answer = new CompletableFuture<>();
        testRunner = new TestRunner(sessionStore, eventProducer, request -> answer);
        testRunner.run(new StartTestRun("removed", testRun).setPromptOnStepFailure(true));
        testRunner.listen(new StepRunFinished("removed", "test-1", "First",
                eventProducer.getCommands().getFirst().getStepRun(),
                new StepRunReport().setStepName("First").setType("SIM").setResult(StepResult.FAILED)));
        sessionStore.removeSession("removed");
        answer.complete(true);
        observedMissingSession.get(4, TimeUnit.SECONDS);
        assertThat(eventProducer.getCommands(), hasSize(1));
        assertThat(eventProducer.getNotifications(), empty());
    }

    @org.junit.jupiter.api.Test
    void expiryFinishesTheRunAndALateChoiceCannotStartAnotherStep() throws Exception {
        CompletableFuture<Boolean> answer = new CompletableFuture<>();
        testRunner = new TestRunner(sessionStore, eventProducer, request -> answer);
        testRunner.run(new StartTestRun("expiry", testRun).setPromptOnStepFailure(true)
                .setExpirationTimestamp(Instant.now().plusMillis(500)));
        testRunner.listen(new StepRunFinished("expiry", "test-1", "First",
                eventProducer.getCommands().getFirst().getStepRun(),
                new StepRunReport().setStepName("First").setType("SIM").setResult(StepResult.UNDEFINED)));
        org.junit.jupiter.api.Assertions.assertTimeoutPreemptively(java.time.Duration.ofSeconds(4), () ->
                eventProducer.notificationReady.get());
        assertThat(answer.complete(true), is(false));
        assertThat(eventProducer.getNotifications(), hasSize(1));
        assertThat(eventProducer.getCommands(), hasSize(1));
        assertThat(sessionStore.hasSession("expiry"), is(false));
    }

    private static final class RecordingTestRunEventProducer implements TestRunEventProducer {

        private final CompletableFuture<StartStepRun> nextStepReady = new CompletableFuture<>();
        private final CompletableFuture<TestRunFinished> notificationReady = new CompletableFuture<>();
        private final List<StartStepRun> commands = new ArrayList<>();
        private final List<TestRunFinished> notifications = new ArrayList<>();

        @Override
        public void command(StartStepRun startStepRun) {
            commands.add(startStepRun);
            if (commands.size() == 2) nextStepReady.complete(startStepRun);
        }

        @Override
        public void notify(TestRunFinished testRunFinished) {
            notifications.add(testRunFinished);
            notificationReady.complete(testRunFinished);
        }

        List<StartStepRun> getCommands() {
            return commands;
        }

        List<TestRunFinished> getNotifications() {
            return notifications;
        }
    }
}
