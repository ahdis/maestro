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
import net.ihe.gazelle.maestro.api.business.testreport.Result;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportSummary;
import net.ihe.gazelle.maestro.api.business.testrun.InvalidTestRunException;
import net.ihe.gazelle.maestro.api.business.testrun.Maestro;
import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.PropertyRef;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.UnsupportedPropertyTypeException;
import net.ihe.gazelle.maestro.api.technical.dto.report.TestReportSummaryDTO;
import net.ihe.gazelle.maestro.api.technical.dto.testrun.TestRunDTO;
import net.ihe.gazelle.maestro.api.technical.dto.testrun.TestSetRunDTO;
import net.ihe.gazelle.maestro.business.MaestroImpl;
import net.ihe.gazelle.maestro.business.MissingArgumentException;
import net.ihe.gazelle.maestro.quarkus.NotProvidedStep;
import net.ihe.gazelle.maestro.business.SessionStoreContextImpl;
import net.ihe.gazelle.maestro.quarkus.factory.TestRunMockEvent;
import net.ihe.gazelle.maestro.quarkus.jobs.EventMaestroProducer;
import net.ihe.gazelle.maestro.quarkus.jobs.SynchronousMaestro;
import net.ihe.gazelle.maestro.quarkus.mock.InMemoryTestReportRecordService;
import net.ihe.gazelle.maestro.quarkus.mock.StepMockEvent;
import net.ihe.gazelle.maestro.quarkus.ws.async.AsynchronousMaestro;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStep;
import net.ihe.gazelle.security.business.acl.AccessControlList;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestRunControllerTest {

    @Mock
    SynchronousMaestro synchronousMaestro;

    @Mock
    AsynchronousMaestro asynchronousMaestro;

    TestRunControllerImpl controller;

    @BeforeEach
    void setUp() {
        controller = new TestRunControllerImpl(synchronousMaestro, asynchronousMaestro);
        controller.dataHouseUrl = "http://datahouse";
    }

    @Test
    void runWithoutCallbackReturnsOkResponseWithSummaryLocation() throws Exception {
        TestReportSummary summary = new TestReportSummary()
                .setId("summary-id")
                .setResult(Result.PASSED)
                .setDateTime(new Date());
        when(synchronousMaestro.executeTestSet(any())).thenReturn(summary);

        Response response = controller.run(null, testSetRunDto());

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        TestReportSummaryDTO entity = (TestReportSummaryDTO) response.getEntity();
        assertNotNull(entity);
        assertEquals("http://datahouse/items/summary-id", entity.getLocation());

        ArgumentCaptor<net.ihe.gazelle.maestro.api.business.testrun.TestSetRun> testSetRunCaptor = ArgumentCaptor.forClass(net.ihe.gazelle.maestro.api.business.testrun.TestSetRun.class);
        verify(synchronousMaestro).executeTestSet(testSetRunCaptor.capture());
        assertEquals(1, testSetRunCaptor.getValue().getTestRuns().size());
        verifyNoInteractions(asynchronousMaestro);
    }

    @Test
    void runWithCallbackTriggersAsyncExecutionAndReturnsAccepted() {
        Response response = controller.run("http://callback", testSetRunDto());

        assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());

        ArgumentCaptor<net.ihe.gazelle.maestro.api.business.testrun.TestSetRun> testSetRunCaptor = ArgumentCaptor.forClass(net.ihe.gazelle.maestro.api.business.testrun.TestSetRun.class);
        verify(asynchronousMaestro).executeTestSet(eq("http://callback"), testSetRunCaptor.capture());
        assertEquals(1, testSetRunCaptor.getValue().getTestRuns().size());
        verifyNoInteractions(synchronousMaestro);
    }

    @Test
    void runWithoutCallbackInvalidTestRunReturnsBadRequest() throws Exception {
        when(synchronousMaestro.executeTestSet(any())).thenThrow(new InvalidTestRunException("invalid"));

        Response response = controller.run(null, testSetRunDto());

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("bad format"));
    }

    @Test
    void runWithoutCallbackTimeoutReturnsGatewayTimeout() throws Exception {
        when(synchronousMaestro.executeTestSet(any())).thenThrow(new TimeoutException("too long"));

        Response response = controller.run(null, testSetRunDto());

        assertEquals(Response.Status.GATEWAY_TIMEOUT.getStatusCode(), response.getStatus());
        assertEquals(SynchronousMaestro.TIMEOUT_MESSAGE, response.getEntity());
    }

    @Test
    void runWithoutCallbackWebApplicationExceptionReturnsGatewayTimeout() throws Exception {
        when(synchronousMaestro.executeTestSet(any())).thenThrow(new WebApplicationException("timeout"));

        Response response = controller.run(null, testSetRunDto());

        assertEquals(Response.Status.GATEWAY_TIMEOUT.getStatusCode(), response.getStatus());
        assertEquals(SynchronousMaestro.TIMEOUT_MESSAGE, response.getEntity());
    }

    @Test
    void runWithoutCallbackNotProvidedStepReturnsBadRequest() throws Exception {
        when(synchronousMaestro.executeTestSet(any())).thenThrow(new NotProvidedStep("missing step"));

        Response response = controller.run(null, testSetRunDto());

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("non provided"));
    }

    @Test
    void runWithoutCallbackUnexpectedExceptionInterruptsThreadAndReturnsInternalServerError() throws Exception {
        when(synchronousMaestro.executeTestSet(any())).thenThrow(new RuntimeException("boom"));

        Response response = controller.run(null, testSetRunDto());

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(Thread.currentThread().isInterrupted());
        assertTrue(response.getEntity().toString().contains("an error occurred"));
        Thread.interrupted();
    }

    @Test
    void runWithCallbackInvalidTestRunReturnsBadRequest() {
        doThrow(new InvalidTestRunException("invalid"))
                .when(asynchronousMaestro).executeTestSet(anyString(), any());

        Response response = controller.run("http://callback", testSetRunDto());

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("bad format"));
    }

    @Test
    void runWithCallbackNotProvidedStepReturnsBadRequest() {
        doThrow(new NotProvidedStep("missing"))
                .when(asynchronousMaestro).executeTestSet(anyString(), any());

        Response response = controller.run("http://callback", testSetRunDto());

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("non provided"));
    }

    @Test
    void runWithCallbackWebApplicationExceptionReturnsGatewayTimeout() {
        doThrow(new WebApplicationException("timeout"))
                .when(asynchronousMaestro).executeTestSet(anyString(), any());

        Response response = controller.run("http://callback", testSetRunDto());

        assertEquals(Response.Status.GATEWAY_TIMEOUT.getStatusCode(), response.getStatus());
        assertEquals(SynchronousMaestro.TIMEOUT_MESSAGE, response.getEntity());
    }

    @Test
    void runWithCallbackUnexpectedExceptionReturnsInternalServerError() {
        doThrow(new RuntimeException("boom"))
                .when(asynchronousMaestro).executeTestSet(anyString(), any());

        Response response = controller.run("http://callback", testSetRunDto());

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
        assertEquals("Unable to parse TestRun structure.", response.getEntity());
    }

    @Test
    void reportValidationStep() throws MissingArgumentException {
        TestRun testRun = new TestRun();

        net.ihe.gazelle.maestro.api.business.testrun.Test test = new net.ihe.gazelle.maestro.api.business.testrun.Test();
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
                .addInputs(new Property<>("iti78-request", "eaozieuaozieuaozieauo".getBytes(StandardCharsets.UTF_8)))
                .addInputs(new Property<>("matchbox", "https://gazelle.ihe.net/matchboxv3/gazelle/validation"))
        ;


        Maestro engine = getEngine();
        engine.run(testRun);

    }

    private Maestro getEngine() {
        return new MaestroImpl(new EventMaestroProducer(new TestRunMockEvent(), new StepMockEvent()), new SessionStoreContextImpl(), new InMemoryTestReportRecordService(), "999.0.0");
    }

    private TestSetRunDTO testSetRunDto() {
        TestSetRunDTO dto = new TestSetRunDTO();
        TestRun testRun = new TestRun().setTestSetId("test-set");
        dto.setTestRuns(List.of(new TestRunDTO(testRun)));
        dto.setAccessControlList(new AccessControlList());
        return dto;
    }
}
