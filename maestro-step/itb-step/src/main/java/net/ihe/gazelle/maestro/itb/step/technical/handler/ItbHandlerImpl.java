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

package net.ihe.gazelle.maestro.itb.step.technical.handler;

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.maestro.itb.step.business.ItbHandler;
import net.ihe.gazelle.maestro.itb.step.business.ItbStep;
import net.ihe.gazelle.maestro.itb.step.business.request.Parameter;
import net.ihe.gazelle.maestro.itb.step.business.request.TestCase;
import net.ihe.gazelle.maestro.itb.step.business.request.TestRequest;
import net.ihe.gazelle.maestro.itb.step.technical.gateway.client.ItbHttpClient;
import net.ihe.gazelle.maestro.itb.step.technical.gateway.service.runitbtest.RunItbTestService;
import org.eclipse.microprofile.config.ConfigProvider;

import java.util.*;

public class ItbHandlerImpl implements ItbHandler {

    private static final Set<String> CORE_PROPERTY_NAMES = Set.of(ItbStep.TEST_CASE, ItbStep.SYSTEM_ID, ItbStep.ACTOR_ID);


    public ItbHandlerImpl() {
        // Empty
    }

    @Override
    public String handle(ItbStep step) {
        String baseItbUrl = Objects.requireNonNull(ConfigProvider.getConfig().getValue("itb.api.url", String.class));
        String itbAuthorization = Objects.requireNonNull(ConfigProvider.getConfig().getValue("itb.api.key", String.class));
        ItbHttpClient itbHttpClient = new ItbHttpClient(baseItbUrl, itbAuthorization);
        RunItbTestService runItbTestService = new RunItbTestService(itbHttpClient);

        TestRequest testRequest = stepToTestRequestMapper(step);
        return runItbTestService.runTests(testRequest);
    }

    private TestRequest stepToTestRequestMapper(ItbStep step) {
        TestRequest testRequest = new TestRequest();
        testRequest.setTestID(step.getActorID().getValue());
        TestCase testCase = new TestCase();
        Optional<Property<String>> propertyOptional = step.getProperties().stream()
                .filter(property -> ItbStep.TEST_CASE.equals(property.getName()) && property.getValue() instanceof String)
                .map(property -> (Property<String>) property)
                .findFirst();
        testCase.setTestCase(propertyOptional.orElseThrow().getValue());

        List<Parameter> parameters = new ArrayList<>();
        for (Property<?> property : step.getProperties()) {
            if (CORE_PROPERTY_NAMES.contains(property.getName())) {
                continue;
            }
            Parameter parameter = new Parameter();
            parameter.setName(property.getName());
            try {
                Object value = property.getValue();
                if (value instanceof byte[] bytes) {
                    parameter.setType("file");
                    parameter.setValue(Base64.getEncoder().encodeToString(bytes));
                } else if (value instanceof String str) {
                    parameter.setType("text");
                    parameter.setValue(str);
                } else if (value != null) {
                    parameter.setType("text");
                    parameter.setValue(String.valueOf(value));
                } else {
                    continue;
                }
                parameters.add(parameter);
            } catch (NoSuchElementException e) {
                // Do nothing, this means the property is not set
            }
        }
        testCase.setParameters(parameters);
        testRequest.setParameters(List.of());
        testRequest.setTestCases(List.of(testCase));
        return testRequest;
    }

    /**
     * Override from {@link Handler} Interface
     */
    @Override
    public boolean isAvailable() {
        return true;
    }

}
