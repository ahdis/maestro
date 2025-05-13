package net.ihe.gazelle.maestro.technical.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.HttpHeaders;
import net.ihe.gazelle.maestro.technical.dto.report.TestReportDTO;
import net.ihe.gazelle.testreport.TestReport;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This client is used to send the Test Report to TM once test set run execution ends
 * <br> It uses the callback URL and authorization token stored
 */
@ApplicationScoped
public class TestReportClient {

    private static final Logger LOG = LoggerFactory.getLogger(TestReportClient.class);

    public void sendTestReport(String callBackAuthorization, String callBackUrl, TestReport testReport) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(callBackUrl);
            httpPost.setHeader(HttpHeaders.AUTHORIZATION, callBackAuthorization);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(new TestReportDTO(testReport));

            HttpEntity multipartEntity = MultipartEntityBuilder.create()
                    .addTextBody("json report", json, ContentType.APPLICATION_JSON)
                    .build();

            httpPost.setEntity(multipartEntity);

            client.execute(httpPost, new BasicHttpClientResponseHandler());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

}
