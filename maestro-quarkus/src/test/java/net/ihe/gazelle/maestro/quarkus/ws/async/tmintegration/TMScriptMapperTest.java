/*
 * Copyright 2025 Kereval.
 */
package net.ihe.gazelle.maestro.quarkus.ws.async.tmintegration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.ihe.gazelle.maestro.api.business.testreport.SystemUnderTest;
import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSetRun;
import net.ihe.gazelle.maestro.business.MissingArgumentException;
import net.ihe.gazelle.maestro.quarkus.ws.async.TestRunStore;
import net.ihe.gazelle.security.business.acl.AccessControlList;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TMScriptMapperTest {

    private final ObjectMapper mapper = new ObjectMapper();

    private ObjectNode baseRoot() {
        ObjectNode root = mapper.createObjectNode();
        root.put(TMScriptMapper.TEST_SUITE_NAME, "suite-1");
        root.put(TMScriptMapper.TEST_SET_RUN_ID, "tsr-123");
        // Minimal ACL with owners
        ObjectNode acl = mapper.createObjectNode();
        ArrayNode owners = mapper.createArrayNode().add("alice");
        acl.set(TMScriptMapper.OWNERS, owners);
        root.set(TMScriptMapper.ACCESS_CONTROL_LIST, acl);
        return root;
    }

    private ObjectNode minimalTestRunNode(String testName) {
        ObjectNode testRun = mapper.createObjectNode();
        testRun.put("clientCallback", "http://callback");
        testRun.put("callbackAuthorization", "Bearer token");
        ObjectNode testNode = mapper.createObjectNode();
        testNode.put(TMScriptMapper.NAME, testName);
        testNode.set("steps", mapper.createArrayNode()); // no steps to avoid ServiceLoader dependency
        testRun.set("test", testNode);
        testRun.set("inputs", mapper.createArrayNode()); // empty inputs
        return testRun;
    }

    @Test
    void convertToTestSetRun_mapsTopLevel_and_SystemsUnderTest_and_TestRuns() {
        ObjectNode root = baseRoot();
        // Systems under test
        ObjectNode sut = mapper.createObjectNode();
        ObjectNode id = mapper.createObjectNode();
        id.put(TMScriptMapper.NAME, "sys");
        id.put(TMScriptMapper.VERSION, "1.0");
        sut.set("systemIdentification", id);
        sut.set("macAddresses", mapper.createArrayNode().add("aa:bb"));
        sut.set("ipAddresses", mapper.createArrayNode().add("1.2.3.4"));
        sut.set("hostNames", mapper.createArrayNode().add("host"));
        root.set("systemsUnderTest", mapper.createArrayNode().add(sut));

        // Test runs
        ArrayNode runs = mapper.createArrayNode();
        runs.add(minimalTestRunNode("test-A"));
        root.set(TMScriptMapper.TEST_RUNS, runs);

        TestRunStore store = new TestRunStore();
        TestSetRun tsr = TMScriptMapper.convertToTestSetRun(root, store);

        assertEquals("suite-1", tsr.getTestSuiteName());
        assertEquals("tsr-123", tsr.getTestSetRunId());
        AccessControlList acl = tsr.getAccessControlList();
        assertTrue(acl.getOwners().contains("alice"));

        List<SystemUnderTest> suts = tsr.getSystemsUnderTest();
        assertEquals(1, suts.size());
        assertEquals("sys", suts.getFirst().getSystemIdentification().getName());
        assertEquals("1.0", suts.getFirst().getSystemIdentification().getVersion());
        assertEquals(List.of("aa:bb"), suts.getFirst().getMacAddresses());
        assertEquals(List.of("1.2.3.4"), suts.getFirst().getIpAddresses());
        assertEquals(List.of("host"), suts.getFirst().getHostNames());

        List<TestRun> trs = tsr.getTestRuns();
        assertEquals(1, trs.size());
        assertEquals("test-A", trs.getFirst().getTest().getName());
        assertEquals("test-A", trs.getFirst().getTest().getId());
        assertEquals(0, trs.getFirst().getTest().getSteps().size());

        assertEquals("http://callback", store.getClientCallback());
        assertEquals("Bearer token", store.getCallbackAuthorization());
    }

    @Test
    void convertToTestSetRun_setsInputs_asByteArrays() {
        ObjectNode root = baseRoot();
        ArrayNode runs = mapper.createArrayNode();
        ObjectNode run = minimalTestRunNode("test-B");
        String content = Base64.getEncoder().encodeToString("hello".getBytes(StandardCharsets.UTF_8));
        ArrayNode inputs = mapper.createArrayNode();
        inputs.add(mapper.createObjectNode().put("key", "payload").put("value", content));
        run.set("inputs", inputs);
        ObjectNode testNode = (ObjectNode) run.get("test");
        ArrayNode declaredInputs = mapper.createArrayNode();
        declaredInputs.add(mapper.createObjectNode().put("key", "payload").put("type", "file"));
        testNode.set("inputs", declaredInputs);
        runs.add(run);
        root.set(TMScriptMapper.TEST_RUNS, runs);

        TestSetRun tsr = TMScriptMapper.convertToTestSetRun(root, new TestRunStore());
        Property<?> in = tsr.getTestRuns().getFirst().getInputs().getFirst();
        assertEquals("payload", in.getName());
        assertArrayEquals("hello".getBytes(StandardCharsets.UTF_8), (byte[]) in.getValue());
    }

    @Test
    void convertToTestSetRun_usesDeclaredInputTypes() {
        ObjectNode root = baseRoot();
        ArrayNode runs = mapper.createArrayNode();
        ObjectNode run = minimalTestRunNode("test-C");

        ObjectNode testNode = (ObjectNode) run.get("test");
        ArrayNode declaredInputs = mapper.createArrayNode();
        declaredInputs.add(mapper.createObjectNode().put("key", "textInput").put("type", "text").put("required", true));
        declaredInputs.add(mapper.createObjectNode().put("key", "flag").put("type", "boolean").put("required", true));
        declaredInputs.add(mapper.createObjectNode().put("key", "count").put("type", "number").put("required", true));
        testNode.set("inputs", declaredInputs);

        ArrayNode inputs = mapper.createArrayNode();
        inputs.add(mapper.createObjectNode().put("key", "textInput").put("value", "alpha"));
        inputs.add(mapper.createObjectNode().put("key", "flag").put("value", "true"));
        inputs.add(mapper.createObjectNode().put("key", "count").put("value", "5.5"));
        run.set("inputs", inputs);

        runs.add(run);
        root.set(TMScriptMapper.TEST_RUNS, runs);

        TestSetRun tsr = TMScriptMapper.convertToTestSetRun(root, new TestRunStore());
        List<Property<?>> properties = tsr.getTestRuns().getFirst().getInputs();

        Property<?> text = properties.stream().filter(p -> "textInput".equals(p.getName())).findFirst().orElseThrow();
        assertInstanceOf(String.class, text.getValue());
        assertEquals("alpha", text.getValue());

        Property<?> flag = properties.stream().filter(p -> "flag".equals(p.getName())).findFirst().orElseThrow();
        assertInstanceOf(Boolean.class, flag.getValue());
        assertEquals(Boolean.TRUE, flag.getValue());

        Property<?> count = properties.stream().filter(p -> "count".equals(p.getName())).findFirst().orElseThrow();
        assertInstanceOf(Double.class, count.getValue());
        assertEquals(5.5d, (Double) count.getValue(), 1e-9);
    }

    @Test
    void validateScript_errors_on_missing_required_fields() {
        // Missing testSuiteName
        ObjectNode root1 = mapper.createObjectNode();
        root1.set(TMScriptMapper.TEST_RUNS, mapper.createArrayNode().add(mapper.createObjectNode()));
        root1.put(TMScriptMapper.TEST_SET_RUN_ID, "tsr");
        TestRunStore testRunDAO = new TestRunStore();
        MissingArgumentException ex1 = assertThrows(MissingArgumentException.class, () -> TMScriptMapper.convertToTestSetRun(root1, testRunDAO));
        assertEquals("Argument testSuiteName is missing", ex1.getMessage());

        // Missing testRuns
        ObjectNode root2 = mapper.createObjectNode();
        root2.put(TMScriptMapper.TEST_SUITE_NAME, "suite");
        root2.put(TMScriptMapper.TEST_SET_RUN_ID, "tsr");
        MissingArgumentException ex2 = assertThrows(MissingArgumentException.class, () -> TMScriptMapper.convertToTestSetRun(root2, testRunDAO));
        assertEquals("Argument testRuns is missing or empty", ex2.getMessage());

        // Missing testSetRunId
        ObjectNode root3 = mapper.createObjectNode();
        root3.put(TMScriptMapper.TEST_SUITE_NAME, "suite");
        root3.set(TMScriptMapper.TEST_RUNS, mapper.createArrayNode().add(mapper.createObjectNode()));
        MissingArgumentException ex3 = assertThrows(MissingArgumentException.class, () -> TMScriptMapper.convertToTestSetRun(root3, testRunDAO));
        assertEquals("Argument testSetRunId is missing", ex3.getMessage());

        // ACL present but owners missing or empty
        ObjectNode root4 = mapper.createObjectNode();
        root4.put(TMScriptMapper.TEST_SUITE_NAME, "suite");
        root4.put(TMScriptMapper.TEST_SET_RUN_ID, "tsr");
        root4.set(TMScriptMapper.TEST_RUNS, mapper.createArrayNode().add(mapper.createObjectNode()));
        ObjectNode acl = mapper.createObjectNode();
        acl.set(TMScriptMapper.OWNERS, mapper.createArrayNode());
        root4.set(TMScriptMapper.ACCESS_CONTROL_LIST, acl);
        MissingArgumentException ex4 = assertThrows(MissingArgumentException.class, () -> TMScriptMapper.convertToTestSetRun(root4, testRunDAO));
        assertEquals("Argument owners is missing or empty in accessControlList", ex4.getMessage());
    }

    @Test
    void convertToTestSetRun_parsesFullAccessControlList() {
        ObjectNode root = baseRoot();
        ObjectNode acl = (ObjectNode) root.get(TMScriptMapper.ACCESS_CONTROL_LIST);
        acl.set("readers", mapper.createArrayNode().add("r1").add("r2"));
        acl.set("editors", mapper.createArrayNode().add("e1"));
        acl.put("isPublic", true);
        acl.put("readAccessKey", "key-1");

        ArrayNode runs = mapper.createArrayNode();
        runs.add(minimalTestRunNode("t"));
        root.set(TMScriptMapper.TEST_RUNS, runs);

        TestSetRun tsr = TMScriptMapper.convertToTestSetRun(root, new TestRunStore());
        AccessControlList parsed = tsr.getAccessControlList();
        assertEquals(Set.of("alice"), parsed.getOwners());
        assertEquals(Set.of("r1", "r2"), parsed.getReaders());
        assertEquals(Set.of("e1"), parsed.getEditors());
        assertTrue(parsed.isPublic());
        assertEquals("key-1", parsed.getReadAccessKey());
    }
}
