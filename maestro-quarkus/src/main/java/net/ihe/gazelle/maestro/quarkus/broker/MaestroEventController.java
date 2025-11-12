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

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.message.*;
import net.ihe.gazelle.maestro.engine.business.MaestroEventProducer;
import net.ihe.gazelle.maestro.engine.business.MaestroImpl;

/**
 * This class is the event broker that listen to all kind of messages used in Maestro application It will trigger
 * application code depending on which event is fired.
 */
@RequestScoped
public class MaestroEventController implements MaestroEventProducer {

   private final MaestroImpl maestroImpl;
   private final Event<Message> messageEvent;

   @Inject
   public MaestroEventController(MaestroImpl maestroImpl, Event<Message> messageEvent) {
      this.maestroImpl = maestroImpl;
      this.messageEvent = messageEvent;
   }

   @Override
   public void command(StartTestSuiteRun startTestSuiteRun) {
      messageEvent.fireAsync(startTestSuiteRun);
   }

    @Override
    public void command(StartTestRun startTestRun) {
        messageEvent.fireAsync(startTestRun);
    }

   public void onStartTestRun(@ObservesAsync StartTestRun startTestRun) {
      maestroImpl.listen(startTestRun);
   }

   public void onStartStepRun(@ObservesAsync StartStepRun startStepRun) {
      maestroImpl.listen(startStepRun);
   }

   public void onStepRunFinished(@ObservesAsync StepRunFinished stepRunFinished) {
      maestroImpl.listen(stepRunFinished);
   }

   public void onTestRunFinished(@ObservesAsync TestRunFinished testRunFinished) {
      maestroImpl.listen(testRunFinished);
   }

   public void onTestSuiteRunFinished(@ObservesAsync TestSuiteRunFinished testSuiteRunFinished) {
      maestroImpl.listen(testSuiteRunFinished);
   }

}
