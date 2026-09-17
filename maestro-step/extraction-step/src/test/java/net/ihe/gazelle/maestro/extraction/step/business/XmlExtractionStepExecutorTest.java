package net.ihe.gazelle.maestro.extraction.step.business;

import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.spi.business.StepRun;
import net.ihe.gazelle.maestro.extraction.step.business.xml.XmlExtractionService;
import net.ihe.gazelle.maestro.extraction.step.business.xml.XmlExtractionStepExecutor;
import net.ihe.gazelle.maestro.extraction.step.technical.xml.XmlExtractionServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static net.ihe.gazelle.maestro.api.business.testreport.StepResult.UNDEFINED;
import static net.ihe.gazelle.maestro.extraction.step.business.xml.XmlExtractionStepDefinition.XML_FILE;
import static net.ihe.gazelle.maestro.extraction.step.business.xml.XmlExtractionStepDefinition.XML_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XmlExtractionStepExecutorTest {

    private final XmlExtractionService service = new XmlExtractionServiceImpl();
    private final XmlExtractionStepExecutor stepExecutor = new XmlExtractionStepExecutor(service);

    @Test
    void executeStepEmptyField() {
        Step step = buildStep("".getBytes(), "/root");
        StepRun stepRun = new StepRun(step, List.of());

        StepRunReport stepRunReport = stepExecutor.execute(stepRun);

        assertEquals(UNDEFINED, stepRunReport.getResult());
        assertEquals(0, stepRunReport.getOutputs().size());
        assertEquals(1, stepRunReport.getUnexpectedErrors().size());
        assertTrue(stepRunReport.getUnexpectedErrors().stream().anyMatch(unexpectedError ->
                unexpectedError.getName().equals("XML_EXTRACTION_ERROR")
        ));
    }

    private Step buildStep(byte[] xmlFile, String xmlPath) {
        return new Step()
                .setName("extractionXmlStep")
                .setType("EXTRACT_XML")
                .setProperties(List.of(
                        new ByteArrayProperty(XML_FILE, xmlFile),
                        new StringProperty(XML_PATH, xmlPath)
                ));
    }
}
