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

package net.ihe.gazelle.maestro.spi.business;

import net.ihe.gazelle.lang.ExecutionRuntimeException;
import net.ihe.gazelle.lang.InterruptedRuntimeException;
import net.ihe.gazelle.lang.TimeoutRuntimeException;
import net.ihe.gazelle.maestro.api.business.property.MissingPropertyException;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;

/**
 * Interface to perform the execution of a step
 */
public interface StepExecutor {

    /**
     * The method that trigger step execution
     *
     * @param stepRun contain a step to run
     * @return the report with information to build a step report
     * @throws MissingPropertyException if a required property is missing to execute the step
     * @throws ClassCastException       if a property has not the expected type
     */
    StepRunReport execute(StepRun stepRun);

    /**
     * Executes the supplied step in a separate thread and waits for its completion
     * until the timeout configured on the step run is reached.
     *
     * @param stepRun the step run to execute
     * @param expirationTimestamp the expiration timestamp for the step execution
     * @return the report produced by the step execution
     * @throws TimeoutRuntimeException     if the execution does not complete before the configured timeout
     * @throws ExecutionRuntimeException   if the execution fails while running asynchronously
     * @throws InterruptedRuntimeException if the current thread is interrupted while waiting for completion
     */
    default StepRunReport execute(StepRun stepRun, Instant expirationTimestamp) {
        if (expirationTimestamp == null) {
            return execute(stepRun);
        } else {
            long timeoutMillis = Duration.between(Instant.now(), expirationTimestamp).toMillis();
            stepRun.setTimeout(timeoutMillis);
            CompletableFuture<StepRunReport> future = CompletableFuture.supplyAsync(
                    () -> execute(stepRun),
                    Executors.newSingleThreadExecutor()
            );

            try {
                return future.get(timeoutMillis, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                future.cancel(true);
                throw new TimeoutRuntimeException("Timeout while waiting for " + stepRun.getType(), e);
            } catch (ExecutionException e) {
                throw new ExecutionRuntimeException(e);
            } catch (InterruptedException _) {
                future.cancel(true);
                Thread.currentThread().interrupt();
                throw new InterruptedRuntimeException("Interrupted while waiting for " + stepRun.getType());
            }
        }
    }

}
