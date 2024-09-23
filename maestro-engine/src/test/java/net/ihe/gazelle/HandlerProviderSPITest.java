package net.ihe.gazelle;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import net.ihe.gazelle.business.step.validation.ValidationHandler;
import net.ihe.gazelle.handler.HandlerProviderSPI;
import net.ihe.gazelle.runner.Handler;
import net.ihe.gazelle.servicemetadata.api.domain.structure.Interface;
import net.ihe.gazelle.servicemetadata.api.domain.structure.RestBinding;
import net.ihe.gazelle.servicemetadata.api.domain.structure.Service;
import net.ihe.gazelle.technical.configuration.ServiceRegistryDAO;
import net.ihe.gazelle.technical.configuration.ServiceRegistryImpl;
import net.ihe.gazelle.validation.api.domain.metadata.structure.ValidationInterface;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HandlerProviderSPITest {

    static WireMockServer wireMockServer;

    @BeforeAll
    static void SetUp(){
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> wireMockServer.stop()));


    }

    @AfterAll
    static void tearDownAll(){wireMockServer.stop();}

    @Test
    @Disabled("Need more studies on how to mock ValidationService")
    void getHandlerWithoutException() throws IOException {

        //TODO Implement before next release

        wireMockServer.stubFor(WireMock.post(WireMock.urlPathMatching("/validate")).willReturn(WireMock.ok().withBody(getJson())));

        HandlerProviderSPI handlerProviderSPI = new HandlerProviderSPI(new ServiceRegistryImpl(new ServiceRegistryDAOMock()));
        Handler handler = handlerProviderSPI.getHandler(ValidationHandler.class, "matchbox");
        assertEquals(ValidationHandler.class, handler.getClass());
    }

    public class ServiceRegistryDAOMock implements ServiceRegistryDAO{

        @Override
        public List<Service> loadFile(String servicesFilePath) {

            Service service = new Service();
            service.setName("matchbox");

            List<Interface> interfaces = new ArrayList<>();
            Interface inter = new ValidationInterface();
            RestBinding restBinding = new RestBinding();
            restBinding.setServiceUrl(wireMockServer.url("/validate"));
            inter.addBinding(restBinding);
            interfaces.add(inter);
            service.setProvidedInterfaces(interfaces);

            return List.of(service);
        }
    }

    private static String getJson() throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/test/resources/response.json")));

    }

}
