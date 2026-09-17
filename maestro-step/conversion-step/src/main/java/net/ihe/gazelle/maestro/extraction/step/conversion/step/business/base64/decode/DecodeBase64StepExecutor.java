package net.ihe.gazelle.maestro.extraction.step.conversion.step.business.base64.decode;

import net.ihe.gazelle.errorhandling.business.UnexpectedErrorBuilder;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.Property;
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

public class DecodeBase64StepExecutor implements StepExecutor {

    public DecodeBase64StepExecutor() {
        // Default constructor
    }

    @Override
    public StepRunReport execute(StepRun stepRun) {
        StepRunReportBuilder reportBuilder = new StepRunReportBuilder()
                .setStepName(stepRun.getName())
                .setType(stepRun.getType());

        String contentToEncode = stepRun.getPropertyValue(CONTENT_TO_DECODE);
        if (contentToEncode == null || contentToEncode.isEmpty()) {
            String failureMessage = "Content to decode is null or empty";
            return buildStepRunReportFailed(failureMessage, reportBuilder);
        }

        try {
            byte[] decodedBytes = java.util.Base64.getDecoder().decode(contentToEncode);
            Property output = new ByteArrayProperty(DECODED_CONTENT, decodedBytes);
            return reportBuilder.setResult(DONE)
                    .setOutputs(List.of(output))
                    .build();
        } catch (IllegalArgumentException e) {
            String failureMessage = "Unable to decode base64 content : " + e.getMessage();
            return buildStepRunReportFailed(failureMessage, reportBuilder);
        }
    }

    private static StepRunReport buildStepRunReportFailed(String failureMessage, StepRunReportBuilder reportBuilder) {
        UnexpectedErrorBuilder unexpectedError =
                new UnexpectedErrorBuilder().setName("Base64 decoding error").setMessage(failureMessage);
        return reportBuilder.setResult(UNDEFINED).setUnexpectedErrors(List.of(unexpectedError)).build();
    }
}
