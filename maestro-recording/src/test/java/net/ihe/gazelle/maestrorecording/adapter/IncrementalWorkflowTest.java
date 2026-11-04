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

package net.ihe.gazelle.maestrorecording.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kereval.gazelle.datahouse.api.business.record.Item;
import net.ihe.gazelle.maestro.api.business.MaestroObserver;
import net.ihe.gazelle.maestro.api.business.message.*;
import net.ihe.gazelle.maestro.api.business.property.*;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.testreport.*;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.engine.business.*;
import net.ihe.gazelle.maestro.engine.business.context.*;
import net.ihe.gazelle.maestro.userinteraction.step.business.UserInteractionStepExecutor;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStepDefinition;
import net.ihe.gazelle.maestro.validation.step.technical.record.ValidationStepOutputDissector;
import net.ihe.gazelle.security.business.acl.AccessControlList;
import net.ihe.gazelle.servicemetadata.api.business.Service;
import net.ihe.gazelle.maestro.engine.technical.dao.SessionStoreCache;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

class IncrementalWorkflowTest {
    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void recordsWhileAnotherStepWaitsWithoutChangingMappedBytes(boolean failStorage) throws Exception {
        ExecutorService workers = Executors.newFixedThreadPool(10);
        var client = new WorkflowRecordItemClient();
        client.fail = failStorage;
        var observer = new WorkflowObserver();
        try {
            byte[] bytes = new ObjectMapper().writeValueAsBytes(
                    new net.ihe.gazelle.validation.v2.api.business.report.ValidationReport().setUuid("workflow-validation"));
            var validation = new StepRunReport().setStepName("Validate").setType("VALIDATION")
                    .setResult(StepResult.PASSED).addOutput(new ByteArrayProperty(ValidationStepDefinition.REPORT, bytes));
            var observerStore = new SessionStoreCache<MaestroObserver>();
            StepExecutorProvider provider = (session, step) -> {
                if (step.getName().equals("Validate")) return run -> validation;
                return run -> {
                    assertArrayEquals(bytes, run.getPropertyValue("previousReport"));
                    return new UserInteractionStepExecutor(new net.ihe.gazelle.maestro.engine.technical.userinteract.UserInteractionHandlerImpl(
                            observerStore.getSession(session))).execute(run);
                };
            };
            var broker = new WorkflowEventBroker(workers);
            var facade = new MaestroFacade(observerStore,
                    new SessionStoreCache<MaestroObserver>(), broker,
                    () -> new Service().setName("Workflow test").setVersion("1"));
            broker.setServices(facade,
                    new TestSuiteRunner(new SessionStoreCache<TestSuiteSession>(), broker),
                    new TestRunner(new SessionStoreCache<TestRunSession>(), broker), new StepRunner(provider, broker));
            var recorder = new DatahouseTestReportRecordingService(client, "http://test-datahouse",
                    step -> "VALIDATION".equals(step.getType()) ? new ValidationStepOutputDissector() : null);
            var maestro = new RecordingMaestro(facade, recorder);
            var definition = new net.ihe.gazelle.maestro.api.business.test.Test().setId("workflow").setName("Workflow")
                    .setSteps(List.of(
                            new Step().setName("Validate").setType("VALIDATION").setTimeout(10000L)
                                    .setOutputMappings(Map.of(ValidationStepDefinition.REPORT, "reportBytes")),
                            new Step().setName("Confirm").setType("USER_INTERACTION").setTimeout(10000L)
                                    .setProperties(List.of(new StringProperty("interactionTitle", "Review"),
                                            new StringProperty("message", "Inspect report"),
                                            new ByteArrayProperty("previousReport", "${reportBytes}")))));
            maestro.executeTest(new TestRun().setTest(definition)
                    .setAccessControlList(new AccessControlList().setPublic(true)), true, observer);
            assertTrue(client.writing.await(10, TimeUnit.SECONDS));
            observer.interaction.get(10, TimeUnit.SECONDS);
            assertFalse(observer.finished.isDone(), "Execution must still be waiting for user input");
            assertFalse(observer.recorded.isDone(), "No available link before persistence succeeds");
            client.release.countDown();
            if (!failStorage) {
                var available = observer.recorded.get(10, TimeUnit.SECONDS);
                ByteArrayItemProperty output = available.getStepRunReport().getOutput(ValidationStepDefinition.REPORT);
                assertEquals("http://test-datahouse/items/itm-1", output.getReference());
                assertNull(output.getValue());
                assertEquals(1, client.recordedItems.size(), "Only the validation report exists while waiting");
                assertFalse(observer.finished.isDone());
                assertArrayEquals(bytes, validation.getOutput(ValidationStepDefinition.REPORT).getValue());
            }
            observer.answer.complete(new UserInteractionCompleted());
            var finished = observer.finished.get(10, TimeUnit.SECONDS);
            assertEquals(1, client.validationWrites, "Finalization must not repeat an uncertain write");
            assertEquals(StepResult.PASSED, finished.getReport().getTestRunReports().getFirst()
                    .getStepRunReports().getFirst().getResult());
            if (failStorage) {
                assertFalse(observer.recorded.isDone());
                assertEquals(1, finished.getReport().getUnexpectedErrors().size());
                assertNull(finished.getReportLocation());
            } else {
                assertTrue(finished.getReport().getUnexpectedErrors().isEmpty());
                ByteArrayItemProperty finalOutput = finished.getReport().getTestRunReports().getFirst()
                        .getStepRunReports().getFirst().getOutput(ValidationStepDefinition.REPORT);
                assertEquals("http://test-datahouse/items/itm-1", finalOutput.getReference());
                assertNull(finalOutput.getValue());
                assertArrayEquals(bytes, validation.getOutput(ValidationStepDefinition.REPORT).getValue());
                assertEquals("http://test-datahouse/items/itm-2", finished.getReportLocation());
                assertEquals(2, client.recordedItems.size());
                Item root = (Item) client.recordedItems.getLast();
                assertTrue(root.getReferences().stream().anyMatch(ref -> "itm-1".equals(ref.getValue())));
            }
            System.out.println("WORKFLOW VERIFIED: storageFailure=" + failStorage
                    + ", validationWrites=" + client.validationWrites + ", items=" + client.recordedItems.size());
        } finally {
            client.release.countDown();
            observer.answer.complete(new UserInteractionCompleted());
            workers.shutdownNow();
            assertTrue(workers.awaitTermination(10, TimeUnit.SECONDS));
        }
    }
}
