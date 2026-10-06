package net.ihe.gazelle.maestro.validation.step.technical.factory;

import com.sun.net.httpserver.HttpServer;
import net.ihe.gazelle.maestro.spi.technical.HandlerContext;
import net.ihe.gazelle.maestro.validation.step.technical.handler.ValidationHttpException;
import net.ihe.gazelle.maestro.validation.step.technical.handler.ValidationServiceHandler;
import net.ihe.gazelle.servicemetadata.api.business.HttpRestBinding;
import net.ihe.gazelle.servicemetadata.api.business.ProvidedInterface;
import net.ihe.gazelle.validation.v2.client.ValidationServiceApiClientFactory;
import org.junit.jupiter.api.Test;
import java.net.InetSocketAddress;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ReleasedValidationClientTest {
    private ValidationServiceHandler handler(HttpServer server, String version, int readTimeoutMs) {
        var provided = new ProvidedInterface();
        provided.setInterfaceName(ValidationServiceApiClientFactory.INTERFACE_NAME);
        provided.setInterfaceVersion(version);
        var binding = new HttpRestBinding();
        binding.setServiceUrl("http://localhost:" + server.getAddress().getPort());
        provided.addBinding(binding);
        var factory = new ValidationServiceHandlerFactory(() -> List.of(new ValidationServiceApiClientFactory()));
        return (ValidationServiceHandler) factory.createHandler(new HandlerContext("local-test"), provided,
                key -> key.endsWith("readTimeoutMs") ? String.valueOf(readTimeoutMs) : "1000");
    }

    @Test
    void reportsHttpStatusForBothReleasedApiVersionsWithoutResponseContent() throws Exception {
        var server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/", exchange -> {
            byte[] body = "private validation response".getBytes(java.nio.charset.StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(504, body.length);
            try (var output = exchange.getResponseBody()) { output.write(body); }
        });
        server.start();
        try {
            for (String version : List.of("1.0.0", "2.0.0")) {
                var failure = assertThrows(ValidationHttpException.class,
                        () -> handler(server, version, 1000).getValidationProfiles());
                assertEquals("Validation service returned HTTP 504", failure.getMessage());
                assertNotNull(failure.getCause());
                if (version.equals("2.0.0")) {
                    var request = new net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest()
                            .setValidationProfileId("test-profile");
                    var validationFailure = assertThrows(ValidationHttpException.class,
                            () -> handler(server, version, 1000).validate(request));
                    assertEquals("Validation service returned HTTP 504", validationFailure.getMessage());
                }
            }
        } finally { server.stop(0); }
    }

    @Test
    void appliesConfiguredReadTimeoutUsingReleasedClientConstructors() throws Exception {
        var server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/", exchange -> {
            try { Thread.sleep(250); } catch (InterruptedException failure) { Thread.currentThread().interrupt(); }
            exchange.close();
        });
        server.start();
        try {
            var failure = assertThrows(net.ihe.gazelle.validation.v2.client.TimeoutException.class,
                    () -> handler(server, "2.0.0", 30).getValidationProfiles());
            assertTrue(failure.getMessage().contains("30"));
        } finally { server.stop(0); }
    }
}
