package net.ihe.gazelle.maestro;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import net.ihe.gazelle.maestro.technical.dto.report.TestReportDTO;
import net.ihe.gazelle.testreport.Result;
import net.ihe.gazelle.testreport.TestReport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestProfile(TMTestRunControllerProfile.class)
class TMTestRunControllerIT implements QuarkusTestProfile {

    private static WireMockServer wireMockServer;

    @BeforeEach
    void init() {
        wireMockServer = new WireMockServer(18088);
        wireMockServer.start();
        configureFor(wireMockServer.port());
    }

    @AfterEach
    void clear() {
        wireMockServer.stop();
    }

    @Test
    void runPASSEDTestFromTM() throws IOException {
        wireMockServer.stubFor(get(urlPathMatching("/http-validator/rest/metadata")).willReturn(ok().withBody("[]")));
        wireMockServer.stubFor(post(urlPathMatching("/http-validator/rest/validation/validate")).willReturn(ok().withBody(readJsonString("src/it/resources/tmintegration/passed_response.json"))));
        wireMockServer.stubFor(post(urlPathMatching("/mock/gazelle/rest")).willReturn(ok()));

        JsonNode tmRequestBody = assertDoesNotThrow(() -> readJsonScript("tmintegration/script.json"));

        given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(tmRequestBody)
                .when().post("http://localhost:8151/maestro/v0/run")
                .then().statusCode(200)
                .extract().response();

        byte[] content = await()
                .atMost(60, TimeUnit.SECONDS)
                .until(() -> {
                    List<LoggedRequest> matchingRequests = getMatchingRequests().apply(wireMockServer);
                    return !matchingRequests.isEmpty() ? matchingRequests : null;
                }, Objects::nonNull).getFirst().getBody();
        TestReport testReport = new ObjectMapper().readValue(content, TestReportDTO.class).getDomain();
        assertTrue(testReport.isValid());
        assertEquals(Result.PASSED, testReport.getResult());
    }

    @Test
    void runFAILEDTestFromTM() throws IOException {
        wireMockServer.stubFor(get(urlPathMatching("/http-validator/rest/metadata")).willReturn(ok().withBody("[]")));
        wireMockServer.stubFor(post(urlPathMatching("/http-validator/rest/validation/validate")).willReturn(ok().withBody(readJsonString("src/it/resources/tmintegration/failed_response.json"))));
        wireMockServer.stubFor(post(urlPathMatching("/mock/gazelle/rest")).willReturn(ok()));

        JsonNode tmRequestBody = assertDoesNotThrow(() -> readJsonScript("tmintegration/script.json"));

        given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(tmRequestBody)
                .when().post("http://localhost:8151/maestro/v0/run")
                .then().statusCode(200)
                .extract().response();

        byte[] content = await()
                .atMost(60, TimeUnit.SECONDS)
                .until(() -> {
                    List<LoggedRequest> matchingRequests = getMatchingRequests().apply(wireMockServer);
                    return !matchingRequests.isEmpty() ? matchingRequests : null;
                }, Objects::nonNull).getFirst().getBody();
        TestReport testReport = new ObjectMapper().readValue(content, TestReportDTO.class).getDomain();
        assertTrue(testReport.isValid());
        assertEquals(Result.FAILED, testReport.getResult());
    }

    private Function<WireMockServer, List<LoggedRequest>> getMatchingRequests() {
        return (WireMockServer wireMockServer) -> wireMockServer.findRequestsMatching(
                postRequestedFor(
                        urlPathMatching("/mock/gazelle/rest")
                ).build()
        ).getRequests();
    }

    private JsonNode readJsonScript(String path) throws IOException {
        return new ObjectMapper().readTree(Thread.currentThread().getContextClassLoader().getResourceAsStream(path));
    }

    private String readJsonString(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }
}
