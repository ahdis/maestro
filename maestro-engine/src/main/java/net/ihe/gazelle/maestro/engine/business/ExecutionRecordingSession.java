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

package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.maestro.api.business.message.StepRunFinished;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;

/** Recording lifecycle owned by one execution, separate from runner state. */
public interface ExecutionRecordingSession {
    /** Records detached step outputs; returns a detached event, or null when unsupported. */
    StepRunFinished recordStep(StepRunFinished finished);

    /**
     * Records a finished step and passes the detached event to the observer once its outputs are stored.
     * Implementations may record in the background so that slow storage does not delay the execution;
     * {@link #finish(TestReport)} then waits for pending steps first. Recording failures are not thrown here:
     * they are retained and handled when the session finishes.
     *
     * @param finished the finished step event
     * @param observer receives the recorded projection, or null when nobody listens
     */
    default void submitStep(StepRunFinished finished, RecordedOutputsObserver observer) {
        try {
            StepRunFinished recorded = recordStep(finished);
            if (recorded != null && observer != null) {
                observer.onOutputsRecorded(recorded);
            }
        } catch (RuntimeException retainedForFinish) {
            // Implementations retain the failure for finalization.
        }
    }

    /**
     * Waits for pending steps, then records the final report and reuses the step outputs already stored.
     * A failed incremental write must not be retried when its storage outcome is uncertain.
     *
     * @param report the complete execution report, including evidence collected after step completion
     * @return the saved report location and recorded projection for completion notifications
     *
     * @throws TestReportRecordingException when the final report cannot be recorded
     */
    ExecutionRecordingResult finish(TestReport report);
}
