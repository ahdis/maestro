package net.ihe.gazelle.maestro.xml.extraction.step.technical.service;

import net.ihe.gazelle.maestro.xml.extraction.step.business.xml.XmlExtractionService;
import net.ihe.gazelle.maestro.xml.extraction.step.technical.xml.XmlExtractionException;
import net.ihe.gazelle.maestro.xml.extraction.step.technical.xml.XmlExtractionServiceImpl;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

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
    void testExtractSimpleValue() {
        String result1 = service.extractValue(sampleXml, "/root/element/child[1]", Map.of());
        assertEquals("<child>value1</child>", result1);

        String result2 = service.extractValue(sampleXml, "/root/single/text()", Map.of());
        assertEquals("singleValue", result2);

        String result3 = service.extractValue(sampleXml, "/root/element/child[2]/text()",Map.of());
        assertEquals("value2", result3);
    }

    @Test
    void testExtractXMLFragment() {
        String result1 = service.extractValue(sampleXml, "/root/element", Map.of());
        assertEquals("<element><child>value1</child><child>value2</child><empty/></element>", result1);

        String result2 = service.extractValue(sampleXml, "/root", Map.of());
        assertEquals("<root><element><child>value1</child><child>value2</child><empty/></element><single>singleValue</single></root>", result2);
    }

    @Test
    void testExtractMultipleValues() {
        Exception e = assertThrows(XmlExtractionException.class,
                () -> service.extractValue(sampleXml, "/root/element/child", Map.of()));
        assertTrue(e.getMessage().contains("Multiple elements found"));
    }

    @Test
    void testExtractEmptyValue() {
        String result1 = service.extractValue(sampleXml, "/root/element/empty", Map.of());
        assertEquals("<empty/>", result1);

        String result2 = service.extractValue(sampleXml, "/root/element/empty/text()", Map.of());
        assertNull(result2);
    }

    @Test
    void testExtractNonExistingXPath() {
        String extractedValue1 = service.extractValue(sampleXml, "/nonExisting", Map.of());
        assertNull(extractedValue1);

        String extractedValue2 = service.extractValue(sampleXml, "/root/nonExisting", Map.of());
        assertNull(extractedValue2);
    }

    @Test
    void testExtractInvalidXPath() {
        assertThrows(XmlExtractionException.class,
                () -> service.extractValue(sampleXml, ".?invalid_xpath", Map.of()));
    }

    @Test
    void testExtractFromSAMLAssertion() {
        // Test with namespace saml2:
        String extractValue1 = service.extractValue(samlAssertionXml, "/saml2:Assertion/saml2:Issuer/text()", Map.of());
        assertEquals("urn:idp:ES:countryB", extractValue1);

        // Test with local-name() (no namespace)
        String extractedValue2 = service.extractValue(samlAssertionXml, "//*[local-name()='Assertion']/*[local-name()='Issuer']/text()", Map.of());
        assertEquals("urn:idp:ES:countryB", extractedValue2);

        // Test with namespace ds:
        String extractValue3 = service.extractValue(samlAssertionXml, "/saml2:Assertion/ds:Signature/ds:KeyInfo/ds:X509Data/ds:X509Certificate/text()", Map.of());
        assertNotNull(extractValue3);
        assertTrue(extractValue3.startsWith("MIIGYDCCBEigAw"));

        // Test with namespace ec:
        String extractValue5 = service.extractValue(samlAssertionXml, "/saml2:Assertion/ds:Signature/ds:SignedInfo/ds:Reference/ds:Transforms/ds:Transform[2]/ec:InclusiveNamespaces/@PrefixList", Map.of());
        assertEquals("xsd", extractValue5);
    }

    @Test
    void testExtractFromSAMLAssertionComplex() {
        // Test extraction of the 5th Attribute element which contains "Pharmacy"
        String extractValue6 = service.extractValue(samlAssertionXml, "/saml2:Assertion/saml2:AttributeStatement/saml2:Attribute[5]/saml2:AttributeValue", Map.of());
        assertTrue(extractValue6.contains("Pharmacy"));
        assertTrue(extractValue6.contains("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""));
        assertTrue(extractValue6.contains("xsi:type=\"xsd:string\""));

        // Or test just the text content
        String extractValue7 = service.extractValue(samlAssertionXml, "/saml2:Assertion/saml2:AttributeStatement/saml2:Attribute[5]/saml2:AttributeValue/text()", Map.of());
        assertEquals("Pharmacy", extractValue7);
    }

    @Test
    void testExtractFromSoapCDA() throws IOException {
        byte[] soapCDA = Files.readAllBytes(Paths.get("src/test/resources/xml/W8_SOAP_MURC_CDAL1.xml"));
        String extractValue1 = service.extractValue(soapCDA, "/urn:ClinicalDocument/urn:component/urn:nonXMLBody/urn:text/text()", Map.of());
        assertTrue(extractValue1.startsWith("JVBERi0x"));
    }

    @Test
    void testExtractWithCustomNamespaces() {
        // Test with custom namespace map - overriding auto-detected namespaces
        Map<String, String> customNamespaces = Map.of(
            "saml", "urn:oasis:names:tc:SAML:2.0:assertion",
            "xmldsig", "http://www.w3.org/2000/09/xmldsig#"
        );

        // Use custom prefixes instead of the ones in the document
        String result1 = service.extractValue(samlAssertionXml, "/saml:Assertion/saml:Issuer/text()", customNamespaces);
        assertEquals("urn:idp:ES:countryB", result1);

        // Test with xmldsig prefix instead of ds
        String result2 = service.extractValue(samlAssertionXml, "/saml:Assertion/xmldsig:Signature/xmldsig:KeyInfo/xmldsig:X509Data/xmldsig:X509Certificate/text()", customNamespaces);
        assertNotNull(result2);
        assertTrue(result2.startsWith("MIIGYDCCBEigAw"));
    }

    @Test
    void testExtractWithDefaultNamespace() {
        // Create XML with a default namespace (no prefix)
        byte[] xmlWithDefaultNs = """
            <root xmlns="http://example.com/default">
                <element>
                    <value>test content</value>
                </element>
            </root>
            """.getBytes(StandardCharsets.UTF_8);

        // Map empty string to the default namespace URI
        Map<String, String> namespaces = Map.of(
            "", "http://example.com/default"
        );

        // Note: XPath requires a prefix even for default namespaces, Saxon maps "" to "default"
        String result = service.extractValue(xmlWithDefaultNs, "/default:root/default:element/default:value/text()", namespaces);
        assertEquals("test content", result);
    }

    @Test
    void testExtractWithMixedNamespaces() {
        // Test with multiple namespaces including SAML, XML Signature, and XML Encryption
        Map<String, String> namespaces = Map.of(
            "s", "urn:oasis:names:tc:SAML:2.0:assertion",
            "ds", "http://www.w3.org/2000/09/xmldsig#",
            "ec", "http://www.w3.org/2001/10/xml-exc-c14n#",
            "xsi", "http://www.w3.org/2001/XMLSchema-instance",
            "xsd", "http://www.w3.org/2001/XMLSchema"
        );

        // Extract with short prefix 's' instead of 'saml2'
        String result1 = service.extractValue(samlAssertionXml, "/s:Assertion/s:Issuer/text()", namespaces);
        assertEquals("urn:idp:ES:countryB", result1);

        // Extract with ec namespace
        String result2 = service.extractValue(samlAssertionXml, "/s:Assertion/ds:Signature/ds:SignedInfo/ds:Reference/ds:Transforms/ds:Transform[2]/ec:InclusiveNamespaces/@PrefixList", namespaces);
        assertEquals("xsd", result2);

        // Extract attribute with FriendlyName
        String result3 = service.extractValue(samlAssertionXml, "/s:Assertion/s:AttributeStatement/s:Attribute[1]/@FriendlyName", namespaces);
        assertEquals("XSPA Subject", result3);
    }

    @Test
    void testExtractWithEmptyNamespaceMap() {
        // When passing empty map, should fall back to auto-detection from document
        String result = service.extractValue(samlAssertionXml, "/saml2:Assertion/saml2:Issuer/text()", Map.of());
        assertEquals("urn:idp:ES:countryB", result);
    }
}
