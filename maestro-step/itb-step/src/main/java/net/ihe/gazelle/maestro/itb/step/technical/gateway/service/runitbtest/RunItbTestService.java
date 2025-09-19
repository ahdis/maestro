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

package net.ihe.gazelle.maestro.itb.step.technical.gateway.service.runitbtest;


import net.ihe.gazelle.maestro.itb.step.business.request.Parameter;
import net.ihe.gazelle.maestro.itb.step.business.request.TestCase;
import net.ihe.gazelle.maestro.itb.step.business.request.TestRequest;
import net.ihe.gazelle.maestro.itb.step.business.request.TestSuite;
import net.ihe.gazelle.maestro.itb.step.technical.gateway.client.ItbHttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Service to execute a test suite
 */
public class RunItbTestService {

    private final ItbHttpClient itbHttpClient;

    public RunItbTestService(ItbHttpClient itbHttpClient) {
        this.itbHttpClient = itbHttpClient;
    }

    /**
     * Method to execute a test request
     *
     * @param request test request to execute
     * @return json response from test service call
     */
    public String runTests(TestRequest request) {
        TestRequest validRequest = validateRequest(request);
        return itbHttpClient.startTest(validRequest);
    }

    /**
     * Method to validate a test request input with an API Key format
     *
     * @param param content to validate
     * @return true if validation succeeded otherwise false
     */
    private boolean validateAPIKey(Parameter param) {
        if (param.getValue().length() != 36) {
            if (Objects.nonNull(param.getType())) {
                throw new RunItbTestServiceException(param.getName() + " length is incorrect");
            } else {
                return false;
            }
        }

        if (!param.getValue().matches("[A-FX0-9]*")) {
            if (Objects.nonNull(param.getType())) {
                throw new RunItbTestServiceException("Wrong format for parameter APIKey : " + param.getName());
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Method to validate a test request input with a UUID format
     *
     * @param param content to validate
     * @return true if validation succeeded otherwise false
     */
    private boolean validateUuid(Parameter param) throws RunItbTestServiceException {
        try {
            UUID.fromString(param.getValue());
        } catch (IllegalArgumentException e) {
            if (Objects.nonNull(param.getType())) {
                throw new RunItbTestServiceException("Wrong format for parameter uuid : " + param.getName() + " : " + e);
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Method to validate a test request input file with a base64 format
     *
     * @param file content to validate
     * @return true if validation succeeded otherwise false
     */
    private boolean validateFile(Parameter file) {
        if (!file.getValue().matches("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$")) {
            if (Objects.nonNull(file.getType())) throw new RunItbTestServiceException("Wrong format for file : " + file.getName());
            else return false;
        }
        return true;
    }


    /**
     * Method to validate a test request input with an integer format
     *
     * @param param content to validate
     * @return true if validation succeeded otherwise false
     */
    private boolean validateInteger(Parameter param) {
        try {
            Integer.parseInt(param.getValue());
        } catch (NumberFormatException e) {
            if (Objects.nonNull(param.getType()))
                throw new RunItbTestServiceException("Wrong format for integer parameter : " + param.getName());
            else return false;
        }
        return true;
    }

    /**
     * Method to validate a test request input with a float format
     *
     * @param param content to validate
     * @return true if validation succeeded otherwise false
     */
    private boolean validateFloat(Parameter param) {
        try {
            Float.parseFloat(param.getValue());
        } catch (NumberFormatException e) {
            if (Objects.nonNull(param.getType()))
                throw new RunItbTestServiceException("Wrong format for float parameter : " + param.getName());
            else return false;
        }
        return true;
    }


    /**
     * Method to validate a parameter with a defined type
     *
     * @param param content to validate
     */
    private void validateParamWithType(Parameter param) {
        switch (param.getType()) {
           case "text" -> validateText(param);
            case "uuid" -> validateUuid(param);
            case "APIKey" -> validateAPIKey(param);
            case "file" -> validateFile(param);
            case "integer" -> validateInteger(param);
            case "float" -> validateFloat(param);
            default -> throw new RunItbTestServiceException("Wrong type for parameter " + param.getName() + " : " + param.getType());
        }
    }

    /**
     * Method to validate a parameter without a defined type
     *
     * @param param content to validate
     * @return identified type
     */
    private String validateParamWithoutType(Parameter param) {
        if (param.getValue().isEmpty()) throw new RunItbTestServiceException(param.getName() + " parameter value is empty");
        String type = "text";
        try {
            if (validateUuid(param)) type = "uuid";
            if (validateAPIKey(param)) type = "APIKey";
            if (validateFile(param)) type = "file";
            if (validateInteger(param)) type = "integer";
            if (validateFloat(param)) type = "float";
        } catch (RunItbTestServiceException ignored) {
            // should not happen as we are in the case where type is unknown
        }
        return type;
    }

    /**
     * Method to validate a test parameter
     *
     * @param param    parameter to validate
     * @param category type of parameter (request, test suite, test case)
     * @return the validated parameter
     */
    private Parameter validateParam(Parameter param, String category) {
        // check attributes are set
        if (Objects.isNull(param)) throw new RunItbTestServiceException("No content for a " + category + " parameter");
        if (Objects.isNull(param.getName()) || param.getName().isEmpty())
            throw new RunItbTestServiceException("A " + category + " parameter name is missing");
        if (Objects.isNull(param.getValue()))
            throw new RunItbTestServiceException(param.getName() + " parameter value is missing");
        // check format is conform to the one indicated
        if (Objects.nonNull(param.getType())) {
            validateParamWithType(param);
        }
        // try to find conformity of the value to any of the supported type when the type is unknown
        else {
            param.setType(validateParamWithoutType(param));
        }
        return param;
    }

    /**
     * Method to get well defined test suite
     *
     * @param ts test suite to check
     * @return the test suite if it is well-defined otherwise null
     */
    private TestSuite getCheckedSuite(TestSuite ts) {
        if (Objects.nonNull(ts) && Objects.nonNull(ts.getSuiteID()) && !ts.getSuiteID().isEmpty()) {
            return ts;
        }

        return null;
    }

    /**
     * Method to validate a single test case
     *
     * @param tc content to validate
     * @return test case potentially modified
     */
    private TestCase validateTestCase(TestCase tc) {
        if (Objects.nonNull(tc.getCaseID()) && !tc.getCaseID().isEmpty() && Objects.nonNull(tc.getParameters())) {
            List<Parameter> newTestCasesParams = new ArrayList<>();
            for (Parameter caseParam : tc.getParameters()) {
                newTestCasesParams.add(validateParam(caseParam, "test case"));
            }
            tc.setParameters(newTestCasesParams);
        }

        return tc;
    }

    /**
     * Method to validate all the test cases
     *
     * @param tcs content to validate
     * @return list of test case potentially modified
     */
    private List<TestCase> validateTestCases(List<TestCase> tcs) {
        // check if test cases have parameters and then validate them (and try to set their type if unknown)
        List<TestCase> newTestCases = new ArrayList<>();
        if (Objects.nonNull(tcs) && !tcs.isEmpty()) {
            for (TestCase tc : tcs) {
                newTestCases.add(validateTestCase(tc));
            }
        }
        return newTestCases;
    }

    /**
     * Method to validate a test suite
     *
     * @param ts content to validate
     */
    private TestSuite validateTestSuite(TestSuite ts) {
        // check if test suite has parameters and then validate them (and try to set their type if unknown)
        if (Objects.nonNull(ts) && Objects.nonNull(ts.getParameters())) {
            List<Parameter> newTestSuitesParams = new ArrayList<>();
            for (Parameter suiteParam : ts.getParameters()) {
                newTestSuitesParams.add(validateParam(suiteParam, "test suite"));
            }
            ts.setParameters(newTestSuitesParams);
        }
        return ts;
    }

    /**
     * Method to validate a test request
     *
     * @param request test request to validate
     * @return test request modified (or not)
     */
    private TestRequest validateRequest(TestRequest request) {
        // check request is set
        if (Objects.isNull(request)) throw new RunItbTestServiceException("No test request content");
        if (Objects.isNull(request.getTestID()) || request.getTestID().isEmpty())
            throw new RunItbTestServiceException("Test ID is missing");
        // In order to be understandable by the test service we while slightly modify the request
        TestRequest modifiedRequest = new TestRequest(request.getTestID(), request.getParameters());
        validateUuid(new Parameter("test ID", request.getTestID()));
        List<Parameter> params = request.getParameters();
        // if the request has parameters we validate them
        if (Objects.nonNull(params) && !params.isEmpty()) {
            for (Parameter requestParam : params) {
                validateParam(requestParam, "request");
            }
        }
        TestSuite ts = getCheckedSuite(request.getTestSuite());
        List<TestCase> tcs = request.getTestCases();
        // check if any test is indicated
        if (Objects.isNull(ts) && (Objects.isNull(tcs) || tcs.isEmpty()))
            throw new RunItbTestServiceException("No tests indicated");

        modifiedRequest.setTestSuite(validateTestSuite(ts));
        modifiedRequest.setTestCases(validateTestCases(tcs));

        return modifiedRequest;
    }

    private void validateText(Parameter param) {
        if (param.getValue().isEmpty()) {
            throw new RunItbTestServiceException(param.getName() + " parameter value is empty");
        }
    }
}
