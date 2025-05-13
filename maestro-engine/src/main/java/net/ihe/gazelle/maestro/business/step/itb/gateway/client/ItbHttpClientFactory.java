package net.ihe.gazelle.maestro.business.step.itb.gateway.client;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

public class ItbHttpClientFactory {

    @ConfigProperty(name = "itb.api.url")
    String itbApiUrl;

    @ConfigProperty(name = "itb.api.key")
    String itbApiKey;

    @Produces
    @RequestScoped
    public ItbHttpClient createItbHttpClient() {
        return new ItbHttpClient(itbApiUrl, itbApiKey);
    }
}
