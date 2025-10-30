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

package net.ihe.gazelle.maestro.quarkus.broker;

import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.message.Message;
import net.ihe.gazelle.maestro.api.business.message.StartStepRun;
import net.ihe.gazelle.maestro.api.business.message.StepRunFinished;
import net.ihe.gazelle.maestro.engine.business.StepRunEventProducer;
import net.ihe.gazelle.maestro.engine.business.StepRunner;

public class StepRunnerEventController implements StepRunEventProducer {

   private final StepRunner stepRunner;
   private final Event<Message> messageEvent;

   @Inject
   public StepRunnerEventController(StepRunner stepRunner, Event<Message> messageEvent) {
      this.stepRunner = stepRunner;
      this.messageEvent = messageEvent;
   }

   public void onStartStepRun(@ObservesAsync StartStepRun startStepRun) {
      stepRunner.run(startStepRun);
   }

   @Override
   public void notify(StepRunFinished stepRunFinished) {
      messageEvent.fireAsync(stepRunFinished);
   }
}
