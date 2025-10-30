package net.ihe.gazelle.maestro.itb.step.technical.handler;

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.itb.step.business.ItbStep;
import net.ihe.gazelle.maestro.itb.step.business.request.Parameter;
import net.ihe.gazelle.maestro.itb.step.business.request.TestCase;
import net.ihe.gazelle.maestro.itb.step.business.request.TestRequest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ItbHandlerImplTest {

    @Test
    void stepToTestRequestMapper_supportsTextAndFileInputs() throws Exception {
        ItbHandlerImpl handler = new ItbHandlerImpl();
        ItbStep step = new ItbStep();

        Property<String> testCase = new Property<>(ItbStep.TEST_CASE, "TC-42");
        Property<String> systemId = new Property<>(ItbStep.SYSTEM_ID, "SYS-9");
        Property<String> actorId = new Property<>(ItbStep.ACTOR_ID, "ACT-7");
        Property<String> textInput = new Property<>("note", "hello world");
        Property<byte[]> fileInput = new Property<>("payload", "bytes".getBytes(StandardCharsets.UTF_8));

        step.setProperties(List.of(testCase, systemId, actorId, textInput, fileInput));

        Method mapper = ItbHandlerImpl.class.getDeclaredMethod("stepToTestRequestMapper", ItbStep.class);
        mapper.setAccessible(true);
        TestRequest request = (TestRequest) mapper.invoke(handler, step);

        assertEquals("ACT-7", request.getTestID());
        List<TestCase> cases = request.getTestCases();
        assertEquals(1, cases.size());
        assertEquals("TC-42", cases.getFirst().getCaseID());

        List<Parameter> parameters = cases.getFirst().getParameters();
        assertEquals(2, parameters.size());

        Parameter textParam = parameters.stream().filter(p -> "note".equals(p.getName())).findFirst().orElse(null);
        assertNotNull(textParam);
        assertEquals("text", textParam.getType());
        assertEquals("hello world", textParam.getValue());

        Parameter fileParam = parameters.stream().filter(p -> "payload".equals(p.getName())).findFirst().orElse(null);
        assertNotNull(fileParam);
        assertEquals("file", fileParam.getType());
        assertEquals(Base64.getEncoder().encodeToString("bytes".getBytes(StandardCharsets.UTF_8)), fileParam.getValue());
    }
}
