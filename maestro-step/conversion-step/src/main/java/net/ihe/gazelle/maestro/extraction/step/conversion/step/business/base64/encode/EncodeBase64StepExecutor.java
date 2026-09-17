package net.ihe.gazelle.maestro.extraction.step.conversion.step.business.base64.encode;

import net.ihe.gazelle.errorhandling.business.UnexpectedErrorBuilder;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.spi.business.StepExecutor;
import net.ihe.gazelle.maestro.spi.business.StepRun;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.Base64.getEncoder;
import static net.ihe.gazelle.maestro.api.business.testreport.StepResult.DONE;
import static net.ihe.gazelle.maestro.api.business.testreport.StepResult.UNDEFINED;
import static net.ihe.gazelle.maestro.extraction.step.conversion.step.business.base64.encode.EncodeBase64StepDefinition.CONTENT_TO_ENCODE;
import static net.ihe.gazelle.maestro.extraction.step.conversion.step.business.base64.encode.EncodeBase64StepDefinition.ENCODED_CONTENT;

public class EncodeBase64StepExecutor implements StepExecutor {

    public EncodeBase64StepExecutor() {
        // Default constructor
    }

    @Override
    public StepRunReport execute(StepRun stepRun) {
        StepRunReportBuilder reportBuilder = new StepRunReportBuilder()
                .setStepName(stepRun.getName())
                .setType(stepRun.getType());

        String contentToEncode = stepRun.getPropertyValue(CONTENT_TO_ENCODE);
        if (contentToEncode == null || contentToEncode.isEmpty()) {
            String failureMessage = "Content to encode is empty.";
            return buildStepRunReportFailed(failureMessage, reportBuilder);
        }

        try {
            String encodedContent = getEncoder().encodeToString(contentToEncode.getBytes());
            Property output = new StringProperty(ENCODED_CONTENT, encodedContent);
            return reportBuilder.setResult(DONE)
                    .setOutputs(List.of(output))
                    .build();
        } catch (Exception e) {
            String failureMessage = "Error during Base64 encoding: " + e.getMessage();
            return buildStepRunReportFailed(failureMessage, reportBuilder);
        }
    }

    private static StepRunReport buildStepRunReportFailed(String failureMessage, StepRunReportBuilder reportBuilder) {
        UnexpectedErrorBuilder unexpectedError =
                new UnexpectedErrorBuilder().setName("Base64 encoding error").setMessage(failureMessage);
        return reportBuilder.setResult(UNDEFINED).setUnexpectedErrors(List.of(unexpectedError)).build();
    }
}
