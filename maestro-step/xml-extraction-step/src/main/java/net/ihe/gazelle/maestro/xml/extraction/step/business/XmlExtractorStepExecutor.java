package net.ihe.gazelle.maestro.xml.extraction.step.business;

import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.spi.business.StepExecutor;
import net.ihe.gazelle.maestro.spi.business.StepRun;
import net.ihe.gazelle.maestro.xml.extraction.step.technical.XmlExtractionException;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.ihe.gazelle.maestro.api.business.testreport.StepResult.DONE;
import static net.ihe.gazelle.maestro.api.business.testreport.StepResult.UNDEFINED;
import static net.ihe.gazelle.maestro.xml.extraction.step.business.XmlExtractorStepDefinition.*;

public class XmlExtractorStepExecutor implements StepExecutor {

    private final XmlExtractionService xmlExtractorService;

    public XmlExtractorStepExecutor(XmlExtractionService xmlExtractorService) {
        this.xmlExtractorService = xmlExtractorService;
    }

    @Override
    public CompletableFuture<StepRunReport> execute(StepRun stepRun) {
        StepRunReportBuilder reportBuilder = new StepRunReportBuilder()
                .setStepId(stepRun.getId())
                .setStepName(stepRun.getName())
                .setType(stepRun.getType());

        String xmlPath = stepRun.getPropertyValue(XML_PATH);
        String xmlFile = stepRun.getPropertyValue(SOURCE_XML);

        try {
            String extractedValue = xmlExtractorService.extractValue(xmlFile, xmlPath);
            Property output = new StringProperty(EXTRACTED_VALUE, extractedValue);
            return CompletableFuture.completedFuture(reportBuilder.setResult(DONE)
                    .setOutputs(List.of(output))
                    .build());
        } catch (XmlExtractionException exception) {
            String failureMessage = String.format("XML extraction error for xmlPath: '%s'. Error: %s", xmlPath, exception.getMessage());
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
