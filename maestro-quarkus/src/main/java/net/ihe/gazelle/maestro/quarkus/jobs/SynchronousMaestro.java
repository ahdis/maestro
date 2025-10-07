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

package net.ihe.gazelle.maestro.quarkus.jobs;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportSummary;
import net.ihe.gazelle.maestro.api.business.testrun.MaestroEventProducer;
import net.ihe.gazelle.maestro.api.business.testrun.TestSetRun;
import net.ihe.gazelle.maestro.api.business.testrun.validator.TestSetRunValidator;
import net.ihe.gazelle.maestro.api.technical.event.FinishTestSetEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Class used to do request to Maestro synchronously. When Maestro is called to execute a list of TestRun,
 * we create a Completable future, and when execution is done, completable future is completed with TestReport.
 * There is a Timeout on completable future, if time is up, completableFuture is completed with a TestReport with
 * undefined result and an unexpectedError TimeoutException.
 */
@ApplicationScoped
@Default
public class SynchronousMaestro {

    public static final String TIMEOUT_MESSAGE = "The test execution timed out. Either the test was too long for a synchronous call, either something fatal happened and you may need to contact an administrator";

    private final Map<String, CompletableFuture<TestReportSummary>> completableFutures = new ConcurrentHashMap<>();
    private final int defaultTimeout;
    private final MaestroEventProducer maestroEventProducer;
    private final TestSetRunValidator testSetRunValidator;


    @Inject
    public SynchronousMaestro(@ConfigProperty(name = "gzl.maestro.timeout", defaultValue = "60") int defaultTimeout, MaestroEventProducer maestroEventProducer) {
        this.defaultTimeout = defaultTimeout;
        this.maestroEventProducer = maestroEventProducer;
        this.testSetRunValidator = new TestSetRunValidator();
    }

    public TestReportSummary executeTestSet(TestSetRun testSetRun) throws ExecutionException, InterruptedException, TimeoutException {
        if (testSetRun == null || testSetRun.getTestRuns() == null || testSetRun.getTestRuns().isEmpty()) {
            throw new IllegalArgumentException("Test runs cannot be null or empty");
        }
        CompletableFuture<TestReportSummary> future = startTestSet(testSetRun);
        return future.get(defaultTimeout, TimeUnit.SECONDS);
    }

    private CompletableFuture<TestReportSummary> startTestSet(TestSetRun testSetRun) {
        testSetRunValidator.assertValid(testSetRun);
        CompletableFuture<TestReportSummary> future = new CompletableFuture<>();
        completableFutures.put(testSetRun.getTestSetId(), future);
        maestroEventProducer.executeTestSet(testSetRun);
        return future;
    }

    public void completeFuture(FinishTestSetEvent finishTestSetEvent) {
        completableFutures.get(finishTestSetEvent.getTestSetRun().getTestSetId()).complete(finishTestSetEvent.getReportSummary());
    }
}
