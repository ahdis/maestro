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

package net.ihe.gazelle.maestro.simulation.step.business;

import net.ihe.gazelle.errorhandling.business.UnexpectedErrorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ObjectResult;
import net.ihe.gazelle.lang.ExecutionRuntimeException;
import net.ihe.gazelle.lang.TimeoutRuntimeException;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.userinteract.UserInteractionHandler;
import net.ihe.gazelle.maestro.spi.business.StepExecutor;
import net.ihe.gazelle.maestro.spi.business.StepRun;
import net.ihe.gazelle.simulation.business.callback.Result;
import net.ihe.gazelle.simulation.business.callback.SimulationReport;
import net.ihe.gazelle.simulation.business.callback.SimulationReportValidator;
import net.ihe.gazelle.simulation.business.sequence.ServiceUnavailableException;
import net.ihe.gazelle.simulation.business.sequence.SupportedParameter;
import net.ihe.gazelle.simulation.business.setup.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SimulationStepExecutor implements StepExecutor {

   private static final Logger LOG = LoggerFactory.getLogger(SimulationStepExecutor.class);
   public static final int DEFAULT_TIMEOUT = 60;

   private final SimulationHandler simulationHandler;
   private final UserInteractionHandler userInteractionHandler;
   private final ReportSerializer serializer;
   private final SimulationReportValidator reportValidator = new SimulationReportValidator();

   public SimulationStepExecutor(SimulationHandler simulationHandler,
                                 UserInteractionHandler userInteractionHandler,
                                 ReportSerializer serializer) {
      this.simulationHandler = simulationHandler;
      this.userInteractionHandler = userInteractionHandler;
      this.serializer = serializer;
   }

   @Override
   public CompletableFuture<StepRunReport> execute(StepRun stepRun) {
      try {
         String simulationService = stepRun.getPropertyValue(SimulationStepDefinition.SIMULATION_SERVICE);
         String sequenceId = stepRun.getPropertyValue(SimulationStepDefinition.SEQUENCE_ID);

         SimulationRequest simulationRequest = createSimulationRequest(
               sequenceId,
               getTimeoutSeconds(stepRun),
               stepRun,
               getSupportedParameters(simulationService, sequenceId)
         );

         CompletableFuture<SimulationReport> futureReport = new CompletableFuture<>();
         simulationHandler.simulate(simulationRequest, this::showInstructions, futureReport::complete);
         SimulationReport simulationReport = waitSimulationReport(simulationRequest, futureReport);

         return CompletableFuture.completedFuture(buildStepRunReport(stepRun, simulationReport));
      } catch (MissingRequiredParameterException e) {
         return buildReportWithError(stepRun, e, "Missing Required Parameter");
      } catch (InvalidParameterValueException e) {
         return buildReportWithError(stepRun, e, "Invalid Parameter Value");
      } catch (UnknownSequenceException e) {
         return buildReportWithError(stepRun, e, "Unknown Simulation Sequence");
      } catch (UnknownSimulationException e) {
         return buildReportWithError(stepRun, e, "Unknown Simulation session");
      } catch (AlreadyRunningException e) {
         return buildReportWithError(stepRun, e, "Simulation already running");
      } catch (ServiceUnavailableException e) {
         return buildReportWithError(stepRun, e, "Service Unavailable");
      } catch (ClassCastException e) {
         return buildReportWithError(stepRun, e, "Error on property type");
      } catch (RuntimeException e) {
         return buildReportWithError(stepRun, e, "Error from simulator");
      }
   }

   private static int getTimeoutSeconds(StepRun stepRun) {
      return stepRun.hasProperty(SimulationStepDefinition.TIMEOUT_SECONDS) ?
            stepRun.getPropertyValue(SimulationStepDefinition.TIMEOUT_SECONDS) :
            DEFAULT_TIMEOUT;
   }

   private List<SupportedParameter> getSupportedParameters(String simulationService, String sequenceId) {
      return simulationHandler.getSimulationSequences().stream()
            .filter(seq -> sequenceId.equals(seq.getId()))
            .findFirst().orElseThrow(() ->
                  new UnknownSequenceException(
                        "Unknown Simulation Sequence with id: " + sequenceId + " for service: " + simulationService))
            .getSupportedParameters();
   }

   public static SimulationRequest createSimulationRequest(String sequenceId, int timeoutSeconds, StepRun stepRun,
                                                           List<SupportedParameter> supportedParameters) {
      SimulationRequestBuilder simulationRequest = new SimulationRequestBuilder()
            .setSequenceId(sequenceId)
            .setTimeoutSeconds(timeoutSeconds);

      if (!supportedParameters.isEmpty()) {
         for (SupportedParameter supportedParameter : supportedParameters) {
            if (supportedParameter.isRequired()) {
               simulationRequest.addSimulationParameter(
                     toParameter(stepRun.getProperty(supportedParameter.getName()), supportedParameter)
               );
            } else {
               if (stepRun.hasProperty(supportedParameter.getName())) {
                  simulationRequest.addSimulationParameter(
                        toParameter(stepRun.getProperty(supportedParameter.getName()), supportedParameter)
                  );
               }
            }
         }
      }

      return simulationRequest.build();
   }

   private static ParameterBuilder toParameter(final Property property, SupportedParameter supportedParameter) {
      return new ParameterBuilder()
            .setName(supportedParameter.getName())
            .setType(supportedParameter.getType())
            .setValue(toParameterValue(
                  property, supportedParameter.getType()
            ));
   }

   private static String toParameterValue(Property property, ParameterType expectedType) {
      return switch (expectedType) {
         case TEXT -> (String) property.getValue();
         case BOOLEAN -> ((Boolean) property.getValue()).toString();
         case FILE -> Base64.getEncoder().encodeToString((byte[]) property.getValue());
      };
   }

   private void showInstructions(AdditionalInstructions additionalInstructions) {
      StringBuilder instructions = new StringBuilder()
            .append(additionalInstructions.getInstruction())
            .append("\n");
      for (Parameter parameter : additionalInstructions.getParameters()) {
         instructions
               .append("name : ").append(parameter.getName()).append("\n")
               .append("value : ").append(parameter.getValue()).append("\n");
      }
       userInteractionHandler.displayMessage("Additional instructions", instructions.toString());
   }

   private SimulationReport waitSimulationReport(SimulationRequest simulationRequest,
                                                 CompletableFuture<SimulationReport> futureReport) {
      long timeout = simulationRequest.getTimeoutSeconds() + 10L;
      try {
         return futureReport.get(timeout, TimeUnit.SECONDS);
      } catch (TimeoutException e) {
         LOG.error("Simulation timed out after {} seconds", timeout);
         throw new TimeoutRuntimeException("Simulation timed out after " + timeout + " seconds");
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         // FIXME returning a null report will cause NPE later.
         // After an interrupt, we should immediatly stop and leave the SimulationStepExecutor.
         return null;
      } catch (ExecutionException e) {
         throw new ExecutionRuntimeException(e);
      }
   }

   private StepRunReport buildStepRunReport(StepRun stepRun, SimulationReport simulationReport) {

      ObjectResult reportValidationResult = reportValidator.validate(simulationReport);
      return prepareStepReport(stepRun)
            .setResult(toStepResult(simulationReport.getResult()))
            .setOutputs(List.of(
                  new ByteArrayProperty(SimulationStepDefinition.REPORT, serializer.toByteArray(simulationReport))
                        .setFileName("simulation-report.json")
                        .setMimeType("application/json")
            ))
            .addUnexpectedErrors(
                  reportValidationResult.getAllInvalidRules().stream()
                        .map(ruleResult -> new UnexpectedErrorBuilder()
                              .setName("Invalid simulation report (" + ruleResult.getId() + ")")
                              .setMessage(ruleResult.getDescription())
                        ).toList()
            )
            .build();
   }

   private StepRunReportBuilder prepareStepReport(StepRun stepRun) {
      return new StepRunReportBuilder()
            .setStepName(stepRun.getName())
            .setType(SimulationStepDefinition.TYPE);
   }

   private CompletableFuture<StepRunReport> buildReportWithError(StepRun stepRun, Exception e, String name) {
      return CompletableFuture.completedFuture(prepareStepReport(stepRun)
            .addUnexpectedError(
                  new UnexpectedErrorBuilder()
                        .setName(name)
                        .setMessage(name)
                        .setCause(new UnexpectedErrorBuilder().fromThrowable(e))
            )
            .build());
   }

   private StepResult toStepResult(Result result) {
      return switch (result) {
         case PASSED -> StepResult.PASSED;
         case FAILED -> StepResult.FAILED;
         default -> StepResult.UNDEFINED;
      };
   }
}
