package net.ihe.gazelle.maestro.xml.extraction.step.business;

import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.spi.business.StepExecutor;
import net.ihe.gazelle.maestro.spi.business.StepRun;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.ihe.gazelle.maestro.api.business.testreport.StepResult.FAILED;
import static net.ihe.gazelle.maestro.api.business.testreport.StepResult.PASSED;
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
      String sourceXml = stepRun.getPropertyValue(SOURCE_XML);

      if(xmlPath != null && sourceXml != null) {
          String extractedValue = xmlExtractorService.extractValue(sourceXml, xmlPath);
          Property output = new StringProperty(EXTRACTED_VALUE, extractedValue);
         return CompletableFuture.completedFuture(reportBuilder.setResult(PASSED).setOutputs(List.of(output)).build());
      } else {
         StringProperty failureOutput = new StringProperty(FAILURE_MESSAGE,
               String.format("One of the input is null. xmlPath: '%s', sourceXml: '%s'", xmlPath, sourceXml)
         );
         return CompletableFuture.completedFuture(
               reportBuilder.setOutputs(List.of(failureOutput)).setResult(FAILED).build()
         );
      }
   }
}
