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

import java.util.concurrent.CompletableFuture;
import net.ihe.gazelle.maestro.api.business.MaestroObserver;
import net.ihe.gazelle.maestro.api.business.message.*;
import net.ihe.gazelle.maestro.engine.business.RecordedOutputsObserver;

final class WorkflowObserver implements MaestroObserver, RecordedOutputsObserver {
    final CompletableFuture<InteractWithUser> interaction = new CompletableFuture<>();
    final CompletableFuture<UserInteractionCompleted> answer = new CompletableFuture<>();
    final CompletableFuture<StepRunFinished> recorded = new CompletableFuture<>();
    final CompletableFuture<ExecutionFinished> finished = new CompletableFuture<>();
    @Override public CompletableFuture<UserInteractionCompleted> interactWithUser(InteractWithUser request) {
        interaction.complete(request);
        return answer;
    }
    @Override public void onOutputsRecorded(StepRunFinished event) { recorded.complete(event); }
    @Override public void onExecutionFinished(ExecutionFinished event) { finished.complete(event); }
    @Override public CompletableFuture<GradingCompleted> askGrading(AskGrading request) {
        return CompletableFuture.failedFuture(new UnsupportedOperationException("No grading in this scenario"));
    }
}
