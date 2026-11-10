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

import jakarta.websocket.Session;
import net.ihe.gazelle.lang.IORuntimeException;
import net.ihe.gazelle.maestro.api.business.MaestroObserver;
import net.ihe.gazelle.maestro.api.business.message.*;
import net.ihe.gazelle.maestro.api.technical.dto.message.*;
import net.ihe.gazelle.maestro.engine.business.RecordedOutputsObserver;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a WebSocket session that listens to Maestro events and handles user interactions.
 * Implements {@link MaestroObserver} to receive updates from test execution.
 */
public class WebSocketSession implements MaestroObserver, RecordedOutputsObserver {

    private static final Logger LOG = LoggerFactory.getLogger(WebSocketSession.class.getName());

    private final TextSerDes serDes = new JacksonSerDes();
    private final Session session;
    private Instant expirationTime;
    private final boolean liveOutputsEnabled;
    private final Set<String> evidenceSessions = ConcurrentHashMap.newKeySet();
    private CompletableFuture<UserInteractionCompleted> interactionFuture;
    private CompletableFuture<GradingCompleted> gradingFuture;

    /**
     * Constructs a new WebSocketSession for the given WebSocket {@link Session}.
     *
     * @param session the WebSocket session to be wrapped
     */
    public WebSocketSession(Session session) {
        this(session, false);
    }

    /**
     * Constructs a new WebSocketSession for the given WebSocket session and live output setting.
     *
     * @param session the WebSocket session to be wrapped
     * @param liveOutputsEnabled whether live output recording and forwarding is enabled for this session
     */
    public WebSocketSession(Session session, boolean liveOutputsEnabled) {
        this.session = session;
        this.liveOutputsEnabled = liveOutputsEnabled;
    }

    /**
     * Returns the expiration time of this WebSocket session.
     *
     * @return the instant at which this WebSocket session expires
     */
    public Instant getExpirationTime() {
        return expirationTime;
    }

    /**
     * Sets the expiration time for this WebSocket session.
     *
     * @param expirationTime the instant at which this WebSocket session expires
     * @return this WebSocket session
     */
    public WebSocketSession setExpirationTime(Instant expirationTime) {
        this.expirationTime = expirationTime;
        return this;
    }

    /**
     * Completes a pending user interaction asynchronously by completing the associated future.
     *
     * @param userInteractionCompleted the result of the user interaction
     */
    public void completeInteraction(UserInteractionCompleted userInteractionCompleted) {
        if (interactionFuture != null && !interactionFuture.isDone()) {
            interactionFuture.complete(userInteractionCompleted);
        }
    }

    /**
     * Completes the grading process by resolving the associated future with the specified grading result.
     *
     * @param gradingCompleted an instance of {@code GradingCompleted} that contains the details
     *                         of the completed grading, such as session ID, grading result,
     *                         criteria reports, and any associated notes or metadata
     */
    public void completeGrading(GradingCompleted gradingCompleted) {
        if (gradingFuture != null && !gradingFuture.isDone()) {
            gradingFuture.complete(gradingCompleted);
        }
    }

    @Override
    public void onTestRunStarted(TestRunStarted testRunStarted) {
        evidenceSessions.add(testRunStarted.getSessionId());
        String message = serDes.serializeAsString(new TestRunStartedDTO(testRunStarted));
        LOG.debug("Test Run Started: {}", message);
        this.session.getAsyncRemote().sendText(message);
    }

    @Override
    public void onStepRunStarted(StepRunStarted stepRunStarted) {
        String message = serDes.serializeAsString(new StepRunStartedDTO(stepRunStarted));
        LOG.debug("Step Run Started: {}", message);
        this.session.getAsyncRemote().sendText(message);
    }

    @Override
    public CompletableFuture<UserInteractionCompleted> interactWithUser(
            InteractWithUser interactWithUser) {
        String message = serDes.serializeAsString(new InteractWithUserDTO(interactWithUser));
        LOG.debug("Interact with user: {}", message);
        this.session.getAsyncRemote().sendText(message);
        this.interactionFuture = new CompletableFuture<>();
        return interactionFuture;
    }

    @Override
    public void onStepRunFinished(StepRunFinished stepRunFinished) {
        String message = serDes.serializeAsString(new StepRunFinishedDTO(stepRunFinished));
        LOG.debug("Step Run Finished: {}", message);
        this.session.getAsyncRemote().sendText(message);
    }

    /**
     * Verifies that the specified evidence session is associated with this execution connection.
     *
     * @param sessionId the identifier of the evidence session to validate
     * @throws IllegalArgumentException if the session ID is null or is not associated with this execution connection
     */
    public void assertEvidenceSession(String sessionId) {
        if (sessionId == null || !evidenceSessions.contains(sessionId)) {
            throw new IllegalArgumentException("Evidence must belong to this execution connection");
        }
    }

    @Override
    public void onEvidenceRecorded(PutEvidence evidence) {
        this.session
                .getAsyncRemote()
                .sendText(serDes.serializeAsString(new EvidenceRecordedDTO(evidence)));
    }

    @Override
    public void onOutputsRecorded(StepRunFinished recorded) {
        if (!liveOutputsEnabled || recorded.getStepRunReport().getOutputs().isEmpty()) {
            return;
        }
        this.session
                .getAsyncRemote()
                .sendText(
                        serDes.serializeAsString(
                                new StepOutputsAvailableDTO(
                                        new StepOutputsAvailable(
                                                recorded.getTestId(),
                                                recorded.getStepName(),
                                                recorded.getStepRunReport().getDateTime(),
                                                recorded.getStepRunReport().getOutputs()))));
    }

    @Override
    public void onTestRunFinished(TestRunFinished testRunFinished) {
        String message = serDes.serializeAsString(new TestRunFinishedDTO(testRunFinished));
        LOG.debug("Test Run Finished: {}", message);
        this.session.getAsyncRemote().sendText(message);
    }

    @Override
    public void onExecutionFinished(ExecutionFinished executionFinished) {
        String message = serDes.serializeAsString(new ExecutionFinishedDTO(executionFinished));
        LOG.debug("Execution Finished: {}", message);
        this.session.getAsyncRemote().sendText(message);
        closeSession();
    }

    @Override
    public CompletableFuture<GradingCompleted> askGrading(AskGrading askGrading) {
        String message = serDes.serializeAsString(new AskGradingDTO(askGrading));
        LOG.debug("Ask grading: {}", message);
        this.session.getAsyncRemote().sendText(message);
        this.gradingFuture = new CompletableFuture<>();
        return gradingFuture;
    }

    private void closeSession() {
        if (session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }
    }
}
