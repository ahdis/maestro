package net.ihe.gazelle.maestro.client;

import java.net.URI;

/**
 * A factory class for creating instances of the {@link MaestroClient}.
 * This class provides a static method to instantiate a concrete implementation
 * of the {@code MaestroClient} interface configured with necessary parameters.
 */
public final class MaestroClientFactory {

    private MaestroClientFactory() {
    }

   /**
    * Creates a new instance of {@code MaestroClient} configured with the specified parameters.
    *
    * @param baseUrl the base URL of the Maestro service
    * @param k8sIdVariableName the Kubernetes environment variable used for ID configuration
    * @param connectTimeout the connection timeout in milliseconds
    * @param readTimeout the read timeout in milliseconds
    * @return a new {@link MaestroClient} instance
    */
    public static MaestroClient createClient(String baseUrl,
                                             String k8sIdVariableName,
                                             int connectTimeout,
                                             int readTimeout) {
        return new MaestroHttpClient(
                URI.create(baseUrl),
                k8sIdVariableName,
                connectTimeout,
                readTimeout
        );
    }
}
