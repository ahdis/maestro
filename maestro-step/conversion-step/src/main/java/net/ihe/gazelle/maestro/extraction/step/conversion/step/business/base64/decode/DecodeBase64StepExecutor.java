package net.ihe.gazelle.maestro.extraction.step.conversion.step.business.base64.decode;

import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.spi.business.StepExecutor;
import net.ihe.gazelle.maestro.spi.business.StepRun;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.ihe.gazelle.maestro.api.business.testreport.StepResult.DONE;
import static net.ihe.gazelle.maestro.api.business.testreport.StepResult.UNDEFINED;
import static net.ihe.gazelle.maestro.extraction.step.conversion.step.business.base64.decode.DecodeBase64StepDefinition.CONTENT_TO_DECODE;
import static net.ihe.gazelle.maestro.extraction.step.conversion.step.business.base64.decode.DecodeBase64StepDefinition.DECODED_CONTENT;
import static net.ihe.gazelle.maestro.extraction.step.conversion.step.business.base64.encode.EncodeBase64StepDefinition.FAILURE_MESSAGE;

public class DecodeBase64StepExecutor implements StepExecutor {


    public DecodeBase64StepExecutor() {
    }

    @Override
    public CompletableFuture<StepRunReport> execute(StepRun stepRun) {
        StepRunReportBuilder reportBuilder = new StepRunReportBuilder()
                .setStepId(stepRun.getId())
                .setStepName(stepRun.getName())
                .setType(stepRun.getType());

        String contentToEncode = stepRun.getPropertyValue(CONTENT_TO_DECODE);
        if (contentToEncode == null || contentToEncode.isEmpty()) {
            String failureMessage = "Content to decode is empty.";
            return buildStepRunReportFailed(failureMessage, reportBuilder);
        }

        try {
            byte[] decodedBytes = java.util.Base64.getDecoder().decode(contentToEncode);
            Property output = new ByteArrayProperty(DECODED_CONTENT, decodedBytes);
            return CompletableFuture.completedFuture(reportBuilder.setResult(DONE)
                    .setOutputs(List.of(output))
                    .build());
        } catch (IllegalArgumentException e) {
            String failureMessage = "Failed to decode Base64 content: " + e.getMessage();
            return buildStepRunReportFailed(failureMessage, reportBuilder);
        }
    }

    private static CompletableFuture<StepRunReport> buildStepRunReportFailed(String failureMessage, StepRunReportBuilder reportBuilder) {
        StringProperty failureOutput = new StringProperty(FAILURE_MESSAGE, failureMessage);
        return CompletableFuture.completedFuture(
                reportBuilder.setOutputs(List.of(failureOutput)).setResult(UNDEFINED).build()
        );
    }
}
