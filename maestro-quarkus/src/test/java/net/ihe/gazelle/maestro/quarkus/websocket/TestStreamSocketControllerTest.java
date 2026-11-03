/*
 * Copyright 2025-2026 IHE International.
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

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Session;
import net.ihe.gazelle.maestro.api.business.testreport.Result;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.technical.dto.message.StartTestRunDTO;
import net.ihe.gazelle.maestro.api.technical.dto.message.StartTestSuiteRunDTO;
import net.ihe.gazelle.maestro.api.technical.dto.message.StepRunFinishedDTO;
import net.ihe.gazelle.maestro.api.technical.dto.message.UserInteractionCompletedDTO;
import net.ihe.gazelle.maestro.quarkus.utils.ObjectFactory;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import net.ihe.gazelle.security.mocks.OIDCJWTGenerator;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActivateRequestContext
class TestStreamSocketControllerTest {

    @TestHTTPResource("/v1/test-stream/run")
    URI uri;

    @Inject
    TestWebSocketClient client;

    private final TextSerDes serDes = new JacksonSerDes();

    @BeforeEach
    void init() {
        TestWebSocketServer.clear();
    }

    @Test
    void should_send_test_run_started_message() throws DeploymentException, IOException {
        try (Session session = ContainerProvider
                .getWebSocketContainer()
                .connectToServer(client, uri)) {
            String token = OIDCJWTGenerator.getValidJwt();
            StartTestRunDTO message = new StartTestRunDTO(ObjectFactory.createTestRun(), token);
            message.setPersist(false);
            String messageString = serDes.serializeAsString(message);
            session.getAsyncRemote().sendText(messageString);

            TestReport report = client.getTestReport(session.getId());
            assertEquals(Result.PASSED, report.getResult());
            TestWebSocketClient.clear(session.getId());
        }
    }

    @Test
    void should_run_test_for_anonymous_identity_when_public_access_is_configured() throws DeploymentException, IOException {
        try (Session session = ContainerProvider
                .getWebSocketContainer()
                .connectToServer(client, uri)) {
            StartTestRunDTO message = new StartTestRunDTO(ObjectFactory.createTestRun(), "");
            message.setPersist(false);
            session.getAsyncRemote().sendText(serDes.serializeAsString(message));

            TestReport report = client.getTestReport(session.getId());
            assertEquals(Result.PASSED, report.getResult());
            TestWebSocketClient.clear(session.getId());
        }
    }

    @Test
    void should_send_test_suite_run_started_message() throws DeploymentException, IOException {
        TestWebSocketServer.clear();
        try (Session session = ContainerProvider
                .getWebSocketContainer()
                .connectToServer(client, uri)) {
            String token = OIDCJWTGenerator.getValidJwt();
            StartTestSuiteRunDTO message = new StartTestSuiteRunDTO(ObjectFactory.createTestSuiteRun(), token);
            message.setPersist(false);
            String messageString = serDes.serializeAsString(message);
            session.getAsyncRemote().sendText(messageString);

            TestReport report = client.getTestReport(session.getId());
            assertEquals(Result.PASSED, report.getResult());
            TestWebSocketClient.clear(session.getId());
        }
    }

    @Test
    void should_throw_test_run_invalid() throws DeploymentException, IOException {
        TestWebSocketServer.clear();
        try (Session session = ContainerProvider
                .getWebSocketContainer()
                .connectToServer(client, uri)) {
            String token = OIDCJWTGenerator.getValidJwt();
            StartTestRunDTO message = new StartTestRunDTO(ObjectFactory.createInvalidTestRun(), token);
            message.setPersist(false);
            String messageString = serDes.serializeAsString(message);
            session.getAsyncRemote().sendText(messageString);

            String error = client.getError(session.getId());
            assertNotNull(error);
            assertThat(error, containsString("Test case is invalid, please contact an administrator."));
            TestWebSocketClient.clear(session.getId());
        }
    }

    @Test
    void should_throw_unable_parse_test_run() throws DeploymentException, IOException {
        TestWebSocketServer.clear();
        try (Session session = ContainerProvider
                .getWebSocketContainer()
                .connectToServer(client, uri)) {
            String messageString = serDes.serializeAsString("wrong json");
            session.getAsyncRemote().sendText(messageString);

            String error = client.getError(session.getId());
            assertNotNull(error);
            assertThat(error, containsString("Unable to parse test case, please contact an administrator."));
            TestWebSocketClient.clear(session.getId());
        }
    }

    @Test
    void should_complete_user_interaction() throws DeploymentException, IOException {
        TestWebSocketServer.clear();
        try (Session session = ContainerProvider
                .getWebSocketContainer()
                .connectToServer(client, uri)) {
            UserInteractionCompletedDTO message = new UserInteractionCompletedDTO();
            String messageString = serDes.serializeAsString(message);
            assertDoesNotThrow(() -> session.getAsyncRemote().sendText(messageString));
            TestWebSocketClient.clear(session.getId());
        }
    }

    /*@Test
    void should_throw_unexpected_message_type() throws DeploymentException, IOException {
        TestWebSocketServer.clear();
        try (Session session = ContainerProvider
                .getWebSocketContainer()
                .connectToServer(client, uri)) {
            String messageString = "{\"type\":\"" + StepRunFinishedDTO.TYPE + "\"}";
            session.getAsyncRemote().sendText(messageString);

            String error = client.getError(session.getId());
            assertNotNull(error);
            assertThat(error, containsString("Unexpected message type: " + StepRunFinishedDTO.class.getName()));
            TestWebSocketClient.clear(session.getId());
        }
    }*/
}
