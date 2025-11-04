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

import net.ihe.gazelle.errorhandling.business.UnexpectedErrorBuilder;
import net.ihe.gazelle.maestro.api.business.InvalidTestRunException;
import net.ihe.gazelle.maestro.api.business.message.StartStepRun;
import net.ihe.gazelle.maestro.api.business.message.StartTestRun;
import net.ihe.gazelle.maestro.api.business.message.StepRunFinished;
import net.ihe.gazelle.maestro.api.business.message.TestRunFinished;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.test.Test;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestRunValidator;
import net.ihe.gazelle.maestro.engine.business.context.SessionStore;
import net.ihe.gazelle.maestro.engine.business.context.TestRunSession;
import net.ihe.gazelle.maestro.spi.business.runner.StepRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TestRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TestRunner.class);

    private final SessionStore<TestRunSession> sessionStore;
    private final TestRunEventProducer testRunEventProducer;
    private final TestRunValidator testRunValidator = new TestRunValidator();

    public TestRunner(SessionStore<TestRunSession> sessionStore, TestRunEventProducer testRunEventProducer) {
        this.sessionStore = sessionStore;
        this.testRunEventProducer = testRunEventProducer;
    }


    public void run(StartTestRun startTestRun) {
        String sessionId = startTestRun.getSessionId();
        TestRun testRun = startTestRun.getTestRun();
        try {
            testRunValidator.validate(testRun).orThrow(InvalidTestRunException::new);

            TestRunSession testRunSession = new TestRunSession(testRun);
            sessionStore.addSession(sessionId, testRunSession);
            testRunEventProducer.command(
                    new StartStepRun(sessionId, testRun.getTest().getId(),
                            new StepRun(testRunSession.nextStep(), testRunSession.getContext()))
            );
        } catch (Exception e) {
            Test test = testRun.getTest();
            LOG.atError().setCause(e).log("Error running test {}", test.getName());
            sessionStore.removeSession(sessionId);
            testRunEventProducer.notify(
                    new TestRunFinished(sessionId, testRun,
                            newTestRunReportBuilder(test)
                                    .addUnexpectedError(new UnexpectedErrorBuilder().fromThrowable(e))
                                    .build()
                    )
            );
        }
    }

    public void listen(StepRunFinished stepRunFinished) {
        String sessionId = stepRunFinished.getSessionId();
        TestRunSession testRunSession = sessionStore.getSession(sessionId);
        if (testRunSession != null) {

            StepRunReport stepRunReport = stepRunFinished.getStepRunReport();
            testRunSession.addStepRunReport(stepRunReport);
            testRunSession.getContext().addAll(
                    contextualizeProperties(stepRunReport.getStepId(), stepRunReport.getOutputs())
            );

            if (testRunSession.hasNextStep()) {
                testRunEventProducer.command(
                        new StartStepRun(sessionId, stepRunFinished.getTestId(),
                                new StepRun(testRunSession.nextStep(), testRunSession.getContext()))
                );
            } else {
                sessionStore.removeSession(sessionId);
                testRunEventProducer.notify(
                        new TestRunFinished(sessionId, testRunSession.getTestRun(),
                                newTestRunReportBuilder(
                                        testRunSession.getTestRun().getTest())
                                        .addStepRunReports(testRunSession.getStepReports())
                                        .setInputs(testRunSession.getTestRun().getInputs())
                                        .build()
                        )
                );
            }
        }
    }

    /**
     * Contextualize properties by prefixing their name with the stepId and a dot before introducing them into the test
     * context.
     *
     * @param stepId  the step identifier
     * @param outputs the list of output properties to contextualize
     * @return the list of contextualized properties
     */
    private List<Property> contextualizeProperties(String stepId, List<Property> outputs) {
        return outputs.stream()
                .map(Property::copy)
                .map(p -> p.setName(stepId + "." + p.getName()))
                .toList();
    }


    private static TestRunReportBuilder newTestRunReportBuilder(Test test) {
        return new TestRunReportBuilder()
                .setTest(new TestBuilder()
                        .setId(test.getId())
                        .setName(test.getName()));
    }

}
