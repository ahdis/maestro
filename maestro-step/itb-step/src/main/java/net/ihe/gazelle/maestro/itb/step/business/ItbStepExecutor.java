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

package net.ihe.gazelle.maestro.itb.step.business;

import net.ihe.gazelle.itb.gateway.business.reporting.ItbResult;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.test.SupportedInput;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.itb.gateway.business.ItbSessionStore;
import net.ihe.gazelle.itb.gateway.business.RunItbTestService;
import net.ihe.gazelle.itb.gateway.business.reporting.ItbReporting;
import net.ihe.gazelle.itb.gateway.business.request.Parameter;
import net.ihe.gazelle.itb.gateway.business.request.TestCase;
import net.ihe.gazelle.itb.gateway.business.request.TestRequest;
import net.ihe.gazelle.maestro.spi.business.StepExecutor;
import net.ihe.gazelle.maestro.spi.business.StepRun;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ItbStepExecutor implements StepExecutor {

   private static final Map<String, SupportedInput> SUPPORTED_INPUTS = new ItbStepDefinition().getSupportedInputs()
         .stream().collect(
               Collectors.toMap(SupportedInput::getId, input -> input)
         );

   private final RunItbTestService itbService;

   public ItbStepExecutor(ItbHandler itbHandler, ItbSessionStore sessionStore) {
      itbService = new RunItbTestService(itbHandler, sessionStore);
   }

   @Override
   public CompletableFuture<StepRunReport> execute(StepRun stepRun) {
      TestRequest testRequest = mapToRequest(stepRun);
      StepRunReportBuilder reportBuilder = new StepRunReportBuilder()
            .setStepName(stepRun.getName())
            .setType(ItbStepDefinition.TYPE);
      return itbService.runTests(testRequest).thenApply(
            itbReporting -> mapToStepRunReport(reportBuilder, itbReporting)
      );
   }

   private TestRequest mapToRequest(StepRun stepRun) {
      TestRequest testRequest = new TestRequest();
      testRequest.setTestID(stepRun.getPropertyValue(ItbStepDefinition.ACTOR_ID));
      TestCase testCase = new TestCase();
      testCase.setTestCase(stepRun.getPropertyValue(ItbStepDefinition.TEST_CASE));

      List<Parameter> parameters = new ArrayList<>();

      for (Property property : stepRun.getProperties()) {
         if (!SUPPORTED_INPUTS.containsKey(property.getName())) {
            parameters.add(mapToParameter(property));
         }
      }
      testCase.setParameters(parameters);
      testRequest.setParameters(List.of());
      testRequest.setTestCases(List.of(testCase));
      return testRequest;
   }


   private static Parameter mapToParameter(Property property) {
      if (property instanceof ByteArrayProperty baProp) {
         return new Parameter(baProp.getName(), "file", Base64.getEncoder().encodeToString(baProp.getValue()));
      } else if (property instanceof StringProperty strProp) {
         return new Parameter(strProp.getName(), "text", strProp.getValue());
      } else {
         return new Parameter(property.getName(), "text", String.valueOf(property.getValue()));
      }
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


