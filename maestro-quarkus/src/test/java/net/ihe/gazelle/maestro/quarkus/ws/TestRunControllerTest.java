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
import net.ihe.gazelle.maestro.api.business.MaestroObserver;
import net.ihe.gazelle.maestro.api.business.SynchronousMaestro;
import net.ihe.gazelle.maestro.api.business.UnsupportedPropertyTypeException;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;
import net.ihe.gazelle.maestro.api.technical.dto.report.TestReportSummaryDTO;
import net.ihe.gazelle.maestro.api.technical.dto.test.TestDTO;
import net.ihe.gazelle.maestro.api.technical.dto.testrun.TestSuiteRunDTO;
import net.ihe.gazelle.maestro.engine.business.MaestroRecorderImpl;
import net.ihe.gazelle.maestro.engine.business.UnknownStepException;
import net.ihe.gazelle.security.technical.acl.AccessControlListDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestRunControllerTest {

    @Mock
    SynchronousMaestro synchronousMaestro;

    @Mock
    MaestroRecorderImpl maestroImpl;

    TestRunControllerImpl controller;

    @BeforeEach
    void setUp() {
        controller = new TestRunControllerImpl(synchronousMaestro, maestroImpl);
    }

    @Test
    void executeTestSuiteWithoutCallbackReturnsOkResponseWithSummaryLocation() throws Exception {
        TestReport testReport = TestReportUtils.createTestReport();
        String id = testReport.getUuid();
        when(synchronousMaestro.executeTestSuite(any(), anyBoolean())).thenReturn(testReport);

        try (Response response = controller.executeTestSuite(testSetExecuteTestSuiteDto(), false, null)) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            TestReportSummaryDTO entity = (TestReportSummaryDTO) response.getEntity();
            assertNotNull(entity);
            assertEquals("http://datahouse/items/"+id, entity.getLocation());

            ArgumentCaptor<TestSuiteRun> testSetRunCaptor = ArgumentCaptor.forClass(TestSuiteRun.class);
            verify(synchronousMaestro).executeTestSuite(testSetRunCaptor.capture(), eq(false));
            assertEquals(1, testSetRunCaptor.getValue().getTests().size());
            verifyNoInteractions(maestroImpl);
        }
    }

    @Test
    void executeTestSuiteWithCallbackTriggersAsyncExecutionAndReturnsAccepted() {
        try (Response response = controller.executeTestSuite(testSetExecuteTestSuiteDto(), false, "http://callback")) {
            assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());

            ArgumentCaptor<TestSuiteRun> testSetRunCaptor = ArgumentCaptor.forClass(TestSuiteRun.class);
            ArgumentCaptor<MaestroObserver> callerCaptor = ArgumentCaptor.forClass(MaestroObserver.class);
            verify(maestroImpl).executeTestSuite(testSetRunCaptor.capture(), false, callerCaptor.capture());
            assertEquals(1, testSetRunCaptor.getValue().getTests().size());
            verifyNoInteractions(synchronousMaestro);
        }
    }

    @Test
    void runWithoutCallbackInvalidTestExecuteTestSuiteReturnsBadRequest() throws Exception {
        when(synchronousMaestro.executeTestSuite(any(), anyBoolean())).thenThrow(new InvalidTestRunException("invalid"));

        try (Response response = controller.executeTestSuite(testSetExecuteTestSuiteDto(), false, null)) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
            assertTrue(response.getEntity().toString().contains("bad format"));
        }
    }

    @Test
    void executeTestSuiteWithoutCallbackTimeoutReturnsGatewayTimeout() throws Exception {
        when(synchronousMaestro.executeTestSuite(any(), anyBoolean())).thenThrow(new TimeoutException("too long"));

        try (Response response = controller.executeTestSuite(testSetExecuteTestSuiteDto(), false, null)) {
            assertEquals(Response.Status.GATEWAY_TIMEOUT.getStatusCode(), response.getStatus());
            assertEquals(SynchronousMaestro.TIMEOUT_MESSAGE, response.getEntity());
        }
    }

    @Test
    void executeTestSuiteWithoutCallbackWebApplicationExceptionReturnsGatewayTimeout() throws Exception {
        when(synchronousMaestro.executeTestSuite(any(), anyBoolean())).thenThrow(new WebApplicationException("timeout"));

        try (Response response = controller.executeTestSuite(testSetExecuteTestSuiteDto(), false, null)) {
            assertEquals(Response.Status.GATEWAY_TIMEOUT.getStatusCode(), response.getStatus());
            assertEquals(SynchronousMaestro.TIMEOUT_MESSAGE, response.getEntity());
        }
    }

    @Test
    void executeTestSuiteWithoutCallbackNotProvidedStepReturnsBadRequest() throws Exception {
        when(synchronousMaestro.executeTestSuite(any(), anyBoolean())).thenThrow(new UnknownStepException("aStep"));

        try (Response response = controller.executeTestSuite(testSetExecuteTestSuiteDto(), false, null)) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
            assertThat(
                    response.getEntity().toString(),
                    containsString("Unknown step type: aStep")
            );
        }
    }

    @Test
    void executeTestSuiteWithoutCallbackUnexpectedExceptionInterruptsThreadAndReturnsInternalServerError() throws Exception {
        when(synchronousMaestro.executeTestSuite(any(), anyBoolean())).thenThrow(new RuntimeException("boom"));

        try (Response response = controller.executeTestSuite(testSetExecuteTestSuiteDto(), false, null)) {
            assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
            assertTrue(Thread.currentThread().isInterrupted());
            assertTrue(response.getEntity().toString().contains("an error occurred"));
        }
    }

    @Test
    void runWithCallbackInvalidTestExecuteTestSuiteReturnsBadRequest() {
        doThrow(new InvalidTestRunException("invalid"))
                .when(maestroImpl).executeTestSuite(any(), anyBoolean(), any());

        try (Response response = controller.executeTestSuite(testSetExecuteTestSuiteDto(), false, "http://callback")) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
            assertTrue(response.getEntity().toString().contains("bad format"));
        }
    }

    @Test
    void executeTestSuiteWithCallbackNotProvidedStepReturnsBadRequest() {
        doThrow(new UnknownStepException("missing"))
                .when(maestroImpl).executeTestSuite(any(), anyBoolean(), any());

        try (Response response = controller.executeTestSuite(testSetExecuteTestSuiteDto(), false, "http://callback")) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
            assertThat(
                    response.getEntity().toString(),
                    containsString("Unknown step type: missing")
            );
        }
    }

    @Test
    void executeTestSuiteWithCallbackWebApplicationExceptionReturnsGatewayTimeout() {
        doThrow(new WebApplicationException("timeout"))
                .when(maestroImpl).executeTestSuite(any(), anyBoolean(), any());

        try (Response response = controller.executeTestSuite(testSetExecuteTestSuiteDto(), false, "http://callback")) {
            assertEquals(Response.Status.GATEWAY_TIMEOUT.getStatusCode(), response.getStatus());
            assertEquals(SynchronousMaestro.TIMEOUT_MESSAGE, response.getEntity());
        }
    }

    @Test
    void executeTestSuiteWithCallbackUnexpectedExceptionReturnsInternalServerError() {
        doThrow(new RuntimeException("boom"))
                .when(maestroImpl).executeTestSuite(any(), anyBoolean(), any());

        try (Response response = controller.executeTestSuite(testSetExecuteTestSuiteDto(), false, "http://callback")) {
            assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
            assertTrue(response.getEntity().toString().contains("an error occurred"));
        }
    }

    @Test
    void handleJsonProcessingExceptionWithIllegalArgumentCauseReturnsMessage() {
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("bad input");
        Exception wrapper = new RuntimeException("wrapper", illegalArgumentException);
        Exception root = new Exception("root", wrapper);

        try (Response response = controller.handleJsonProcessingException(root)) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
            assertEquals("bad input", response.getEntity());
        }
    }

    @Test
    void handleJsonProcessingExceptionWithUnsupportedPropertyTypeReturnsMessage() {
        UnsupportedPropertyTypeException unsupportedPropertyTypeException = new UnsupportedPropertyTypeException("unsupported");
        Exception wrapper = new RuntimeException("wrapper", unsupportedPropertyTypeException);
        Exception root = new Exception("root", wrapper);

        try (Response response = controller.handleJsonProcessingException(root)) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
            assertEquals("unsupported", response.getEntity());
        }
    }

    @Test
    void handleJsonProcessingExceptionWithInvalidTypeIdReturnsOriginalMessage() {
        InvalidTypeIdException invalidTypeIdException = InvalidTypeIdException.from(null, "original message", TypeFactory.defaultInstance().constructType(Object.class), "type");

        try (Response response = controller.handleJsonProcessingException(invalidTypeIdException)) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
            assertEquals("original message", response.getEntity());
        }
    }

    @Test
    void handleJsonProcessingExceptionWithUnknownExceptionReturnsGenericMessage() {
        Exception exception = new Exception("unknown");

        try (Response response = controller.handleJsonProcessingException(exception)) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
            assertEquals("Unable to parse TestSuite structure.", response.getEntity());
        }
    }

    private TestSuiteRunDTO testSetExecuteTestSuiteDto() {
        TestSuiteRunDTO dto = new TestSuiteRunDTO();
        dto.setTests(List.of(new TestDTO(new net.ihe.gazelle.maestro.api.business.test.Test())));
        dto.setAccessControlList(new AccessControlListDTO());
        return dto;
    }
}
