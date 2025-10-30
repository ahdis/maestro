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

package net.ihe.gazelle.maestro.quarkus.ws;

import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.type.TypeFactory;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import net.ihe.gazelle.maestro.api.business.InvalidTestRunException;
import net.ihe.gazelle.maestro.api.business.Maestro;
import net.ihe.gazelle.maestro.api.business.MaestroObserver;
import net.ihe.gazelle.maestro.api.business.SynchronousMaestro;
import net.ihe.gazelle.maestro.api.business.UnsupportedPropertyTypeException;
import net.ihe.gazelle.maestro.api.business.testreport.Result;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportSummary;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;
import net.ihe.gazelle.maestro.api.technical.dto.report.TestReportSummaryDTO;
import net.ihe.gazelle.maestro.api.technical.dto.testrun.TestRunDTO;
import net.ihe.gazelle.maestro.api.technical.dto.testrun.TestSuiteRunDTO;
import net.ihe.gazelle.maestro.engine.business.MaestroImpl;
import net.ihe.gazelle.maestro.engine.business.TestRunner;
import net.ihe.gazelle.maestro.engine.business.context.SessionStoreImpl;
import net.ihe.gazelle.maestro.quarkus.UnknownStepType;
import net.ihe.gazelle.maestro.quarkus.factory.TestRunMockEvent;
import net.ihe.gazelle.maestro.quarkus.mock.InMemoryTestReportRecordService;
import net.ihe.gazelle.maestro.quarkus.ws.async.tmintegration.MissingArgumentException;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStep;
import net.ihe.gazelle.security.technical.acl.AccessControlListDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestRunControllerTest {

    @Mock
    SynchronousMaestro synchronousMaestro;

    @Mock
    MaestroImpl maestroImpl;

    TestRunControllerImpl controller;

    @BeforeEach
    void setUp() {
        controller = new TestRunControllerImpl(synchronousMaestro, maestroImpl);
    }

    @Test
    void executeTestSuiteWithoutCallbackReturnsOkResponseWithSummaryLocation() throws Exception {
        TestReportSummary summary = new TestReportSummary()
                .setId("summary-id")
                .setLocation("http://datahouse/items/summary-id")
                .setResult(Result.PASSED)
                .setDateTime(new Date());
        when(synchronousMaestro.executeTestSuite(any())).thenReturn(summary);

        Response response = controller.executeTestSuite(testSetExecuteTestSuiteDto(), null);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        TestReportSummaryDTO entity = (TestReportSummaryDTO) response.getEntity();
        assertNotNull(entity);
        assertEquals("http://datahouse/items/summary-id", entity.getLocation());

        ArgumentCaptor<TestSuiteRun> testSetRunCaptor = ArgumentCaptor.forClass(TestSuiteRun.class);
        verify(synchronousMaestro).executeTestSuite(testSetRunCaptor.capture());
        assertEquals(1, testSetRunCaptor.getValue().getTestRuns().size());
        verifyNoInteractions(maestroImpl);
    }

    @Test
    void executeTestSuiteWithCallbackTriggersAsyncExecutionAndReturnsAccepted() {
        Response response = controller.executeTestSuite(testSetExecuteTestSuiteDto(), "http://callback");

        assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());

        ArgumentCaptor<TestSuiteRun> testSetRunCaptor = ArgumentCaptor.forClass(TestSuiteRun.class);
        ArgumentCaptor<MaestroObserver> callerCaptor = ArgumentCaptor.forClass(MaestroObserver.class);
        verify(maestroImpl).executeTestSuite(testSetRunCaptor.capture(), callerCaptor.capture());
        assertEquals(1, testSetRunCaptor.getValue().getTestRuns().size());
        verifyNoInteractions(synchronousMaestro);
    }

    @Test
    void runWithoutCallbackInvalidTestExecuteTestSuiteReturnsBadRequest() throws Exception {
        when(synchronousMaestro.executeTestSuite(any())).thenThrow(new InvalidTestRunException("invalid"));

        Response response = controller.executeTestSuite(testSetExecuteTestSuiteDto(), null);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("bad format"));
    }

    @Test
    void executeTestSuiteWithoutCallbackTimeoutReturnsGatewayTimeout() throws Exception {
        when(synchronousMaestro.executeTestSuite(any())).thenThrow(new TimeoutException("too long"));

        Response response = controller.executeTestSuite(testSetExecuteTestSuiteDto(), null);

        assertEquals(Response.Status.GATEWAY_TIMEOUT.getStatusCode(), response.getStatus());
        assertEquals(SynchronousMaestro.TIMEOUT_MESSAGE, response.getEntity());
    }

    @Test
    void executeTestSuiteWithoutCallbackWebApplicationExceptionReturnsGatewayTimeout() throws Exception {
        when(synchronousMaestro.executeTestSuite(any())).thenThrow(new WebApplicationException("timeout"));

        Response response = controller.executeTestSuite(testSetExecuteTestSuiteDto(), null);

        assertEquals(Response.Status.GATEWAY_TIMEOUT.getStatusCode(), response.getStatus());
        assertEquals(SynchronousMaestro.TIMEOUT_MESSAGE, response.getEntity());
    }

    @Test
    void executeTestSuiteWithoutCallbackNotProvidedStepReturnsBadRequest() throws Exception {
        when(synchronousMaestro.executeTestSuite(any())).thenThrow(new UnknownStepType("aStep"));

        Response response = controller.executeTestSuite(testSetExecuteTestSuiteDto(), null);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertThat(
              response.getEntity().toString(),
              containsString("Unknown step type: aStep")
        );
    }

    @Test
    void executeTestSuiteWithoutCallbackUnexpectedExceptionInterruptsThreadAndReturnsInternalServerError() throws Exception {
        when(synchronousMaestro.executeTestSuite(any())).thenThrow(new RuntimeException("boom"));

        Response response = controller.executeTestSuite(testSetExecuteTestSuiteDto(), null);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(Thread.currentThread().isInterrupted());
        assertTrue(response.getEntity().toString().contains("an error occurred"));
        Thread.interrupted();
    }

    @Test
    void runWithCallbackInvalidTestExecuteTestSuiteReturnsBadRequest() {
        doThrow(new InvalidTestRunException("invalid"))
                .when(maestroImpl).executeTestSuite(any(), any());

        Response response = controller.executeTestSuite(testSetExecuteTestSuiteDto(), "http://callback");

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("bad format"));
    }

    @Test
    void executeTestSuiteWithCallbackNotProvidedStepReturnsBadRequest() {
        doThrow(new UnknownStepType("missing"))
                .when(maestroImpl).executeTestSuite(any(), any());

        Response response = controller.executeTestSuite(testSetExecuteTestSuiteDto(), "http://callback");

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertThat(
              response.getEntity().toString(),
              containsString("Unknown step type: missing")
        );
    }

    @Test
    void executeTestSuiteWithCallbackWebApplicationExceptionReturnsGatewayTimeout() {
        doThrow(new WebApplicationException("timeout"))
                .when(maestroImpl).executeTestSuite(any(), any());

        Response response = controller.executeTestSuite(testSetExecuteTestSuiteDto(), "http://callback");

        assertEquals(Response.Status.GATEWAY_TIMEOUT.getStatusCode(), response.getStatus());
        assertEquals(SynchronousMaestro.TIMEOUT_MESSAGE, response.getEntity());
    }

    @Test
    void executeTestSuiteWithCallbackUnexpectedExceptionReturnsInternalServerError() {
        doThrow(new RuntimeException("boom"))
                .when(maestroImpl).executeTestSuite(any(), any());

        Response response = controller.executeTestSuite(testSetExecuteTestSuiteDto(), "http://callback");

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("an error occurred"));
    }

    @Test
    void handleJsonProcessingExceptionWithIllegalArgumentCauseReturnsMessage() {
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("bad input");
        Exception wrapper = new RuntimeException("wrapper", illegalArgumentException);
        Exception root = new Exception("root", wrapper);

        Response response = controller.handleJsonProcessingException(root);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("bad input", response.getEntity());
    }

    @Test
    void handleJsonProcessingExceptionWithUnsupportedPropertyTypeReturnsMessage() {
        UnsupportedPropertyTypeException unsupportedPropertyTypeException = new UnsupportedPropertyTypeException("unsupported");
        Exception wrapper = new RuntimeException("wrapper", unsupportedPropertyTypeException);
        Exception root = new Exception("root", wrapper);

        Response response = controller.handleJsonProcessingException(root);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("unsupported", response.getEntity());
    }

    @Test
    void handleJsonProcessingExceptionWithInvalidTypeIdReturnsOriginalMessage() {
        InvalidTypeIdException invalidTypeIdException = InvalidTypeIdException.from(null, "original message", TypeFactory.defaultInstance().constructType(Object.class), "type");

        Response response = controller.handleJsonProcessingException(invalidTypeIdException);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("original message", response.getEntity());
    }

    @Test
    void handleJsonProcessingExceptionWithUnknownExceptionReturnsGenericMessage() {
        Exception exception = new Exception("unknown");

        Response response = controller.handleJsonProcessingException(exception);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("Unable to parse TestSuite structure.", response.getEntity());
    }

    @Test
    void reportValidationStep() throws MissingArgumentException {
        TestRun testRun = new TestRun();

        net.ihe.gazelle.maestro.api.business.test.Test test = new net.ihe.gazelle.maestro.api.business.test.Test();
        test.setId("iti78-request");
        test.setName("ITI-78 Request");
        test.addStep(new ValidationStep(
                "iti78-request-validation",
                new Property<>(ValidationStep.VALIDATION_SERVICE, "matchbox"),
                new Property<>(ValidationStep.VALIDATION_PROFILE, "Parameters"),
                new ArrayList<>(List.of(new PropertyRef<>("content", "iti78-request", byte[].class))))
        );

        testRun.setTest(test);

        testRun
                .addInput(new Property<>("iti78-request", "eaozieuaozieuaozieauo".getBytes(StandardCharsets.UTF_8)))
                .addInput(new Property<>("matchbox", "https://gazelle.ihe.net/matchboxv3/gazelle/validation"))
        ;


        Maestro engine = getEngine();
        engine.run(testRun);

    }

    private Maestro getEngine() {
        return new TestRunner(new EventMaestroProducer(new TestRunMockEvent()), new SessionStoreImpl(), new InMemoryTestReportRecordService(), "999.0.0");
    }

    private TestSuiteRunDTO testSetExecuteTestSuiteDto() {
        TestSuiteRunDTO dto = new TestSuiteRunDTO();
        TestRun testRun = new TestRun().setTestSuiteId("test-set");
        dto.setTests(List.of(new TestRunDTO(testRun)));
        dto.setAccessControlList(new AccessControlListDTO());
        return dto;
    }
}
