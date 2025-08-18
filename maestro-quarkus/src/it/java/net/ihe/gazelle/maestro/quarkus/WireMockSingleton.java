/*
 * Copyright 2025 Kereval.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ihe.gazelle.maestro.quarkus;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import net.ihe.gazelle.m2m.client.technical.KeycloakConfigurationProvider;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.technical.dto.report.TestReportDTO;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import net.ihe.gazelle.oidc.server.OIDCJWTGenerator;
import net.ihe.gazelle.servicemetadata.api.business.Binding;
import net.ihe.gazelle.servicemetadata.api.business.HttpRestBinding;
import net.ihe.gazelle.servicemetadata.api.business.ProvidedInterface;
import net.ihe.gazelle.servicemetadata.api.business.Service;
import net.ihe.gazelle.servicemetadata.technical.jaxrs.ServiceDTO;
import net.ihe.gazelle.sso.api.business.configuration.SSOConfigurationProvider;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Disabled;

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

@Disabled
public class WireMockSingleton {

    private static WireMockServer wireMockServer;
    private static WireMockServer keycloakMockServer;
    private static WireMockServer datahouseMockServer;

    private static final String K_8_SID_VARIABLE_NAME = ConfigProvider.getConfig().getValue("gzl.k8s.id.env.variable.name", String.class);
    private static final SSOConfigurationProvider ssoConfigurationProvider = new KeycloakConfigurationProvider(K_8_SID_VARIABLE_NAME);


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

    public static void mockGetProfiles() {
        wireMockServer.stubFor(get(urlPathMatching("/matchboxv3/gazelle/validation/v2/profiles"))
                .willReturn(ok().withBody("[]"))
        );
    }

    public static void mockValidate() throws IOException {
        mockValidate("/response.json");
    }

    public static void mockValidate(String resourcePath) throws IOException {
        wireMockServer.stubFor(post(urlPathMatching("/matchboxv3/gazelle/validation/v2/validate"))
                .willReturn(ok().withBody(getResourceAsString(resourcePath)))
        );
    }

    public static void mockServiceRegistry() throws IOException {
        String servicesJson = getResourceAsString("/services.json");
        TextSerDes serDes = new JacksonSerDes();
        List<Service> services = Arrays.stream(serDes.deserialize(servicesJson, ServiceDTO[].class))
                .map(ServiceDTO::getBusinessObject)
                .toList();

        for (Service service : services) {
            for (ProvidedInterface providedInterface : service.getProvidedInterfaces()) {
                for (Binding binding : providedInterface.getBindings()) {
                    if (binding instanceof HttpRestBinding restBinding) {
                        restBinding.setServiceUrl(restBinding.getServiceUrl().replace(":port", ":" + wireMockServer.port()));
                    }
                }
            }
        }

        List<ServiceDTO<Service>> serviceDTOList = services.stream()
                .map(ServiceDTO::new)
                .toList();
        String replacedJson = serDes.serializeAsString(serviceDTOList);

        wireMockServer.stubFor(get("/service-registry/services?_limit=all&_offset=0")
                .willReturn(okJson(replacedJson).withHeader("Content-Range", "DeployedServices 1-1/1"))
        );
    }

    public static void mockCallback() {
        wireMockServer.stubFor(post(urlPathMatching("/mock/gazelle/rest")).willReturn(ok()));
    }

    public static void mockKeycloak() {
        keycloakMockServer = new WireMockServer(28080);

        keycloakMockServer.start();

        String tokenUrl = "/realms/gazelle/protocol/openid-connect/token";
        keycloakMockServer.stubFor(post(tokenUrl)
                .withRequestBody(containing("client_id=" + ssoConfigurationProvider.getClientId()))
                .withRequestBody(containing("client_secret=" + ssoConfigurationProvider.getClientSecret()))
                .willReturn(ok("{\"access_token\":\"" + OIDCJWTGenerator.getValidJwtWithGroups(List.of("admin")) + "\"}")));
    }

    public static void mockDatahouse(){
        datahouseMockServer = new WireMockServer(8080);
        datahouseMockServer.start();
        datahouseMockServer.stubFor(post("/datahouse/rest/v1/items")
                .willReturn(created().withHeader("Location", "http://localhost:8080/datahouse/rest/v1/items/1234567890"))
        );
        //stub /attachments endpoint to return a 200 Ok response with a list of strings as the body, representing attachment IDs
        datahouseMockServer.stubFor(post(urlEqualTo("/datahouse/rest/v1/items/attachments"))
                .willReturn(aResponse()
                        .withTransformers("attachment-count-transformer") // the name from getName()
                        .withHeader("Content-Type", "application/json"))
        );
    }

    public static TestReport awaitTestReport() {
        String content = await()
                .atMost(3, TimeUnit.SECONDS)
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

    public static String getResourceAsString(String resourcePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/it/resources" + resourcePath)));
    }

    public static synchronized void stop() {
        wireMockServer.stop();
        wireMockServer = null;
    }
}
