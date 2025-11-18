package net.ihe.gazelle.maestro.extraction.step.conversion.step.business.cert.pem;

public interface PemConverterService {

    /**
     * Convert a certificate in DER format to PEM format
     * @param certificateInDerFormat the certificate in DER format
     * @return the certificate in PEM format
     */
    String convertToPemFormat(byte[] certificateInDerFormat);
}
