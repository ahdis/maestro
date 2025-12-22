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

package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.maestro.api.business.MaestroObserver;
import net.ihe.gazelle.maestro.api.business.message.*;
import net.ihe.gazelle.maestro.api.business.test.TestSuite;
import net.ihe.gazelle.maestro.api.business.testreport.*;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestRunValidator;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRunValidator;
import net.ihe.gazelle.maestro.engine.business.context.SessionStore;
import net.ihe.gazelle.security.business.acl.AccessControlListBuilder;
import net.ihe.gazelle.servicemetadata.api.business.MetadataService;
import net.ihe.gazelle.servicemetadata.api.business.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class MaestroFacade {

    private final TestSuiteRunValidator testSuiteRunValidator = new TestSuiteRunValidator();
    private final TestRunValidator testRunValidator = new TestRunValidator();
    private final SessionStore<MaestroObserver> testRunObserverStore;
    private final SessionStore<MaestroObserver> testSuiteRunObserverStore;
    private final MaestroEventProducer maestroEventProducer;
    private final Service maestroService;

    public MaestroFacade(SessionStore<MaestroObserver> testRunObserverStore,
                         SessionStore<MaestroObserver> testSuiteRunObserverStore,
                         MaestroEventProducer maestroEventProducer,
                         MetadataService metadataService) {
        this.testRunObserverStore = testRunObserverStore;
        this.testSuiteRunObserverStore = testSuiteRunObserverStore;
        this.maestroEventProducer = maestroEventProducer;
        this.maestroService = metadataService.getMetadata();
    }

    public void executeTestSuite(TestSuiteRun testSuiteRun, MaestroObserver testSuiteRunObserver) {
        testSuiteRunValidator.assertValid(testSuiteRun);
        String sessionId = UUID.randomUUID().toString();
        long timeout = testSuiteRun.computeTestSuiteTimeout();
        testSuiteRunObserverStore.addSession(sessionId, testSuiteRunObserver, timeout);
        maestroEventProducer.command(new StartTestSuiteRun(sessionId, testSuiteRun));
    }

    public void executeTest(TestRun testRun, MaestroObserver testRunObservers) {
        testRunValidator.assertValid(testRun);
        String sessionId = UUID.randomUUID().toString();
        long timeout = testRun.getTest().computeTestRunTimeout();
        testRunObserverStore.addSession(sessionId, testRunObservers, timeout);
        maestroEventProducer.command(new StartTestRun(sessionId, testRun));
    }

    public void listen(StartTestRun startTestRun) {
        Optional.ofNullable(testSuiteRunObserverStore.getSession(startTestRun.getSessionId()))
                .ifPresent(observer -> observer.onTestRunStarted(
                        new TestRunStarted(startTestRun.getTestRun().getTest().getId())
                ));
    }

    public void listen(StartStepRun startStepRun) {
        getObserver(startStepRun.getSessionId())
                .ifPresent(observer -> observer.onStepRunStarted(
                        new StepRunStarted(startStepRun.getTestId(), startStepRun.getStepIndex())
                ));
    }

    public void listen(StepRunFinished stepRunFinished) {
        getObserver(stepRunFinished.getSessionId())
                .ifPresent(observer -> observer.onStepRunFinished(stepRunFinished));
    }

    public void listen(TestRunFinished testRunFinished) {
        String sessionId = testRunFinished.getSessionId();
        Optional.ofNullable(testSuiteRunObserverStore.getSession(sessionId))
                .ifPresentOrElse(
                        obs -> obs.onTestRunFinished(testRunFinished),
                        () -> Optional.ofNullable(testRunObserverStore.getSession(sessionId))
                                .ifPresent(obs -> obs.onExecutionFinished(asExecutionFinished(testRunFinished)))
                );
    }

    public void listen(TestSuiteRunFinished testSuiteRunFinished) {
        Optional.ofNullable(testSuiteRunObserverStore.getSession(testSuiteRunFinished.getSessionId()))
                .ifPresent(observer -> {
                    observer.onExecutionFinished(asExecutionFinished(testSuiteRunFinished));
                    testSuiteRunObserverStore.removeSession(testSuiteRunFinished.getSessionId());
                });
    }

    private ExecutionFinished asExecutionFinished(TestRunFinished testRunFinished) {
        TestRun testRun = testRunFinished.getTestRun();
        TestReportBuilder testReportBuilder = new TestReportBuilder()
                .setTestService(getTestServiceBuilder())
                .setAccessControlList(new AccessControlListBuilder(testRun.getAccessControlList()))
                .setSystemsUnderTest(Optional.ofNullable(testRun.getSystemsUnderTest())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(SystemUnderTestBuilder::new)
                        .toList());
        testReportBuilder.setTestSuiteName(testRun.getTest().getName());
        testReportBuilder.addTestRunReport(testRunFinished.getTestRunReport());
        TestReport testReport = testReportBuilder.build();
        return new ExecutionFinished(testRunFinished.getSessionId(), testRunFinished.getTestRun(), testReport);
    }

    private ExecutionFinished asExecutionFinished(TestSuiteRunFinished testSuiteRunFinished) {
        TestReport testReport = buildGenericTestReport(testSuiteRunFinished.getTestSuiteRun())
                .addTestRunReports(testSuiteRunFinished.getTestRunReports())
                .build();
        return new ExecutionFinished(testSuiteRunFinished.getSessionId(), testSuiteRunFinished.getTestSuiteRun(), testReport);
    }

    private Optional<MaestroObserver> getObserver(String sessionId) {
        Optional<MaestroObserver> observer = Optional.ofNullable(testRunObserverStore.getSession(sessionId));
        if (observer.isEmpty()) {
            observer = Optional.ofNullable(testSuiteRunObserverStore.getSession(sessionId));
        }
        return observer;
    }

    private TestReportBuilder buildGenericTestReport(TestSuiteRun testSuiteRun) {
        TestReportBuilder testReportBuilder = new TestReportBuilder()
                .setTestService(getTestServiceBuilder())
                .setAccessControlList(new AccessControlListBuilder(testSuiteRun.getAccessControlList()))
                .setSystemsUnderTest(testSuiteRun.getSystemsUnderTest().stream().map(SystemUnderTestBuilder::new).toList());
        if (testSuiteRun.getTestSuite() != null) {
            testReportBuilder.setTestSuiteName(testSuiteRun.getTestSuite().getName());
        }
        return testReportBuilder;
    }

    private TestServiceBuilder getTestServiceBuilder() {
        return new TestServiceBuilder()
                .setDisclaimer(
                        "This Gazelle execution service is provided for technical compliance purposes only and does not " +
                                "constitute legal, medical, or regulatory advice.")
                .setServiceIdentification(
                        new EntityIdentificationBuilder()
                                .setName(maestroService.getName())
                                .setVersion(maestroService.getVersion())
                );
    }
}
