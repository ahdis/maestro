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

package net.ihe.gazelle.maestro.quarkus.ws.async.tmintegration;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.testrun.MaestroEventProducer;
import net.ihe.gazelle.maestro.api.business.testrun.TestSetRun;
import net.ihe.gazelle.maestro.api.technical.event.FinishTestSetEvent;
import net.ihe.gazelle.maestro.quarkus.client.TestReportClient;
import net.ihe.gazelle.maestro.quarkus.ws.async.TestRunStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is temporary, used to execute Maestro engine asynchronously when called by TM
 * <br> It emits a test-run channel start event and listen for test-run finish event to send report to TM
 */
@ApplicationScoped
public class TMTestRunMaestro {

    private final Map<String, TestRunStore> tmRunningServices;
    private final MaestroEventProducer maestroEventProducer;
    private final TestReportClient client;

    @Inject
    public TMTestRunMaestro(MaestroEventProducer maestroEventProducer, TestReportClient client) {
        this.tmRunningServices = new ConcurrentHashMap<>();
        this.maestroEventProducer = maestroEventProducer;
        this.client = client;
    }

    public void startTestSet(JsonNode testSetRunNode) {
        TestRunStore store = new TestRunStore();
        TestSetRun testSetRun = TMScriptMapper.convertToTestSetRun(testSetRunNode, store);
        tmRunningServices.put(testSetRun.getTestSetId(), store);
        maestroEventProducer.executeTestSet(testSetRun);
    }

    public void callbackTM(FinishTestSetEvent finishTestSetEvent) {
        TestRunStore store = tmRunningServices.get(finishTestSetEvent.getTestSetRun().getTestSetId());
        client.sendTestReport(
                store.getCallbackAuthorization(),
                store.getClientCallback(),
                finishTestSetEvent.getReportSummary()
        );
    }

    public boolean isServiceRunning(String testSetId) {
        return tmRunningServices.containsKey(testSetId);
    }
}
