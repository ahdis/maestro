package net.ihe.gazelle.maestro.extraction.step.conversion.step.business.cert;

import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.extraction.step.conversion.step.business.cert.pem.ConvertCertificateDerToPemStepDefinition;
import net.ihe.gazelle.maestro.extraction.step.conversion.step.business.cert.pem.ConvertCertificateDerToPemStepExecutor;
import net.ihe.gazelle.maestro.extraction.step.conversion.step.business.cert.pem.PemConverterService;
import net.ihe.gazelle.maestro.extraction.step.conversion.step.technical.cert.PemConverterServiceImpl;
import net.ihe.gazelle.maestro.spi.business.StepRun;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static net.ihe.gazelle.maestro.api.business.testreport.StepResult.DONE;
import static net.ihe.gazelle.maestro.api.business.testreport.StepResult.UNDEFINED;
import static net.ihe.gazelle.maestro.extraction.step.conversion.step.business.base64.encode.EncodeBase64StepDefinition.FAILURE_MESSAGE;
import static net.ihe.gazelle.maestro.extraction.step.conversion.step.business.cert.pem.ConvertCertificateDerToPemStepDefinition.DER_CERTIFICATE;
import static net.ihe.gazelle.maestro.extraction.step.conversion.step.business.cert.pem.ConvertCertificateDerToPemStepDefinition.PEM_CERTIFICATE;
import static org.junit.jupiter.api.Assertions.*;

class ConvertCertificateDerToPemStepExecutorTest {

    private final PemConverterService pemConverterService = new PemConverterServiceImpl();
    private final ConvertCertificateDerToPemStepExecutor stepExecutor = new ConvertCertificateDerToPemStepExecutor(pemConverterService);

    @Test
    void testConvertValidDerCertificateToPem() {
        byte[] derCertificate = "TestCertificateData".getBytes(StandardCharsets.UTF_8);
        Step step = buildStep(derCertificate);
        StepRun stepRun = new StepRun(step, List.of());

        StepRunReport stepRunReport = stepExecutor.execute(stepRun).join();

        assertEquals(DONE, stepRunReport.getResult());
        assertEquals(1, stepRunReport.getOutputs().size());
        assertTrue(stepRunReport.getOutputs().stream().anyMatch(output ->
                output.getName().equals(PEM_CERTIFICATE)
        ));

        byte[] pemCertificateBytes = stepRunReport.getOutputs().stream()
                .filter(output -> output.getName().equals(PEM_CERTIFICATE))
                .findFirst()
                .map(output -> (byte[]) output.getValue())
                .orElse(null);

        assertNotNull(pemCertificateBytes);
        String pemCertificate = new String(pemCertificateBytes, StandardCharsets.UTF_8);
        assertTrue(pemCertificate.startsWith("-----BEGIN CERTIFICATE-----"));
        assertTrue(pemCertificate.contains("-----END CERTIFICATE-----"));
    }

    @Test
    void testUndefinedResultForEmptyDerCertificate() {
        // Arrange
        byte[] derCertificate = new byte[0];
        Step step = buildStep(derCertificate);
        StepRun stepRun = new StepRun(step, List.of());

        // Act
        StepRunReport stepRunReport = stepExecutor.execute(stepRun).join();

        // Assert
        assertEquals(UNDEFINED, stepRunReport.getResult());
        assertEquals(1, stepRunReport.getOutputs().size());
        assertTrue(stepRunReport.getOutputs().stream().anyMatch(output ->
                output.getName().equals(FAILURE_MESSAGE)
        ));

        String failureMessage = stepRunReport.getOutputs().stream()
                .filter(output -> output.getName().equals(FAILURE_MESSAGE))
                .findFirst()
                .map(output -> (String) output.getValue())
                .orElse(null);

        assertNotNull(failureMessage);
    }

    @Test
    void testUndefinedResultForNullDerCertificate() {
        Step step = buildStep(null);
        StepRun stepRun = new StepRun(step, List.of());

        // Act
        StepRunReport stepRunReport = stepExecutor.execute(stepRun).join();

        // Assert
        assertEquals(UNDEFINED, stepRunReport.getResult());
        assertEquals(1, stepRunReport.getOutputs().size());
        assertTrue(stepRunReport.getOutputs().stream().anyMatch(output ->
                output.getName().equals(FAILURE_MESSAGE)
        ));

        String failureMessage = stepRunReport.getOutputs().stream()
                .filter(output -> output.getName().equals(FAILURE_MESSAGE))
                .findFirst()
                .map(output -> (String) output.getValue())
                .orElse(null);

        assertNotNull(failureMessage);
    }

    @Test
    void testHandleLargeCertificate() {
        // Arrange
        byte[] derCertificate = new byte[1024];
        for (int i = 0; i < 1024; i++) {
            derCertificate[i] = (byte) (i % 256);
        }
        Step step = buildStep(derCertificate);
        StepRun stepRun = new StepRun(step, List.of());

        // Act
        StepRunReport stepRunReport = stepExecutor.execute(stepRun).join();

        // Assert
        assertEquals(DONE, stepRunReport.getResult());
        assertEquals(1, stepRunReport.getOutputs().size());

        byte[] pemCertificateBytes = stepRunReport.getOutputs().stream()
                .filter(output -> output.getName().equals(PEM_CERTIFICATE))
                .findFirst()
                .map(output -> (byte[]) output.getValue())
                .orElse(null);

        assertNotNull(pemCertificateBytes);
        String pemCertificate = new String(pemCertificateBytes, StandardCharsets.UTF_8);
        assertTrue(pemCertificate.startsWith("-----BEGIN CERTIFICATE-----"));
    }

    @Test
    void testHandleExceptionFromService() {
        // Arrange - Create a mock service that throws an exception
        PemConverterService failingService = derCert -> {
            throw new RuntimeException("Service error");
        };
        ConvertCertificateDerToPemStepExecutor failingExecutor = new ConvertCertificateDerToPemStepExecutor(failingService);

        byte[] derCertificate = "TestData".getBytes(StandardCharsets.UTF_8);
        Step step = buildStep(derCertificate);
        StepRun stepRun = new StepRun(step, List.of());

        StepRunReport stepRunReport = failingExecutor.execute(stepRun).join();

        assertEquals(UNDEFINED, stepRunReport.getResult());
        assertEquals(1, stepRunReport.getOutputs().size());
        assertTrue(stepRunReport.getOutputs().stream().anyMatch(output ->
                output.getName().equals(FAILURE_MESSAGE)
        ));

        String failureMessage = stepRunReport.getOutputs().stream()
                .filter(output -> output.getName().equals(FAILURE_MESSAGE))
                .findFirst()
                .map(output -> (String) output.getValue())
                .orElse(null);

        assertNotNull(failureMessage);
    }

    private Step buildStep(byte[] derCertificate) {
        return new Step()
                .setId("convertDerToPemStep")
                .setName("convertDerToPemStep")
                .setType(ConvertCertificateDerToPemStepDefinition.TYPE)
                .setProperties(List.of(
                        new ByteArrayProperty(DER_CERTIFICATE, derCertificate)
                ));
    }
}
