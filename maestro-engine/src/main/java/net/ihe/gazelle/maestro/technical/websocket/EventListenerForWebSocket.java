/*
 * Copyright 2025 IHE International.
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

package net.ihe.gazelle.maestro.technical.websocket;

import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.testrun.runner.FinishedStepEvent;
import net.ihe.gazelle.maestro.technical.websocket.client.TestRunSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the event broker that listen some messages used in Maestro application
 * It will send messages to the WebSocket server to notify clients about the test run status
 */
public class EventListenerForWebSocket {

    private static final Logger LOG = LoggerFactory.getLogger(EventListenerForWebSocket.class);

    private final TestRunSocketClient testRunSocketClient = new TestRunSocketClient();

    @Inject
    public EventListenerForWebSocket() {
        // Default constructor for CDI
    }

    public void onFinishStep(@ObservesAsync FinishedStepEvent finishedStepEvent) {
        testRunSocketClient.sendTestStepCompleted(finishedStepEvent.getStepRun().testSetId(),finishedStepEvent.getStepRunReport().build());
    }
}
