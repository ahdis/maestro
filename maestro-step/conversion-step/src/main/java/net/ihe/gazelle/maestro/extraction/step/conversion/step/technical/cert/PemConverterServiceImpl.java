package net.ihe.gazelle.maestro.extraction.step.conversion.step.technical.cert;

import net.ihe.gazelle.maestro.extraction.step.conversion.step.business.cert.pem.PemConverterService;

import java.util.Base64;

public class PemConverterServiceImpl implements PemConverterService {

    private static final String PEM_CERTIFICATE_HEADER = "-----BEGIN CERTIFICATE-----";
    private static final String PEM_CERTIFICATE_FOOTER = "-----END CERTIFICATE-----";
    private static final int PEM_LINE_LENGTH = 64;

    @Override
    public String convertToPemFormat(byte[] certificateInDerFormat) {
        if (certificateInDerFormat == null || certificateInDerFormat.length == 0) {
            throw new IllegalArgumentException("Certificate in DER format cannot be null or empty");
        }

        // Encode DER certificate to Base64
        String base64Encoded = Base64.getEncoder().encodeToString(certificateInDerFormat);

        // Format the Base64 string with line breaks (64 characters per line)
        StringBuilder pemBuilder = new StringBuilder();
        pemBuilder.append(PEM_CERTIFICATE_HEADER).append(System.lineSeparator());

        int index = 0;
        while (index < base64Encoded.length()) {
            int endIndex = Math.min(index + PEM_LINE_LENGTH, base64Encoded.length());
            pemBuilder.append(base64Encoded, index, endIndex).append(System.lineSeparator());
            index = endIndex;
        }

        pemBuilder.append(PEM_CERTIFICATE_FOOTER).append(System.lineSeparator());

        return pemBuilder.toString();
    }
}
