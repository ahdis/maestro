package net.ihe.gazelle.maestro;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import jakarta.ws.rs.core.MediaType;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@TestProfile(TestRunControllerProfile.class)
class TestRunControllerIT implements QuarkusTestProfile {

    static WireMockServer wireMockServer;
    static ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() {
        wireMockServer = new WireMockServer(18089);
        wireMockServer.start();
        configureFor(wireMockServer.port());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> wireMockServer.stop()));
        objectMapper = new ObjectMapper();
    }

    @Test
    void launchTestSetWithOneValidation() {
        try {
            wireMockServer.stubFor(post(urlPathMatching("matchboxv3/gazelle/validation")).willReturn(ok().withBody(getJson())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        InputStream testRun = readJson();

        Response response = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(testRun)
                //TODO port shouldn't appear in test file
                .when().post("http://localhost:8151/maestro/testRuns/run")
                .then().statusCode(200)
                .extract().response();
        assertNotNull(response);
        ResponseBody body = response.getBody();
        assertNotNull(body);
        System.out.println(body.prettyPrint());

//        JsonNode node = objectMapper.readTree(body.prettyPrint());
//        TestReportDTO testReportDTO = objectMapper.treeToValue(node, TestReportDTO.class);
//        TestReport report = testReportDTO.getDomain();
//        assertNotNull(report);
    }

    private InputStream readJson() {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream("Testrun.json");
    }

    private static String getJson() throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/it/resources/response.json")));
    }
}
