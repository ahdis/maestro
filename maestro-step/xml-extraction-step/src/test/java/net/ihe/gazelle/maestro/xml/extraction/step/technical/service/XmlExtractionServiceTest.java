package net.ihe.gazelle.maestro.xml.extraction.step.technical.service;

import net.ihe.gazelle.maestro.xml.extraction.step.business.XmlExtractionService;
import net.ihe.gazelle.maestro.xml.extraction.step.technical.XmlExtractionException;
import net.ihe.gazelle.maestro.xml.extraction.step.technical.XmlExtractionServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class XmlExtractionServiceTest {

    private final XmlExtractionService service = new XmlExtractionServiceImpl();

    private final String sampleXml = """
            <root>
                <element>
                    <child>value1</child>
                    <child>value2</child>
                    <empty></empty>
                </element>
                <single>singleValue</single>
            </root>
            """;

    @Test
    public void testExtractValue() {
        String result1 = service.extractValue(sampleXml, "/root/element/child[1]");
        assertEquals("value1", result1);

        String result2 = service.extractValue(sampleXml, "/root/single");
        assertEquals("singleValue", result2);

        String result3 = service.extractValue(sampleXml, "/root/element/child[2]");
        assertEquals("value2", result3);
    }

    @Test
    public void testExtractMultipleValue() {
        String result = service.extractValue(sampleXml, "/root/element/child");
        assertEquals("value1,value2", result);
    }

    @Test
    public void testExtractEmptyValue() {
        String result = service.extractValue(sampleXml, "/root/element/empty");
        assertEquals("", result);
    }

    @Test
    public void testExtractNonExistingXPath() {
        assertThrows(XmlExtractionException.class, () -> {
            service.extractValue(sampleXml, "/root/nonExisting");
        });

        assertThrows(XmlExtractionException.class, () -> {
            service.extractValue("", "/root");
        });
    }

    @Test
    public void testExtractInvalidXPath() {
        assertThrows(XmlExtractionException.class, () -> {
            service.extractValue(sampleXml, ".?invalid_xpath");
        });
    }
}
