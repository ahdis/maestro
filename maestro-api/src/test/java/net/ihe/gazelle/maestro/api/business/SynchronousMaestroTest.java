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

package net.ihe.gazelle.maestro.api.business;

import net.ihe.gazelle.maestro.api.business.message.ExecutionFinished;
import net.ihe.gazelle.maestro.api.business.message.StartTestRun;
import net.ihe.gazelle.maestro.api.business.message.StartTestSuiteRun;
import net.ihe.gazelle.maestro.api.business.test.GradingMethod;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.test.TestReference;
import net.ihe.gazelle.maestro.api.business.test.TestSuite;
import net.ihe.gazelle.maestro.api.business.testreport.LocalizedTestReport;
import net.ihe.gazelle.maestro.api.business.testreport.Result;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;
import net.ihe.gazelle.security.business.acl.AccessControlList;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

class SynchronousMaestroTest {

    @Test
    void shouldReturnReportSummaryWhenExecutionCompletes() throws Exception {
        TestSuiteRun testSuiteRun = getTestSuiteRun();
        TestReport expectedReport = new TestReport()
                .setUuid("summary-1")
                .setDateTime(Instant.parse("2025-01-01T00:00:00Z"))
                .setTestSessionId("test-session-1")
                .setResult(Result.PASSED);
        LocalizedTestReport expectedLocalizedReport = new LocalizedTestReport(expectedReport, null);

        CapturingMaestro maestro = new CapturingMaestro(expectedReport);
        SynchronousMaestro synchronousMaestro = new SynchronousMaestro(maestro);

        LocalizedTestReport localizedTestReport = (LocalizedTestReport) synchronousMaestro.executeTestSuite(
                new StartTestSuiteRun()
                        .setTestSuiteRun(testSuiteRun)
                        .setPersist(false)
        );

        assertEquals(expectedLocalizedReport, localizedTestReport);
        assertSame(testSuiteRun, maestro.lastTestSuiteRun);
    }

    @Test
    void shouldTimeoutWhenExecutionDoesNotComplete() {
        BlockingMaestro maestro = new BlockingMaestro();
        SynchronousMaestro synchronousMaestro = new SynchronousMaestro(maestro);
        TestSuiteRun testSuiteRun = getTestSuiteRun();
        StartTestSuiteRun startTestSuiteRun = new StartTestSuiteRun()
                .setTestSuiteRun(testSuiteRun)
                .setPersist(false)
                .setExpirationTimestamp(Duration.ofMillis(1));

        assertThrows(TimeoutException.class, () -> synchronousMaestro.executeTestSuite(startTestSuiteRun));
    }

    private static final class CapturingMaestro implements Maestro {

        private final TestReport report;
        private TestSuiteRun lastTestSuiteRun;

        private CapturingMaestro(TestReport report) {
            this.report = report;
        }

        @Override
        public void executeTestSuite(StartTestSuiteRun startTestSuiteRun, MaestroObserver observer) {
            this.lastTestSuiteRun = startTestSuiteRun.getTestSuiteRun();
            observer.onExecutionFinished(new ExecutionFinished()
                    .setRun(lastTestSuiteRun)
                    .setReport(report));
        }

        @Override
        public void executeTest(StartTestRun startTestRun, MaestroObserver observer) {
            // Not needed for this test
        }
    }

    private TestSuiteRun getTestSuiteRun() {
        return new TestSuiteRun()
                .setTestSuite(
                        new TestSuite()
                                .setId("TS1")
                                .setName("Test Suite 1")
                                .setTestReferences(List.of(
                                        new TestReference().setTestId("T1"),
                                        new TestReference().setTestId("T2")
                                ))
                )
                .setTests(List.of(
                        new net.ihe.gazelle.maestro.api.business.test.Test()
                                .setId("T1")
                                .setName("Test 1")
                                .setGradingMethod(GradingMethod.AUTOMATED)
                                .addStep(
                                        new Step()
                                                .setName("A step")
                                                .setType("SOME_STEP_TYPE")
                                                .setTimeout(10L)
                                ),
                        new net.ihe.gazelle.maestro.api.business.test.Test()
                                .setId("T2")
                                .setName("Test 2")
                                .setGradingMethod(GradingMethod.AUTOMATED)
                                .addStep(
                                        new Step()
                                                .setName("A step")
                                                .setType("SOME_STEP_TYPE")
                                                .setTimeout(10L)
                                )
                ))
                .setAccessControlList(new AccessControlList().setPublic(true));
    }

    private static final class BlockingMaestro implements Maestro {

        @Override
        public void executeTestSuite(StartTestSuiteRun startTestSuiteRun, MaestroObserver observer) {
            // Intentionally left blank to trigger timeout
        }

        @Override
        public void executeTest(StartTestRun startTestRun, MaestroObserver observer) {
            // Not needed for this test
        }
    }
}
