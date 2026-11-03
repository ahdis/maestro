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
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a WebSocket session that listens to Maestro events and handles user interactions.
 * Implements {@link MaestroObserver} to receive updates from test execution.
 */
public class WebSocketSession implements MaestroObserver {

    private static final Logger LOG = LoggerFactory.getLogger(WebSocketSession.class.getName());

    private final TextSerDes serDes = new JacksonSerDes();
    private final Session session;
    private CompletableFuture<UserInteractionCompleted> interactionFuture;
    private CompletableFuture<GradingCompleted> gradingFuture;

    /**
     * Constructs a new WebSocketSession for the given WebSocket {@link Session}.
     *
     * @param session the WebSocket session to be wrapped
     */
    public WebSocketSession(Session session) {
        this.session = session;
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
    public CompletableFuture<UserInteractionCompleted> interactWithUser(InteractWithUser interactWithUser) {
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
