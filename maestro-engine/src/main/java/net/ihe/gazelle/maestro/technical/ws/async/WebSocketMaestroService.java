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

package net.ihe.gazelle.maestro.technical.ws.async;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSetRun;
import net.ihe.gazelle.maestro.technical.jobs.testrunevents.StartTestSetCommand;
import net.ihe.gazelle.maestro.technical.jobs.testrunevents.TestRunEvent;

import java.util.List;

@ApplicationScoped
public class WebSocketMaestroService {

    private final Event<TestRunEvent> event;

    @Inject
    public WebSocketMaestroService(Event<TestRunEvent> event) {
        this.event = event;
    }

    public void executeTestSet(String testSetId, List<TestRun> testRuns) {
        TestSetRun testSetRun = new TestSetRun(testSetId, testRuns);
        event.fireAsync(new StartTestSetCommand(testSetRun));
    }
}
