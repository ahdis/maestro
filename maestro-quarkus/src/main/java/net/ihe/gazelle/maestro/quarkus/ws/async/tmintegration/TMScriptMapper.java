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
import com.fasterxml.jackson.databind.node.ArrayNode;
import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.Step;
import net.ihe.gazelle.maestro.api.business.testrun.Test;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSetRun;
import net.ihe.gazelle.maestro.api.business.testreport.EntityIdentification;
import net.ihe.gazelle.maestro.api.business.testreport.SystemUnderTest;
import net.ihe.gazelle.maestro.business.MissingArgumentException;
import net.ihe.gazelle.maestro.quarkus.ws.async.TestRunStore;
import net.ihe.gazelle.security.business.acl.AccessControlList;

import java.util.*;

/**
 * This class is temporary, used for converting TM script to maestro script
 * We store the clientCallback URL and authorization token until the end of engine execution
 */
public class TMScriptMapper {

    public static final String TEST_SUITE_NAME = "testSuiteName";
    public static final String TEST_SET_RUN_ID = "testSetRunId";
    public static final String ACCESS_CONTROL_LIST = "accessControlList";
    public static final String TEST_RUNS = "testRuns";
    public static final String OWNERS = "owners";
    public static final String VERSION = "version";
    public static final String NAME = "name";

    private TMScriptMapper() {
        // Private constructor to prevent instantiation
    }

    /**
     * New method that returns a TestSetRun for the updated JSON structure
     * Expected JSON structure:
     * {
     *   "testSuiteName": "...",
     *   "accessControlList": { ... },
     *   "systemsUnderTest": [
     *     {
     *       "systemIdentification": {"name": "...", "version": "..."},
     *       "macAddresses": ["..."],
     *       "ipAddresses": ["..."],
     *       "hostNames": ["..."]
     *     }
     *   ],
     *   "testRuns": [ ... ]
     * }
     */
    public static TestSetRun convertToTestSetRun(JsonNode jsonNode, TestRunStore testRunDAO) {
        try {
            validateScript(jsonNode);
            // Extract root level properties
            String testSuiteName = jsonNode.get(TEST_SUITE_NAME).asText();
            String testSetRunId = jsonNode.get(TEST_SET_RUN_ID).asText();
            JsonNode accessControlListNode = jsonNode.get(ACCESS_CONTROL_LIST);
            JsonNode systemsUnderTestNode = jsonNode.get("systemsUnderTest");
            ArrayNode testRunNodes = (ArrayNode) jsonNode.get(TEST_RUNS);

            // Create TestSetRun instance
            TestSetRun testSetRun = new TestSetRun();
            testSetRun.setTestSuiteName(testSuiteName);
            testSetRun.setTestSetRunId(testSetRunId);

            // Parse access control list if present
            if (accessControlListNode != null && !accessControlListNode.isNull()) {
                AccessControlList acl = parseAccessControlList(accessControlListNode);
                testSetRun.setAccessControlList(acl);
            }

            // Parse systemsUnderTest (optional)
            List<SystemUnderTest> systemsUnderTest = new ArrayList<>();
            if (systemsUnderTestNode != null && systemsUnderTestNode.isArray()) {
                systemsUnderTest = parseSystemsUnderTest((ArrayNode) systemsUnderTestNode);
            }
            testSetRun.setSystemsUnderTest(systemsUnderTest);

            // Parse test runs
            List<TestRun> testRuns = new ArrayList<>();
            for (JsonNode testRunNode : testRunNodes) {
                parseTestRunNode(testRunDAO, testRunNode, testSetRun, testRuns);
            }

            testSetRun.setTestRuns(testRuns);
            return testSetRun;

        } catch (NullPointerException e) {
            throw new MissingArgumentException("Required root properties (testSuiteName, testRuns) are missing");
        }
    }

    private static List<SystemUnderTest> parseSystemsUnderTest(ArrayNode sutNodes) {
        List<SystemUnderTest> systems = new ArrayList<>();
        for (JsonNode sutNode : sutNodes) {
            systems.add(parseSingleSystemUnderTest(sutNode));
        }
        return systems;
    }

    private static SystemUnderTest parseSingleSystemUnderTest(JsonNode sutNode) {
        SystemUnderTest sut = new SystemUnderTest();

        EntityIdentification identification = parseEntityIdentification(sutNode.get("systemIdentification"));
        if (identification != null) {
            sut.setSystemIdentification(identification);
        }

        List<String> macs = parseStringArray(sutNode.get("macAddresses"));
        sut.setMacAddresses(macs);

        List<String> ips = parseStringArray(sutNode.get("ipAddresses"));
        sut.setIpAddresses(ips);

        List<String> hosts = parseStringArray(sutNode.get("hostNames"));
        sut.setHostNames(hosts);

        return sut;
    }

    private static EntityIdentification parseEntityIdentification(JsonNode idNode) {
        if (idNode == null || idNode.isNull()) {
            return null;
        }
        EntityIdentification id = new EntityIdentification();
        String name = idNode.get(NAME).asText();
        if (idNode.hasNonNull(NAME) && !name.isBlank()) {
            id.setName(name);
        }
        String version = idNode.get(VERSION).asText();
        if (idNode.hasNonNull(VERSION) && !version.isBlank()) {
            id.setVersion(version);
        }
        return id;
    }

    private static List<String> parseStringArray(JsonNode node) {
        if (node == null || !node.isArray()) {
            return List.of();
        }
        List<String> values = new ArrayList<>();
        node.forEach(n -> values.add(n.asText()));
        return values;
    }

    private static void parseTestRunNode(TestRunStore testRunDAO, JsonNode testRunNode, TestSetRun testSetRun, List<TestRun> testRuns) {
        try {
            testRunDAO.setClientCallback(testRunNode.get("clientCallback").asText());
            testRunDAO.setCallbackAuthorization(testRunNode.get("callbackAuthorization").asText());
            TestRun testRun = new TestRun();
            JsonNode testNode = testRunNode.get("test");
            parseTest(testNode, testRun);
            ArrayNode inputNodes = (ArrayNode) testRunNode.get("inputs");
            parseInputs(inputNodes, testRun);
            testRun.setTestSetId(testSetRun.getTestSetId());
            testRuns.add(testRun);
        } catch (NullPointerException e) {
            throw new MissingArgumentException("Argument inputs is missing in testRun");
        }
    }

    private static void parseTest(JsonNode testNode, TestRun testRun) {
        try {
            Test test = new Test();
            String name = testNode.get(NAME).asText();
            test.setId(name);
            test.setName(name);
            ArrayNode stepNodes = (ArrayNode) testNode.get("steps");
            parseStep(stepNodes, test);
            testRun.setTest(test);
        } catch (NullPointerException e) {
            throw new MissingArgumentException("Argument inputs is missing in test");
        }
    }

    private static void parseStep(ArrayNode stepNodes, Test test) {
        for (JsonNode stepNode : stepNodes) {
            try {
                String name = stepNode.get("id").asText();
                String type = stepNode.get("@type").asText();
                type = adaptTypeIfNeeded(type);
                ArrayNode propertyNodes = (ArrayNode) stepNode.get("properties");
                StepFactoryProvider provider = new StepFactoryProvider();
                Step step = provider.create(type, name, parseProperties(propertyNodes));
                test.addStep(step);
            } catch (NullPointerException e) {
                throw new MissingArgumentException("Argument inputs is missing in steps");
            }
        }
    }

    private static Map<String, Property<?>> parseProperties(ArrayNode propertyNodes) {
        Map<String, Property<?>> properties = new HashMap<>();
        for (JsonNode propertyNode : propertyNodes) {
            try {
                String key = propertyNode.get("key").asText();
                String value = propertyNode.get("value").asText();
                ReferenceParser referenceParser = new ReferenceParser();
                if (referenceParser.isReference(value)) {
                    properties.put(key, new PropertyDTO(key, value, "byteArray").createPropertyRef(referenceParser.getReference(value)));
                } else  {
                    properties.put(key, new PropertyDTO(key, value, "string").createProperty());
                }
            } catch (NullPointerException e) {
                throw new MissingArgumentException("Argument inputs is missing in properties");
            }
        }
        return properties;
    }

    private static void parseInputs(ArrayNode inputNodes, TestRun testRun) {
        for (JsonNode inputNode : inputNodes) {
            try {
                PropertyDTO propertyDTO = new PropertyDTO(
                        inputNode.get("key").asText(),
                        inputNode.get("value").asText(),
                        "byteArray");
                testRun.addInputs(propertyDTO.createProperty());
            } catch (NullPointerException e) {
                throw new MissingArgumentException("Argument inputs is missing in inputs");
            }
        }
    }

    private static AccessControlList parseAccessControlList(JsonNode aclNode) {
        AccessControlList acl = new AccessControlList();
        try {
            Set<String> owners = parseStringSet(aclNode.get(OWNERS));
            acl.setOwners(owners);

            Set<String> readers = parseStringSet(aclNode.get("readers"));
            acl.setReaders(readers);

            Set<String> editors = parseStringSet(aclNode.get("editors"));
            acl.setEditors(editors);

            Boolean isPublic = parseBoolean(aclNode.get("isPublic"));
            acl.setPublic(isPublic);


            String readAccessKey = parseText(aclNode.get("readAccessKey"));
            if (readAccessKey != null) {
                acl.setReadAccessKey(readAccessKey);
            }

        } catch (Exception e) {
            throw new MissingArgumentException("Error parsing AccessControlList: " + e.getMessage());
        }
        return acl;
    }

    private static Set<String> parseStringSet(JsonNode node) {
        if (node == null || !node.isArray()) {
            return Set.of();
        }
        Set<String> values = new HashSet<>();
        for (JsonNode item : node) {
            values.add(item.asText());
        }
        return values;
    }

    private static Boolean parseBoolean(JsonNode node) {
        if (node == null || node.isNull()) {
            return Boolean.FALSE;
        }
        return node.asBoolean();
    }

    private static String parseText(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        return node.asText();
    }

    private static void validateScript(JsonNode jsonNode) {
        if (jsonNode.get(TEST_SUITE_NAME) == null || jsonNode.get(TEST_SUITE_NAME).isNull()) {
            throw new MissingArgumentException("Argument testSuiteName is missing");
        }
        if (jsonNode.get(TEST_RUNS) == null || !jsonNode.get(TEST_RUNS).isArray() || jsonNode.get(TEST_RUNS).isEmpty()) {
            throw new MissingArgumentException("Argument testRuns is missing or empty");
        }
        if( jsonNode.get(TEST_SET_RUN_ID) == null || jsonNode.get(TEST_SET_RUN_ID).isNull()) {
            throw new MissingArgumentException("Argument testSetRunId is missing");
        }
        if (jsonNode.get(ACCESS_CONTROL_LIST) != null && !jsonNode.get(ACCESS_CONTROL_LIST).isNull()) {
            JsonNode aclNode = jsonNode.get(ACCESS_CONTROL_LIST);
            if (aclNode.get(OWNERS) == null || !aclNode.get(OWNERS).isArray() || aclNode.get(OWNERS).isEmpty()) {
                throw new MissingArgumentException("Argument owners is missing or empty in accessControlList");
            }
        }

    }

    private static String adaptTypeIfNeeded(String type) {
        // Temporary adaptation for known discrepancies
        if("ITBStep".equals(type)) {
            return "ITB";
        }
        return type;
    }

}
