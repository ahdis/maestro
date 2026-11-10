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

package net.ihe.gazelle.maestro.engine.technical.userinteract;

import net.ihe.gazelle.lang.ExecutionRuntimeException;
import net.ihe.gazelle.lang.InterruptedRuntimeException;
import net.ihe.gazelle.lang.TimeoutRuntimeException;
import net.ihe.gazelle.maestro.api.business.MaestroObserver;
import net.ihe.gazelle.maestro.api.business.message.AskGrading;
import net.ihe.gazelle.maestro.api.business.message.GradingCompleted;
import net.ihe.gazelle.maestro.api.business.userinteract.GradingHandler;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Implementation of {@link GradingHandler} that delegates user interaction
 * events to a {@link MaestroObserver}.
 */
public class GradingHandlerImpl implements GradingHandler {

    private final MaestroObserver observer;

    /**
     * Creates a new {@code GradingHandlerImpl} using the specified observer.
     *
     * @param observer the {@link MaestroObserver} that will receive user interaction events
     */
    public GradingHandlerImpl(MaestroObserver observer) {
        this.observer = observer;
    }

    @Override
    public GradingCompleted displayMessage(List<String> criteria, long timeout) {
        CompletableFuture<GradingCompleted> future = observer.askGrading(
                new AskGrading().setCriteria(criteria)
        );

        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new TimeoutRuntimeException("Timeout while waiting for grading", e);
        } catch (ExecutionException e) {
            throw new ExecutionRuntimeException(e);
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
            throw new InterruptedRuntimeException("Interrupted while waiting for grading");
        }
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
