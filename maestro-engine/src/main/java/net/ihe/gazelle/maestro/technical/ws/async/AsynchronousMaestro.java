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

package net.ihe.gazelle.maestro.technical.ws.async;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSetRun;
import net.ihe.gazelle.maestro.business.engine.MaestroProducer;
import net.ihe.gazelle.maestro.technical.client.TestReportClient;
import net.ihe.gazelle.maestro.technical.jobs.testrunevents.FinishTestSetEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class AsynchronousMaestro {

    private final Map<String, TestRunStore> runningServices;
    private final MaestroProducer maestroProducer;
    private final TestReportClient client;

    @Inject
    public AsynchronousMaestro(MaestroProducer maestroProducer, TestReportClient client) {
        this.runningServices = new ConcurrentHashMap<>();
        this.maestroProducer = maestroProducer;
        this.client = client;
    }

    public void executeTestSet(String callback, List<TestRun> testRuns) {
        TestRunStore store = new TestRunStore();
        store.setClientCallback(callback);
        TestSetRun testSetRun = new TestSetRun(UUID.randomUUID().toString(), testRuns);
        runningServices.put(testSetRun.testSetId(), store);
        maestroProducer.executeTestSet(testSetRun);
    }

    public void callback(FinishTestSetEvent finishTestSetEvent) {
        TestRunStore store = runningServices.get(finishTestSetEvent.getTestSetRun().testSetId());
        client.sendTestReport(
                store.getCallbackAuthorization(),
                store.getClientCallback(),
                finishTestSetEvent.getTestReport()
        );
    }

    public boolean isServiceRunning(String testSetId) {
        return runningServices.containsKey(testSetId);
    }
}
