package net.ihe.gazelle.maestro.extraction.step.conversion.step.business.cert.pem;

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
import static net.ihe.gazelle.maestro.extraction.step.conversion.step.business.base64.encode.EncodeBase64StepDefinition.FAILURE_MESSAGE;
import static net.ihe.gazelle.maestro.extraction.step.conversion.step.business.cert.pem.ConvertCertificateDerToPemStepDefinition.DER_CERTIFICATE;
import static net.ihe.gazelle.maestro.extraction.step.conversion.step.business.cert.pem.ConvertCertificateDerToPemStepDefinition.PEM_CERTIFICATE;

public class ConvertCertificateDerToPemStepExecutor implements StepExecutor {

    private final PemConverterService pemConverterService;

    public ConvertCertificateDerToPemStepExecutor(PemConverterService pemConverterService) {
        this.pemConverterService = pemConverterService;
    }

    @Override
    public CompletableFuture<StepRunReport> execute(StepRun stepRun) {
        StepRunReportBuilder reportBuilder = new StepRunReportBuilder()
                .setStepName(stepRun.getName())
                .setType(stepRun.getType());
        byte[] derCertificateBytes = stepRun.getPropertyValue(DER_CERTIFICATE);
        if (derCertificateBytes == null || derCertificateBytes.length == 0) {
            String failureMessage = "DER certificate content is empty.";
            return buildStepRunReportFailed(failureMessage, reportBuilder);
        }
        try {
            String pemCertificate = pemConverterService.convertToPemFormat(derCertificateBytes);
            Property output = new ByteArrayProperty(PEM_CERTIFICATE, pemCertificate.getBytes());
            return CompletableFuture.completedFuture(reportBuilder.setResult(DONE)
                    .setOutputs(List.of(output))
                    .build());
        } catch (Exception e) {
            String failureMessage = "Error during DER to PEM conversion: " + e.getMessage();
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
