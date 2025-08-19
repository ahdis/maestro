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
import net.ihe.gazelle.maestro.business.MissingArgumentException;
import net.ihe.gazelle.maestro.quarkus.ws.async.TestRunStore;
import net.ihe.gazelle.security.business.acl.AccessControlList;

import java.util.*;

/**
 * This class is temporary, used for converting TM script to maestro script
 * We store the clientCallback URL and authorization token until the end of engine execution
 */
public class TMScriptMapper {

    private TMScriptMapper() {
        // Private constructor to prevent instantiation
    }

    /**
     * New method that returns a TestSetRun for the updated JSON structure
     * Expected JSON structure:
     * {
     *   "testSuiteName": "...",
     *   "accessControlList": { ... },
     *   "testRuns": [ ... ]
     * }
     */
    public static TestSetRun convertToTestSetRun(JsonNode jsonNode, TestRunStore testRunDAO) {
        try {
            // Extract root level properties
            String testSuiteName = jsonNode.get("testSuiteName").asText();
            JsonNode accessControlListNode = jsonNode.get("accessControlList");
            ArrayNode testRunNodes = (ArrayNode) jsonNode.get("testRuns");

            // Create TestSetRun instance
            TestSetRun testSetRun = new TestSetRun();
            testSetRun.setTestSuiteName(testSuiteName);

            // Parse access control list if present
            if (accessControlListNode != null && !accessControlListNode.isNull()) {
                AccessControlList acl = parseAccessControlList(accessControlListNode);
                testSetRun.setAccessControlList(acl);
            }

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
            String name = testNode.get("name").asText();
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
            // Parse owners
            JsonNode ownersNode = aclNode.get("owners");
            if (ownersNode != null && ownersNode.isArray()) {
                Set<String> owners = new HashSet<>();
                for (JsonNode ownerNode : ownersNode) {
                    owners.add(ownerNode.asText());
                }
                acl.setOwners(owners);
            }

            // Parse readers
            JsonNode readersNode = aclNode.get("readers");
            if (readersNode != null && readersNode.isArray()) {
                Set<String> readers = new HashSet<>();
                for (JsonNode readerNode : readersNode) {
                    readers.add(readerNode.asText());
                }
                acl.setReaders(readers);
            }

            // Parse editors
            JsonNode editorsNode = aclNode.get("editors");
            if (editorsNode != null && editorsNode.isArray()) {
                Set<String> editors = new HashSet<>();
                for (JsonNode editorNode : editorsNode) {
                    editors.add(editorNode.asText());
                }
                acl.setEditors(editors);
            }

            // Parse isPublic
            JsonNode isPublicNode = aclNode.get("isPublic");
            if (isPublicNode != null) {
                acl.setPublic(isPublicNode.asBoolean());
            }

            // Parse readAccessKey
            JsonNode readAccessKeyNode = aclNode.get("readAccessKey");
            if (readAccessKeyNode != null && !readAccessKeyNode.isNull()) {
                acl.setReadAccessKey(readAccessKeyNode.asText());
            }

        } catch (Exception e) {
            throw new MissingArgumentException("Error parsing AccessControlList: " + e.getMessage());
        }
        return acl;
    }

}
