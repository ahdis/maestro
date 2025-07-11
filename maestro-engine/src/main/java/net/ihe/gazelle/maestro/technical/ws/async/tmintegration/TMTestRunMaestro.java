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

package net.ihe.gazelle.maestro.technical.ws.async.tmintegration;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSetRun;
import net.ihe.gazelle.maestro.business.engine.MaestroProducer;
import net.ihe.gazelle.maestro.technical.client.TestReportClient;
import net.ihe.gazelle.maestro.technical.jobs.testrunevents.FinishTestSetEvent;
import net.ihe.gazelle.maestro.technical.ws.async.TestRunStore;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is temporary, used to execute Maestro engine asynchronously when called by TM
 * <br> It emits a test-run channel start event and listen for test-run finish event to send report to TM
 */
@ApplicationScoped
public class TMTestRunMaestro {

    private final Map<String, TestRunStore> tmRunningServices;
    private final MaestroProducer maestroProducer;
    private final TestReportClient client;

    @Inject
    public TMTestRunMaestro(MaestroProducer maestroProducer, TestReportClient client) {
        this.tmRunningServices = new ConcurrentHashMap<>();
        this.maestroProducer = maestroProducer;
        this.client = client;
    }

    public void startTestSet(JsonNode testRunNodes) {
        TestRunStore store = new TestRunStore();
        List<TestRun> testRuns = TMScriptMapper.convertToTestRun(testRunNodes, store);
        TestSetRun testSetRun = new TestSetRun(UUID.randomUUID().toString(), testRuns);
        tmRunningServices.put(testSetRun.testSetId(), store);
        maestroProducer.executeTestSet(testSetRun);
    }

    public void callbackTM(FinishTestSetEvent finishTestSetEvent) {
        TestRunStore store = tmRunningServices.get(finishTestSetEvent.getTestSetRun().testSetId());
        client.sendTestReport(
                store.getCallbackAuthorization(),
                store.getClientCallback(),
                finishTestSetEvent.getTestReport()
        );
    }

    public boolean isServiceRunning(String testSetId) {
        return tmRunningServices.containsKey(testSetId);
    }
}
