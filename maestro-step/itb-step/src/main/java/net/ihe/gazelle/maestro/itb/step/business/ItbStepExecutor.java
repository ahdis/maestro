/*
 * Copyright 2025-2026 IHE International.
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

package net.ihe.gazelle.maestro.itb.step.business;

import net.ihe.gazelle.errorhandling.business.UnexpectedErrorBuilder;
import net.ihe.gazelle.itb.gateway.business.ItbSessionStore;
import net.ihe.gazelle.itb.gateway.business.RunItbTestService;
import net.ihe.gazelle.itb.gateway.business.reporting.ItbReporting;
import net.ihe.gazelle.itb.gateway.business.reporting.ItbResult;
import net.ihe.gazelle.itb.gateway.business.request.Parameter;
import net.ihe.gazelle.itb.gateway.business.request.TestCase;
import net.ihe.gazelle.itb.gateway.business.request.TestRequest;
import net.ihe.gazelle.lang.ExecutionRuntimeException;
import net.ihe.gazelle.lang.TimeoutRuntimeException;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.test.SupportedInput;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.spi.business.StepExecutor;
import net.ihe.gazelle.maestro.spi.business.StepRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * Implementation of the StepExecutor interface for handling ITB step executions.
 */
public class ItbStepExecutor implements StepExecutor {

   private static final Logger LOG = LoggerFactory.getLogger(ItbStepExecutor.class);

   private static final Map<String, SupportedInput> SUPPORTED_INPUTS = new ItbStepDefinition().getSupportedInputs()
         .stream().collect(
               Collectors.toMap(SupportedInput::getId, input -> input)
         );
   private static final String SYSTEM_ID = "systemID";

   private final RunItbTestService itbService;

   /**
    * Constructs an instance of the {@code ItbStepExecutor} class, which is responsible for executing
    * ITB test steps by using the provided ITB handler and session store.
    *
    * @param itbHandler the handler interface for managing ITB services and test sessions.
    * @param sessionStore the session store that handles the storage and retrieval of ITB reporting sessions.
    */
   public ItbStepExecutor(ItbHandler itbHandler, ItbSessionStore sessionStore) {
      itbService = new RunItbTestService(itbHandler, sessionStore);
   }

   @Override
   public StepRunReport execute(StepRun stepRun) {
      try {
         TestRequest testRequest = mapToRequest(stepRun);
         StepRunReportBuilder reportBuilder = new StepRunReportBuilder()
               .setStepName(stepRun.getName())
               .setType(ItbStepDefinition.TYPE);

         CompletableFuture<ItbReporting> futureReport = itbService.runTests(testRequest);

         return waitItbReport(stepRun, futureReport, reportBuilder);
      } catch (RuntimeException e) {
         LOG.error("Error while executing ITB step '{}'", stepRun.getName(), e);
         return buildReportWithError(stepRun, e);
      }
   }

   private StepRunReport waitItbReport(StepRun stepRun, CompletableFuture<ItbReporting> futureReport, StepRunReportBuilder reportBuilder) {
      long timeout = stepRun.getTimeout();
      try {
         return futureReport
               .thenApply(itbReporting -> mapToStepRunReport(reportBuilder, itbReporting))
               .get(stepRun.getTimeout(), TimeUnit.SECONDS);
      } catch (TimeoutException e) {
         LOG.error("ITB timed out after {} seconds", timeout);
         throw new TimeoutRuntimeException("ITB timed out after " + timeout + " seconds");
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         throw new ExecutionRuntimeException(e);
      } catch (ExecutionException e) {
         throw new ExecutionRuntimeException(e);
      }
   }

   private TestRequest mapToRequest(StepRun stepRun) {
      TestRequest testRequest = new TestRequest();
      testRequest.setTestID(stepRun.getPropertyValue(ItbStepDefinition.ACTOR_ID));
      TestCase testCase = new TestCase();
      testCase.setTestCase(stepRun.getPropertyValue(ItbStepDefinition.TEST_CASE));

      List<Parameter> requestParameters = new ArrayList<>();
      List<Parameter> testCaseParameters = new ArrayList<>();

      for (Property property : stepRun.getProperties()) {
         if (!SUPPORTED_INPUTS.containsKey(property.getName())) {
            Parameter parameter = mapToParameter(property);
            if (SYSTEM_ID.equals(property.getName())) {
               requestParameters.add(parameter);
            } else {
               testCaseParameters.add(parameter);
            }
         }
      }
      testCase.setParameters(testCaseParameters);
      testRequest.setParameters(requestParameters);
      testRequest.setTestCases(List.of(testCase));
      return testRequest;
   }

   private StepRunReportBuilder prepareStepReport(StepRun stepRun) {
      return new StepRunReportBuilder()
            .setStepName(stepRun.getName())
            .setType(ItbStepDefinition.TYPE);
   }

   private StepRunReport buildReportWithError(StepRun stepRun, Exception e) {
      return prepareStepReport(stepRun)
            .addUnexpectedError(new UnexpectedErrorBuilder().fromThrowable(e))
            .build();
   }

   private static Parameter mapToParameter(Property property) {
      return switch (property) {
         case ByteArrayProperty baProp ->
               new Parameter(baProp.getName(), "file", Base64.getEncoder().encodeToString(baProp.getValue()));
         case StringProperty strProp -> new Parameter(strProp.getName(), "text", strProp.getValue());
         default -> new Parameter(property.getName(), "text", String.valueOf(property.getValue()));
      };
   }


   private StepRunReport mapToStepRunReport(StepRunReportBuilder reportBuilder, ItbReporting itbReporting) {
      reportBuilder.setResult(asStepResult(itbReporting.getResult()));
      if (itbReporting.getTestReport() != null) {
         reportBuilder.addOutput(getItbXMLReport(itbReporting));
      }
      if (itbReporting.getPdfReport() != null) {
         reportBuilder.addOutput(getItbPdfReport(itbReporting));
      }
      if (itbReporting.getLogs() != null) {
         reportBuilder.addOutput(getLogs(itbReporting));
      }
      return reportBuilder.build();
   }

   private StepResult asStepResult(ItbResult itbResult) {
      return switch (itbResult) {
         case ItbResult.SUCCESS -> StepResult.PASSED;
         case ItbResult.FAILURE -> StepResult.FAILED;
         default -> StepResult.UNDEFINED;
      };
   }

   private Property getItbXMLReport(ItbReporting itbReporting) {
      return new ByteArrayProperty(ItbStepDefinition.XML_REPORT, itbReporting.getTestReport().getBytes())
            .setFileName("itb-report.xml")
            .setMimeType("application/xml");
   }

   private Property getItbPdfReport(ItbReporting itbReporting) {
      return new ByteArrayProperty(ItbStepDefinition.PDF_REPORT, itbReporting.getPdfReport())
            .setFileName("itb-report.pdf")
            .setMimeType("application/pdf");
   }

   private Property getLogs(ItbReporting itbReporting) {
      return new ByteArrayProperty(ItbStepDefinition.LOGS, itbReporting.getLogs().getBytes(StandardCharsets.UTF_8))
            .setFileName("itb-logs.txt")
            .setMimeType("text/plain");
   }

}
