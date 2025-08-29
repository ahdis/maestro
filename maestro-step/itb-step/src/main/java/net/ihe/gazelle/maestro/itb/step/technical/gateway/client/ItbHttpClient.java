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

package net.ihe.gazelle.maestro.itb.step.technical.gateway.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ihe.gazelle.maestro.itb.step.business.request.Parameter;
import net.ihe.gazelle.maestro.itb.step.business.request.TestCase;
import net.ihe.gazelle.maestro.itb.step.business.request.TestRequest;
import net.ihe.gazelle.maestro.itb.step.business.request.TestSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

/**
 * Adapter to execute HTTP requests to ITB (Interoperability Test Bed)
 */
public class ItbHttpClient implements ItbClient {

    private static final Logger logger = LoggerFactory.getLogger(ItbHttpClient.class);
    public static final String INPUT = "input";

    private Map<String, Object> map;

    private List<Map<String, Object>> inputMapping;

    private final ObjectMapper mapper = new ObjectMapper();
    private final String baseItbUrl;
    private final String itbApiKey;

    public ItbHttpClient(String baseItbUrl, String itbApiKey) {
        this.baseItbUrl = baseItbUrl;
        this.itbApiKey = itbApiKey;
    }

    @Override
    public String startTest(TestRequest testRequest) {
        List<Parameter> requestParams = testRequest.getParameters();
        map = new HashMap<>();
        inputMapping = new ArrayList<>();
        // Go through all the request parameters to set ITB request
        setUpRequestParameters(requestParams);
        // Go through all the test suite parameters to set ITB request
        TestSuite ts = testRequest.getTestSuite();
        setUpTestSuite(ts);
        // Go through all the test cases parameters to set ITB request
        setUpTestCases(testRequest.getTestCases());
        if (!inputMapping.isEmpty()) map.put("inputMapping", inputMapping);
        return sendTestRequest();
    }

    @Override
    public String getTestLogs(String sessionID) {
        if (sessionID != null) {
            try {
                Map<String, Object> content = new HashMap<>();
                List<String> sessionIDs = new ArrayList<>();
                sessionIDs.add(sessionID);
                content.put("session", sessionIDs);
                content.put("withLogs", true);
                String body = mapper.writeValueAsString(content);
                return sendPOSTRequest(baseItbUrl + "/api/rest/tests/status", "application/json", body);
            } catch (JsonProcessingException e) {
                throw new ItbHttpClientException("Error parsing request body : " + e);
            }
        }
        return "";
    }

    @Override
    public byte[] requestPDFReport(String sessionID, String testCaseName) {
        if (sessionID != null) {
            //Set up http request
            try (HttpClient client = HttpClient.newHttpClient()) {
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(baseItbUrl + "/api/rest/tests/report/" + sessionID))
                        .header("ITB_API_KEY", itbApiKey)
                        .header("Accept", "application/pdf")
                        .GET()
                        .build();
                // Execute request and return response to the controller
                try {
                    HttpResponse<byte[]> response = client.send(req, HttpResponse.BodyHandlers.ofByteArray());
                    return response.body();
                } catch (IOException e) {
                    throw new ItbHttpClientException("Error request interrupted requesting PDF report. Try again.");
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new ItbHttpClientException("Error request interrupted requesting PDF report. Try again.");
                }
            }
        }
        return new byte[0];
    }

    /**
     * Method to convert an additional Parameter object in a json input for an ITB test entity
     *
     * @param param Parameter object
     * @return Map created from the object
     */
    private Map<String, Object> setUpAdditionalInput(Parameter param) {
        Map<String, Object> input = new HashMap<>();
        input.put("name", param.getName());
        if (param.getType().equals("file")) {
            input.put("embeddingMethod", "BASE64");
            input.put("type", "binary");

        } else if (param.getType().equals("integer") || param.getType().equals("float")) {
            input.put("type", "number");
        }
        input.put("value", param.getValue());
        return input;
    }

    /**
     * Method to convert a Parameter object in a json input for an ITB test request
     *
     * @param param Parameter object
     * @return Map created from the object
     */
    private Map<String, Object> setUpRequestParameter(Parameter param) {
        Map<String, Object> input = new HashMap<>();
        String name = param.getName();
        switch (name) {
            case "systemID" -> map.put("system", param.getValue());
            case "actorID" -> map.put("actor", param.getValue());
            default -> input = setUpAdditionalInput(param);
        }
        return input;
    }

    /**
     * Method to set up all the request parameters to construct ITB request
     *
     * @param requestParams list of request parameters
     */
    private void setUpRequestParameters(List<Parameter> requestParams) {
        // Go through all the request parameters to set ITB request
        for (Parameter requestParam : requestParams) {
            Map<String, Object> input = setUpRequestParameter(requestParam);
            if (!input.isEmpty()) {
                Map<String, Object> inputWrapper = new HashMap<>();
                inputWrapper.put(INPUT, input);
                inputMapping.add(inputWrapper);
            }
        }
    }

    /**
     * Method to set up ITB test suite execution request
     *
     * @param ts test suite to execute
     */
    private void setUpTestSuite(TestSuite ts) {
        if (Objects.nonNull(ts)) {
            map.put("testSuite", new String[]{ts.getSuiteID()});
            if (Objects.nonNull(ts.getParameters())) {
                for (Parameter suiteParameter : ts.getParameters()) {
                    Map<String, Object> input = setUpAdditionalInput(suiteParameter);
                    Map<String, Object> inputWrapper = new HashMap<>();
                    inputWrapper.put("testSuite", new String[]{ts.getSuiteID()});
                    inputWrapper.put(INPUT, input);
                    inputMapping.add(inputWrapper);
                }
            }
        }
    }

    /**
     * Method to set up ITB test cases execution request
     *
     * @param tcs list of test cases to execute
     */
    private void setUpTestCases(List<TestCase> tcs) {
        if (Objects.nonNull(tcs)) {
            List<String> tcsName = new ArrayList<>();
            for (TestCase tc : tcs) {
                tcsName.add(tc.getCaseID());
                if (Objects.nonNull(tc.getParameters())) {
                    for (Parameter caseParameter : tc.getParameters()) {
                        Map<String, Object> input = setUpAdditionalInput(caseParameter);
                        inputMapping.add(Map.of(
                            "testCase", new String[]{tc.getCaseID()},
                                INPUT, input
                        ));
                    }
                }
            }
            tcsName.toArray(new String[0]);
            map.put("testCase", tcsName);
        }
    }

    /**
     * Method to send ITB test request through the REST API
     *
     * @return Created session ID
     */
    private String sendTestRequest() {
        // Convert our map with data request in ITB body request
        String body;
        try {
            body = mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new ItbHttpClientException("Error parsing request body : " + e);
        }
        return sendPOSTRequest(baseItbUrl + "/api/rest/tests/start", "application/json", body);
    }

    /**
     * Method to send a POST Request to ITB REST API
     *
     * @param uri       endpoint
     * @param mediatype media type accepted
     * @param body      request content
     * @return ITB response
     */
    private String sendPOSTRequest(String uri, String mediatype, String body) {
        //Set up http request
        try(HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .header("ITB_API_KEY", itbApiKey)
                    .header("Content-Type", mediatype)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            // Execute request and return response to the controller
            try {
                HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
                String respBody = response.body();
                JsonNode content = mapper.readTree(respBody);
                if (response.statusCode() == 400) {
                    throw new ItbHttpClientException("ITB request error : " + content.get("error_description"));
                }
                return content.get("createdSessions").get(0).get("session").asText();
            } catch (IOException e) {
                logger.error("Error sending POST request", e);
                throw new ItbHttpClientException("Error request interrupted. Try again.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ItbHttpClientException("Error request interrupted. Try again.");
            }
        }
    }
}
