package net.ihe.gazelle.maestro.xml.extraction.step.technical.service;

import net.ihe.gazelle.maestro.xml.extraction.step.business.xml.XmlExtractionService;
import net.ihe.gazelle.maestro.xml.extraction.step.technical.xml.XmlExtractionException;
import net.ihe.gazelle.maestro.xml.extraction.step.technical.xml.XmlExtractionServiceImpl;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class XmlExtractionServiceTest {

    private final XmlExtractionService service = new XmlExtractionServiceImpl();

    private final byte[] sampleXml = """
            <root>
                <element>
                    <child>value1</child>
                    <child>value2</child>
                    <empty></empty>
                </element>
                <single>singleValue</single>
            </root>
            """.getBytes(StandardCharsets.UTF_8);

    private final byte[] samlAssertionXml;

    public XmlExtractionServiceTest() {
        try {
            samlAssertionXml = Files.readAllBytes(Paths.get("src/test/resources/xml/SAMLAssertion.xml"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load XML Test files", e);
        }
    }

    @Test
    void testExtractValue() {
        String result1 = service.extractValue(sampleXml, "/root/element/child[1]");
        assertEquals("<child>value1</child>", result1);

        String result2 = service.extractValue(sampleXml, "/root/single/text()");
        assertEquals("singleValue", result2);

        String result3 = service.extractValue(sampleXml, "/root/element/child[2]/text()");
        assertEquals("value2", result3);
    }

    @Test
    void testExtractXMLFragment() {
        String result1 = service.extractValue(sampleXml, "/root/element");
        assertEquals("<element><child>value1</child><child>value2</child><empty/></element>", result1);

        String result2 = service.extractValue(sampleXml, "/root");
        assertEquals("<root><element><child>value1</child><child>value2</child><empty/></element><single>singleValue</single></root>", result2);
    }

    @Test
    void testExtractMultipleValues() {
        Exception e = assertThrows(XmlExtractionException.class,
                () -> service.extractValue(sampleXml, "/root/element/child"));
        assertTrue(e.getMessage().contains("Multiple elements found"));
    }

    @Test
    void testExtractEmptyValue() {
        String result1 = service.extractValue(sampleXml, "/root/element/empty");
        assertEquals("<empty/>", result1);

        String result2 = service.extractValue(sampleXml, "/root/element/empty/text()");
        assertNull(result2);
    }

    @Test
    void testExtractNonExistingXPath() {
        String extractedValue1 = service.extractValue(sampleXml, "/nonExisting");
        assertNull(extractedValue1);

        String extractedValue2 = service.extractValue(sampleXml, "/root/nonExisting");
        assertNull(extractedValue2);
    }

    @Test
    void testExtractInvalidXPath() {
        assertThrows(XmlExtractionException.class,
                () -> service.extractValue(sampleXml, ".?invalid_xpath"));
    }

    @Test
    void testExtractFromSAMLAssertion() {
        // Test with namespace saml2:
        String extractValue1 = service.extractValue(samlAssertionXml, "/saml2:Assertion/saml2:Issuer/text()");
        assertEquals("urn:idp:ES:countryB", extractValue1);

        // Test with local-name() (no namespace)
        String extractedValue2 = service.extractValue(samlAssertionXml, "//*[local-name()='Assertion']/*[local-name()='Issuer']/text()");
        assertEquals("urn:idp:ES:countryB", extractedValue2);

        // Test with namespace ds:
        String extractValue3 = service.extractValue(samlAssertionXml, "/saml2:Assertion/ds:Signature/ds:KeyInfo/ds:X509Data/ds:X509Certificate/text()");
        assertNotNull(extractValue3);
        assertTrue(extractValue3.startsWith("MIIGYDCCBEigAw"));

        // Test with namespace ec:
        String extractValue5 = service.extractValue(samlAssertionXml, "/saml2:Assertion/ds:Signature/ds:SignedInfo/ds:Reference/ds:Transforms/ds:Transform[2]/ec:InclusiveNamespaces/@PrefixList");
        assertEquals("xsd", extractValue5);
    }

    @Test
    void testExtractFromSAMLAssertionComplex() {
        // Test extraction of the 5th Attribute element which contains "Pharmacy"
        String extractValue6 = service.extractValue(samlAssertionXml, "/saml2:Assertion/saml2:AttributeStatement/saml2:Attribute[5]/saml2:AttributeValue");
        assertTrue(extractValue6.contains("Pharmacy"));
        assertTrue(extractValue6.contains("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""));
        assertTrue(extractValue6.contains("xsi:type=\"xsd:string\""));

        // Or test just the text content
        String extractValue7 = service.extractValue(samlAssertionXml, "/saml2:Assertion/saml2:AttributeStatement/saml2:Attribute[5]/saml2:AttributeValue/text()");
        assertEquals("Pharmacy", extractValue7);
    }

    @Test
    void testExtractFromSoapCDA() throws IOException {
        byte[] soapCDA = Files.readAllBytes(Paths.get("src/test/resources/xml/W8_SOAP_MURC_CDAL1.xml"));
        String extractValue1 = service.extractValue(soapCDA, "/urn:ClinicalDocument/urn:component/urn:nonXMLBody/urn:text/text()");
        assertTrue(extractValue1.startsWith("JVBERi0x"));
    }
}
