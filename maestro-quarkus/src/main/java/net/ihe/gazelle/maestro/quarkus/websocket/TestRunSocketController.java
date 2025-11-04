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

package net.ihe.gazelle.maestro.quarkus.websocket;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import net.ihe.gazelle.maestro.api.business.Maestro;
import net.ihe.gazelle.maestro.api.business.MaestroObserver;
import net.ihe.gazelle.maestro.api.business.message.*;
import net.ihe.gazelle.maestro.api.business.testreport.Result;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportSummary;
import net.ihe.gazelle.maestro.quarkus.websocket.dto.*;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

@ApplicationScoped
@ServerEndpoint("/v1/runs")
public class TestRunSocketController {

    private static final Logger LOG = LoggerFactory.getLogger(TestRunSocketController.class.getName());

    private final TextSerDes serDes = new JacksonSerDes();
    private final Maestro maestro;
    private StartTestSuiteRunDTO localStartTestSuite;

    @Inject
    public TestRunSocketController(Maestro maestro) {
        this.maestro = maestro;
    }

    @OnMessage
    public void onMessage(Session session, String messageString) throws InterruptedException {
        Object message = serDes.deserialize(messageString, MessageDTO.class).getBusinessObject();
        WebsocketSession maestroCaller = new WebsocketSession(session);
        Thread.sleep(1000);
        if (message instanceof StartTestSuiteRunDTO startTestSuiteRun) {
            this.localStartTestSuite = startTestSuiteRun;
            if ("With additional instructions".equals(startTestSuiteRun.getTestSuiteRun().getTestSuite().getName())) {
                maestroCaller.interactWithUser(new InteractWithUser("User interaction test", "Please click continue"));
            } else {
                maestroCaller.onTestSuiteFinished(createTestSuiteFinished());
            }
            //maestro.executeTestSuite(localStartTestSuite.getTestSuiteRun(), maestroCaller);
        } else if (message instanceof UserInteractionCompletedDTO) {
            maestroCaller.onTestSuiteFinished(createTestSuiteFinished());
        } else {
            throw new IllegalArgumentException("Unexpected message type: " + message.getClass().getName());
        }
    }

    private TestSuiteRunFinished createTestSuiteFinished() {
        return new TestSuiteRunFinished(
                "sessionId",
                localStartTestSuite.getBusinessObject(),
                new TestReportSummary()
                        .setDateTime(Instant.now())
                        .setResult(Result.PASSED)
                        .setLocation("http://localhost:8082/datahouse/rest/v1/items/68f0bf9e6575ab5cb50eeaeb")
        );
    }

    public class WebsocketSession implements MaestroObserver {

        private final Session session;

        public WebsocketSession(Session session) {
            this.session = session;
        }

        @Override
        public void onTestRunStarted(TestRunStarted testRunStarted) {
            String message = serDes.serializeAsString(new TestRunStartedDTO(testRunStarted.getTestId()));
            this.session.getAsyncRemote().sendText(message);
        }

        @Override
        public void onStepRunStarted(StepRunStarted stepRunStarted) {
            String message = serDes.serializeAsString(new StepRunStartedDTO(stepRunStarted));
            this.session.getAsyncRemote().sendText(message);
        }

        @Override
        public void interactWithUser(InteractWithUser interactWithUser) {
            String message = serDes.serializeAsString(new InteractWithUserDTO(interactWithUser));
            LOG.info("send message: {}", message);
            this.session.getAsyncRemote().sendText(message);
        }

        @Override
        public void onStepRunFinished(StepRunFinished stepRunFinished) {
            String message = serDes.serializeAsString(new StepRunFinishedDTO(stepRunFinished));
            this.session.getAsyncRemote().sendText(message);
        }

        @Override
        public void onTestRunFinished(TestRunFinished testRunFinished) {
            String message = serDes.serializeAsString(new TestRunFinishedDTO(testRunFinished));
            this.session.getAsyncRemote().sendText(message);
        }

        @Override
        public void onTestSuiteFinished(TestSuiteRunFinished testSuiteFinished) {
            String message = serDes.serializeAsString(new TestSuiteRunFinishedDTO(testSuiteFinished.getReportSummary()));
            LOG.info("send message: {}", message);
            this.session.getAsyncRemote().sendText(message);
        }
    }
}


