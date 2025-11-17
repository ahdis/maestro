package net.ihe.gazelle.maestro.extraction.step.business;

import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.spi.business.StepRun;
import net.ihe.gazelle.maestro.extraction.step.business.xml.XmlExtractionService;
import net.ihe.gazelle.maestro.extraction.step.business.xml.XmlExtractionStepDefinition;
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

        StepRunReport stepRunReport = stepExecutor.execute(stepRun).join();

        assertEquals(UNDEFINED, stepRunReport.getResult());
        assertEquals(1, stepRunReport.getOutputs().size());
        assertTrue(stepRunReport.getOutputs().stream().anyMatch(output ->
                output.getName().equals(XmlExtractionStepDefinition.FAILURE_MESSAGE)
        ));
    }

    private Step buildStep(byte[] xmlFile, String xmlPath) {
        return new Step()
                .setId("extractionXmlStep")
                .setName("extractionXmlStep")
                .setType("EXTRACT_XML")
                .setProperties(List.of(
                        new ByteArrayProperty(XML_FILE, xmlFile),
                        new StringProperty(XML_PATH, xmlPath)
                ));
    }
}
