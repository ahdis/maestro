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
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.test.Test;
import net.ihe.gazelle.maestro.api.business.testreport.EntityIdentification;
import net.ihe.gazelle.maestro.api.business.testreport.SystemUnderTest;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;
import net.ihe.gazelle.security.business.acl.AccessControlList;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
    public static final String REQUIRED = "required";
    public static final String BYTE_ARRAY = "byteArray";
    public static final String STRING = "string";
    public static final String DOUBLE = "double";

    private TMScriptMapper() {
        // Private constructor to prevent instantiation
    }

    /**
     * New method that returns a TestSuiteRun for the updated JSON structure
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
    public static TestSuiteRun convertToTestSetRun(JsonNode jsonNode, RestCallbackId testRunDAO) {
        try {
            validateScript(jsonNode);
            // Extract root level properties
            String testSuiteName = jsonNode.get(TEST_SUITE_NAME).asText();
            String testSetRunId = jsonNode.get(TEST_SET_RUN_ID).asText();
            JsonNode accessControlListNode = jsonNode.get(ACCESS_CONTROL_LIST);
            JsonNode systemsUnderTestNode = jsonNode.get("systemsUnderTest");
            ArrayNode testRunNodes = (ArrayNode) jsonNode.get(TEST_RUNS);

            // Create TestSuiteRun instance
            TestSuiteRun testSuiteRun = new TestSuiteRun();
            testSuiteRun.setTestSuiteName(testSuiteName);
            testSuiteRun.setTestSuiteClientId(testSetRunId);

            // Parse access control list if present
            if (accessControlListNode != null && !accessControlListNode.isNull()) {
                AccessControlList acl = parseAccessControlList(accessControlListNode);
                testSuiteRun.setAccessControlList(acl);
            }

            // Parse systemsUnderTest (optional)
            List<SystemUnderTest> systemsUnderTest = new ArrayList<>();
            if (systemsUnderTestNode != null && systemsUnderTestNode.isArray()) {
                systemsUnderTest = parseSystemsUnderTest((ArrayNode) systemsUnderTestNode);
            }
            testSuiteRun.setSystemsUnderTest(systemsUnderTest);

            // Parse test runs
            List<TestRun> testRuns = new ArrayList<>();
            for (JsonNode testRunNode : testRunNodes) {
                parseTestRunNode(testRunDAO, testRunNode, testSuiteRun, testRuns);
            }

            testSuiteRun.setTestRuns(testRuns);
            return testSuiteRun;

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

    private static void parseTestRunNode(RestCallbackId testRunDAO, JsonNode testRunNode, TestSuiteRun testSuiteRun, List<TestRun> testRuns) {
        try {
            testRunDAO.setCallbackUrl(testRunNode.get("clientCallback").asText());
            testRunDAO.setCallbackAuthorization(testRunNode.get("callbackAuthorization").asText());
            TestRun testRun = new TestRun();
            JsonNode testNode = testRunNode.get("test");
            Map<String, InputDefinition> inputDefinitions = parseTest(testNode, testRun);
            ArrayNode inputNodes = (ArrayNode) testRunNode.get("inputs");
            Set<String> availableKeys = parseInputs(inputNodes, testRun, inputDefinitions);
            trimOptionalMissingStepRefs(testRun, inputDefinitions, availableKeys);
            testRun.setTestSuiteId(testSuiteRun.getInternalId());
            testRuns.add(testRun);
        } catch (NullPointerException e) {
            throw new MissingArgumentException("Argument inputs is missing in testRun", e);
        }
    }

    private static Map<String, InputDefinition> parseTest(JsonNode testNode, TestRun testRun) {
        try {
            Test test = new Test();
            String name = testNode.get(NAME).asText();
            test.setId(name);
            test.setName(name);
            ArrayNode stepNodes = (ArrayNode) testNode.get("steps");
            parseStep(stepNodes, test);
            testRun.setTest(test);
            // Parse test declared inputs with required flag
            Map<String, InputDefinition> definitions = new HashMap<>();
            JsonNode declaredInputs = testNode.get("inputs");
            if (declaredInputs != null && declaredInputs.isArray()) {
                for (JsonNode declaredInput : declaredInputs) {
                    String key = declaredInput.get("key").asText();
                    boolean required = true; // default to required when unspecified
                    if (declaredInput.has(REQUIRED) && !declaredInput.get(REQUIRED).isNull()) {
                        required = declaredInput.get(REQUIRED).asBoolean();
                    }
                    String type = null;
                    if (declaredInput.hasNonNull("type")) {
                        type = declaredInput.get("type").asText();
                    }
                    definitions.put(key, new InputDefinition(type, required));
                }
            }
            return definitions;
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
                Step step = new Step()
                      .setId(name)
                      .setName(name)
                      .setType(type)
                      .setProperties(parseProperties(propertyNodes));
                test.addStep(step);
            } catch (NullPointerException e) {
                throw new MissingArgumentException("Argument inputs is missing in steps");
            }
        }
    }

    private static List<Property> parseProperties(ArrayNode propertyNodes) {
        List<Property> properties = new ArrayList<>();
        for (JsonNode propertyNode : propertyNodes) {
            try {
                String key = propertyNode.get("key").asText();
                String value = propertyNode.get("value").asText();
                ReferenceParser referenceParser = new ReferenceParser();
                if (referenceParser.isReference(value)) {
                    properties.add(new PropertyDTO(key, value, BYTE_ARRAY).createPropertyRef(referenceParser.getReference(value)));
                } else  {
                    properties.add(new PropertyDTO(key, value, STRING).createProperty());
                }
            } catch (NullPointerException e) {
                throw new MissingArgumentException("Argument inputs is missing in properties");
            }
        }
        return properties;
    }

    private static Set<String> parseInputs(ArrayNode inputNodes, TestRun testRun, Map<String, InputDefinition> inputDefinitions) {
        Set<String> providedKeys = new HashSet<>();
        Set<String> availableKeys = new HashSet<>();
        if (inputNodes != null) {
            for (JsonNode inputNode : inputNodes) {
                addInputToTestRun(testRun, inputNode, inputDefinitions, providedKeys, availableKeys);
            }
        }
        if (inputDefinitions != null) {
            for (Map.Entry<String, InputDefinition> entry : inputDefinitions.entrySet()) {
                String key = entry.getKey();
                InputDefinition definition = entry.getValue();
                boolean required = definition == null || definition.required();
                if (required && !providedKeys.contains(key)) {
                    testRun.addInput(createDefaultProperty(key, definition));
                    availableKeys.add(key);
                }
            }
        }
        return availableKeys;
    }

    private static void addInputToTestRun(TestRun testRun,
                                          JsonNode inputNode,
                                          Map<String, InputDefinition> inputDefinitions,
                                          Set<String> providedKeys,
                                          Set<String> availableKeys) {
        try {
            String key = inputNode.get("key").asText();
            providedKeys.add(key);
            String value = inputNode.get("value").asText();
            if (value != null && !value.isBlank()) {
                availableKeys.add(key);
            }
            InputDefinition definition = inputDefinitions != null ? inputDefinitions.get(key) : null;
            String declaredType = definition != null ? definition.type() : null;
            String instanceType = inputNode.hasNonNull("type") ? inputNode.get("type").asText() : null;
            String propertyType = resolvePropertyType(declaredType, instanceType);
            String adjustedValue = adjustValueForInput(value, declaredType, instanceType, propertyType);
            PropertyDTO propertyDTO = new PropertyDTO(key, adjustedValue, propertyType);
            testRun.addInput(propertyDTO.createProperty());
        } catch (NullPointerException e) {
            throw new MissingArgumentException("Argument inputs is missing in inputs");
        }
    }

    private static void trimOptionalMissingStepRefs(TestRun testRun, Map<String, InputDefinition> inputDefinitions, Set<String> availableKeys) {
        if (testRun == null || testRun.getTest() == null || inputDefinitions == null) {
            return;
        }
        for (Step step : testRun.getTest().getSteps()) {
            List<Property<?>> properties = step.getProperties();
            if (properties == null) {
                continue;
            }
            List<Property<?>> adjusted = new ArrayList<>();
            constructAdjustedProperties(inputDefinitions, availableKeys, properties, adjusted);
            step.setProperties(adjusted);
        }
    }

    private static void constructAdjustedProperties(Map<String, InputDefinition> inputDefinitions,
                                                    Set<String> availableKeys,
                                                    List<Property<?>> properties,
                                                    List<Property<?>> adjusted) {
        for (Property<?> property : properties) {
            if (property != null) {
                if (property instanceof PropertyRef<?> ref) {
                    String refKey = ref.getReference();
                    InputDefinition definition = inputDefinitions.get(refKey);
                    boolean required = definition == null || definition.required();
                    boolean available = availableKeys != null && availableKeys.contains(refKey);
                    if (!required && !available) {
                        // Skip optional and missing reference
                        continue;
                    }
                }
                adjusted.add(property);
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

    private static String resolvePropertyType(String declaredType, String instanceType) {
        String source = firstNonBlank(declaredType, instanceType);
        if (source == null) {
            return STRING;
        }
        String normalized = source.trim().toLowerCase(Locale.ROOT);
        if (normalized.contains("file") || normalized.contains("byte")) {
            return BYTE_ARRAY;
        }
        if (normalized.contains("text") || normalized.contains(STRING)) {
            return STRING;
        }
        if (normalized.contains("bool")) {
            return "boolean";
        }
        if (normalized.contains("int")) {
            return "integer";
        }
        if (normalized.contains(DOUBLE) || normalized.contains("float") || normalized.contains("number")) {
            return DOUBLE;
        }
        if (normalized.contains("date")) {
            return "date";
        }
        return STRING;
    }

    private static Property<?> createDefaultProperty(String key, InputDefinition definition) {
        String propertyType = resolvePropertyType(definition != null ? definition.type() : null, null);
        return switch (propertyType) {
            case "boolean" -> new Property<>(key, Boolean.FALSE);
            case "integer" -> new Property<>(key, 0);
            case DOUBLE -> new Property<>(key, 0.0d);
            case BYTE_ARRAY -> new Property<>(key, new byte[0]);
            case "date" -> new Property<>(key, new Date(0L));
            default -> new Property<>(key, "");
        };
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private static String adjustValueForInput(String value, String declaredType, String instanceType, String propertyType) {
        if (value == null) {
            return null;
        }
        if (!STRING.equals(propertyType)) {
            return value;
        }
        String sourceType = firstNonBlank(declaredType, instanceType);
        if (sourceType == null) {
            return value;
        }
        String normalized = sourceType.trim().toLowerCase(Locale.ROOT);
        if (normalized.contains("text")) {
            try {
                byte[] decoded = Base64.getDecoder().decode(value.trim());
                return new String(decoded, StandardCharsets.UTF_8);
            } catch (IllegalArgumentException ignored) {
                // Value was not Base64 encoded; keep as-is
            }
        }
        return value;
    }

    private record InputDefinition(String type, boolean required) {
    }

}
