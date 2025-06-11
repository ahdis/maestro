package net.ihe.gazelle.maestro;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.technical.dto.report.TestReportDTO;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import net.ihe.gazelle.servicemetadata.api.business.Binding;
import net.ihe.gazelle.servicemetadata.api.business.HttpRestBinding;
import net.ihe.gazelle.servicemetadata.api.business.ProvidedInterface;
import net.ihe.gazelle.servicemetadata.api.business.Service;
import net.ihe.gazelle.servicemetadata.technical.jaxrs.ServiceDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.awaitility.Awaitility.await;

public class WireMockSingleton {

    private static WireMockServer wireMockServer;

    private WireMockSingleton() {
        // Private constructor to enforce Singleton pattern
    }

    public static synchronized WireMockServer startServer(int port) {
        if (wireMockServer == null) {
            wireMockServer = new WireMockServer(port);
            wireMockServer.start();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> wireMockServer.stop()));
        }
        return wireMockServer;
    }

    public static synchronized void mockGetProfiles() {
        wireMockServer.stubFor(get(urlPathMatching("/matchboxv3/gazelle/validation/v2/profiles"))
                .willReturn(ok().withBody("[]"))
        );
    }

    public static synchronized void mockValidate() throws IOException {
        wireMockServer.stubFor(post(urlPathMatching("/matchboxv3/gazelle/validation/v2/validate"))
                .willReturn(ok().withBody(getResourceAsString("/response.json")))
        );
    }

    public static synchronized void mockServiceRegistry() throws IOException {
        String servicesJson = getResourceAsString("/services.json");
        TextSerDes serDes = new JacksonSerDes();
        List<Service> services = Arrays.stream(serDes.deserialize(servicesJson, ServiceDTO[].class))
                .map(ServiceDTO::getBusinessObject)
                .toList();

        for (Service service : services) {
            for (ProvidedInterface providedInterface : service.getProvidedInterfaces()) {
                for (Binding binding : providedInterface.getBindings()) {
                    if (binding instanceof HttpRestBinding restBinding) {
                        restBinding.setServiceUrl("http://localhost:" + wireMockServer.port());
                    }
                }
            }
        }

        List<ServiceDTO> serviceDTOList = services.stream()
                .map(ServiceDTO::new)
                .toList();
        String replacedJson = serDes.serializeAsString(serviceDTOList);

        wireMockServer.stubFor(get("/services")
                .willReturn(okJson(replacedJson))
        );
    }

    public static void mockCallback() {
        wireMockServer.stubFor(post(urlPathMatching("/mock/gazelle/rest")).willReturn(ok()));
    }

    public static TestReport awaitTestReport() {
        String content = await()
                .atMost(60, TimeUnit.SECONDS)
                .until(() -> {
                    List<LoggedRequest> matchingRequests = getMatchingRequests().apply(wireMockServer);
                    return !matchingRequests.isEmpty() ? matchingRequests : null;
                }, Objects::nonNull).getFirst().getPart("json report").getBody().asString();
        return new JacksonSerDes().deserialize(content, TestReportDTO.class).getDomain();
    }

    private static Function<WireMockServer, List<LoggedRequest>> getMatchingRequests() {
        return (WireMockServer wireMockServer) -> wireMockServer.findRequestsMatching(
                postRequestedFor(
                        urlPathMatching("/mock/gazelle/rest")
                ).build()
        ).getRequests();
    }

    public static WireMockServer getWireMockServer() {
        return wireMockServer;
    }

    public static String getResourceAsString(String resourcePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/it/resources" + resourcePath)));
    }
}
