package net.ihe.gazelle.maestro.xml.extraction.step.business.xml;

import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.spi.business.StepExecutor;
import net.ihe.gazelle.maestro.spi.business.StepRun;
import net.ihe.gazelle.maestro.xml.extraction.step.technical.xml.XmlExtractionException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static net.ihe.gazelle.maestro.api.business.testreport.StepResult.DONE;
import static net.ihe.gazelle.maestro.api.business.testreport.StepResult.UNDEFINED;
import static net.ihe.gazelle.maestro.xml.extraction.step.business.xml.XmlExtractionStepDefinition.*;

public class XmlExtractionStepExecutor implements StepExecutor {

    private final XmlExtractionService xmlExtractionService;

    public XmlExtractionStepExecutor(XmlExtractionService xmlExtractionService) {
        this.xmlExtractionService = xmlExtractionService;
    }

    @Override
    public CompletableFuture<StepRunReport> execute(StepRun stepRun) {
        StepRunReportBuilder reportBuilder = new StepRunReportBuilder()
                .setStepId(stepRun.getId())
                .setStepName(stepRun.getName())
                .setType(stepRun.getType());

        String xmlPath = stepRun.getPropertyValue(XML_PATH);
        byte[] xmlFile = stepRun.getPropertyValue(XML_FILE);

        Map<String,String> namespaces = new HashMap<>();

        if (stepRun.hasProperty(DEFAULT_NAMESPACE)) {
            String defaultNameSpace = stepRun.getPropertyValue(DEFAULT_NAMESPACE);
            namespaces.put("", defaultNameSpace.split("=")[1]);
        }
        if (stepRun.hasProperty(NAMESPACES)) {
            String nameSpacesRaw = stepRun.getPropertyValue(NAMESPACES);
            for (String ns : nameSpacesRaw.split(",")) {
                String[] parts = ns.split("=");
                if (parts.length == 2) {
                    namespaces.put(parts[0], parts[1]);
                } else {
                    String failureMessage = String.format("Invalid namespace format: '%s'. Expected format is 'prefix=URI'.", ns);
                    return buildStepRunReportFailed(failureMessage, reportBuilder);
                }
            }
        }

        try {
            String extractedValue = xmlExtractionService.extractValue(xmlFile, xmlPath, namespaces);
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
