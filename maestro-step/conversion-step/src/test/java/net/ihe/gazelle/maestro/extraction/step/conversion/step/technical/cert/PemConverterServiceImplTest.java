package net.ihe.gazelle.maestro.extraction.step.conversion.step.technical.cert;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class PemConverterServiceImplTest {

    private final PemConverterServiceImpl pemConverterService = new PemConverterServiceImpl();

    @Test
    void convertToPemFormatWithValidDerCertificate() {
        // Given: A valid DER certificate (simulated as simple bytes)
        byte[] derCertificate = "TestCertificateData".getBytes(StandardCharsets.UTF_8);

        // When: Converting to PEM format
        String pemCertificate = pemConverterService.convertToPemFormat(derCertificate);

        // Then: The PEM certificate should have proper header, footer and Base64 content
        assertNotNull(pemCertificate);
        assertTrue(pemCertificate.startsWith("-----BEGIN CERTIFICATE-----"));
        assertTrue(pemCertificate.contains("-----END CERTIFICATE-----"));

        // Verify that the content between header and footer is Base64 encoded
        String expectedBase64 = Base64.getEncoder().encodeToString(derCertificate);
        assertTrue(pemCertificate.contains(expectedBase64));
    }

    @Test
    void convertToPemFormatWithNullCertificate() {
        // Given: A null certificate
        byte[] derCertificate = null;

        // When/Then: Should throw IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pemConverterService.convertToPemFormat(derCertificate)
        );

        assertEquals("Certificate in DER format cannot be null or empty", exception.getMessage());
    }

    @Test
    void convertToPemFormatWithEmptyCertificate() {
        // Given: An empty certificate
        byte[] derCertificate = new byte[0];

        // When/Then: Should throw IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pemConverterService.convertToPemFormat(derCertificate)
        );

        assertEquals("Certificate in DER format cannot be null or empty", exception.getMessage());
    }

    @Test
    void convertToPemFormatShouldFormatWith64CharactersPerLine() {
        // Given: A DER certificate with enough data to span multiple lines
        byte[] derCertificate = new byte[100];
        for (int i = 0; i < 100; i++) {
            derCertificate[i] = (byte) i;
        }

        // When: Converting to PEM format
        String pemCertificate = pemConverterService.convertToPemFormat(derCertificate);

        // Then: The PEM certificate should have lines with max 64 characters (excluding line separator)
        String[] lines = pemCertificate.split(System.lineSeparator());

        // Skip header and footer
        for (int i = 1; i < lines.length - 1; i++) {
            assertTrue(lines[i].length() <= 64,
                    "Line " + i + " has length " + lines[i].length() + " (expected <= 64)");
        }
    }

    @Test
    void convertToPemFormatShouldHaveProperStructure() {
        // Given: A simple DER certificate
        byte[] derCertificate = "X509Certificate".getBytes(StandardCharsets.UTF_8);

        // When: Converting to PEM format
        String pemCertificate = pemConverterService.convertToPemFormat(derCertificate);

        // Then: The PEM certificate should have exactly one header and one footer
        String[] lines = pemCertificate.split(System.lineSeparator());

        assertEquals("-----BEGIN CERTIFICATE-----", lines[0]);

        // Find the footer line (it should be the last non-empty line or second to last)
        int footerIndex = lines.length - 1;
        if (footerIndex >= 0 && lines[footerIndex].isEmpty()) {
            footerIndex--;
        }
        assertEquals("-----END CERTIFICATE-----", lines[footerIndex]);

        // Verify there's at least one line of Base64 content
        assertTrue(lines.length >= 3);
    }

    @Test
    void convertToPemFormatWithRealCertificate() throws IOException {
        byte[] derCert = Files.readAllBytes(Paths.get("src/test/resources/cert/cert.der"));
        String pemCert = Files.readString(Paths.get("src/test/resources/cert/cert.pem"));

        String convertedPemCert = pemConverterService.convertToPemFormat(derCert);
        assertEquals(pemCert.trim(), convertedPemCert.trim());
    }

    @Test
    void convertToPemFormatShouldBeReversible() {
        // Given: A DER certificate
        byte[] originalDerCertificate = "This is a test certificate data".getBytes(StandardCharsets.UTF_8);

        // When: Converting to PEM and then extracting the Base64 content
        String pemCertificate = pemConverterService.convertToPemFormat(originalDerCertificate);

        // Extract Base64 content (remove header, footer, and newlines)
        String base64Content = pemCertificate
                .replace("-----BEGIN CERTIFICATE-----", "")
                .replace("-----END CERTIFICATE-----", "")
                .replaceAll("\\s+", "");

        byte[] decodedCertificate = Base64.getDecoder().decode(base64Content);

        // Then: The decoded content should match the original
        assertArrayEquals(originalDerCertificate, decodedCertificate);
    }
}

