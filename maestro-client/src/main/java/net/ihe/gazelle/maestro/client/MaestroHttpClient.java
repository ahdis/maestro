package net.ihe.gazelle.maestro.client;

import net.ihe.gazelle.lang.UnexpectedInternalErrorException;
import net.ihe.gazelle.m2m.client.technical.filter.apache.ApacheM2MHttpClientBuilder;
import net.ihe.gazelle.maestro.api.business.InvalidTestRunException;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;
import net.ihe.gazelle.maestro.api.technical.dto.report.TestReportDTO;
import net.ihe.gazelle.maestro.api.technical.dto.testrun.TestRunDTO;
import net.ihe.gazelle.maestro.api.technical.dto.testrun.TestSuiteRunDTO;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class MaestroHttpClient implements MaestroClient {

    public static final String TEST_RUN_ENDPOINT = "/v1/test/run";
    public static final String TEST_SUITE_RUN_ENDPOINT = "/v1/test-suite/run";
    public static final String CONTENT_TYPE = "application/json";
    public static final int DEFAULT_CONNECT_TIMEOUT_MILLISECONDS = 10000;
    public static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = 120000;

    private static final TextSerDes serDes = new JacksonSerDes();

    private final URI baseUri;
    private final HttpClient client;
    private final int msConnectTimeout;
    private final int msReadTimeout;

    public MaestroHttpClient(URI baseUri, String k8sIdVariableName) {
        this(baseUri, k8sIdVariableName, DEFAULT_CONNECT_TIMEOUT_MILLISECONDS, DEFAULT_READ_TIMEOUT_MILLISECONDS);
    }

    public MaestroHttpClient(URI baseUri, String k8sIdVariableName, int msConnectTimeout, int msReadTimeout) {
        if (baseUri == null) {
            throw new IllegalArgumentException("baseUri must not be null");
        }
        if (k8sIdVariableName == null || k8sIdVariableName.isBlank()) {
            throw new IllegalArgumentException("k8sIdVariableName must not be null or blank");
        }
        this.baseUri = baseUri;
        this.client = buildM2mHttpClient(k8sIdVariableName, msConnectTimeout, msReadTimeout);
        this.msConnectTimeout = msConnectTimeout;
        this.msReadTimeout = msReadTimeout;
    }

    @Override
    public TestReport executeTest(TestRun testRun, boolean persist) {
        return handleTechnicalErrors(() -> doExecuteTest(testRun, persist));
    }

    @Override
    public TestReport executeTestSuite(TestSuiteRun testSuiteRun, boolean persist) {
        return handleTechnicalErrors(() -> doExecuteTestSuite(testSuiteRun, persist));
    }

    private TestReport doExecuteTest(TestRun testRun, boolean persist) {
        String url = baseUri.toString() + TEST_RUN_ENDPOINT + "?persist=" + persist;
        TestRunDTO dto = new TestRunDTO(testRun);
        String payload = serDes.serializeAsString(dto);
        return executePost(url, payload);
    }

    private TestReport doExecuteTestSuite(TestSuiteRun testSuiteRun, boolean persist) {
        String url = baseUri.toString() + TEST_SUITE_RUN_ENDPOINT + "?persist=" + persist;
        TestSuiteRunDTO dto = new TestSuiteRunDTO(testSuiteRun);
        String payload = serDes.serializeAsString(dto);
        return executePost(url, payload);
    }

    private TestReport executePost(String url, String payload) {
        try {
            HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom()
                  .setConnectTimeout(msConnectTimeout)
                  .setSocketTimeout(msReadTimeout)
                  .build();
            httpPost.setConfig(requestConfig);
            StringEntity input = new StringEntity(payload, StandardCharsets.UTF_8);
            input.setContentType(CONTENT_TYPE);
            httpPost.addHeader("Content-type", CONTENT_TYPE);
            httpPost.setEntity(input);
            HttpResponse response = client.execute(httpPost);
            String body = readBody(response);
            return handleApplicationError(response, body);
        } catch (IOException e) {
            throw new MaestroConnectionException("Unable to execute request to Maestro at " + url, e);
        }
    }

    private TestReport handleApplicationError(HttpResponse response, String body) {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            return serDes.deserialize(body, TestReportDTO.class).getBusinessObject();
        }
        if (statusCode == 400) {
            throw new InvalidTestRunException(body != null ? body : "Invalid test run");
        }
        if (statusCode == 401) {
            throw new MaestroHttpError(statusCode, body);
        }
        if (statusCode >= 400) {
            throw new MaestroHttpError(statusCode, body);
        }
        throw new UnexpectedInternalErrorException("Unexpected response from Maestro: " + statusCode);
    }

    private TestReport handleTechnicalErrors(Execution operation) {
        try {
            return operation.run();
        } catch (MaestroHttpError e) {
            throw e;
        } catch (MaestroConnectionException e) {
            if (e.getCause() instanceof SocketTimeoutException) {
                throw new MaestroTimeoutException(
                      String.format("Maestro did not respond before timeout (connectTimeout=%d, readTimeout=%d).",
                            msConnectTimeout, msReadTimeout),
                      e);
            }
            throw e;
        }
    }

    private static String readBody(HttpResponse response) throws IOException {
        if (response.getEntity() == null) {
            return "";
        }
        try (InputStream inputStream = response.getEntity().getContent()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static CloseableHttpClient buildM2mHttpClient(String k8sIdVariableName,
                                                         int msConnectTimeout,
                                                         int msReadTimeout) {
        RequestConfig requestConfig = RequestConfig.custom()
              .setConnectTimeout(msConnectTimeout)
              .setSocketTimeout(msReadTimeout)
              .build();
        return ApacheM2MHttpClientBuilder.createWithAccessToken(k8sIdVariableName)
              .setDefaultRequestConfig(requestConfig)
              .build();
    }

    @FunctionalInterface
    private interface Execution {
        TestReport run();
    }
}
