package net.ihe.gazelle.maestro.technical.ws.tmintegration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import net.ihe.gazelle.maestro.technical.dto.report.TestReportDTO;
import net.ihe.gazelle.testreport.TestReport;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * This client is used to send the Test Report to TM once test set run execution ends
 * <br> It uses the callback URL and authorization token stored
 */
@ApplicationScoped
public class TMTestReportClient {

    public void sendTestReport(String callBackAuthorization, String callBackUrl, TestReport testReport) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(callBackUrl);

            // Set headers
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
            httpPost.setHeader(HttpHeaders.AUTHORIZATION, callBackAuthorization);

            // Convert DTO to JSON
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(new TestReportDTO(testReport));

            // Set JSON as request body
            httpPost.setEntity(new StringEntity(json));

            // Execute the request
            client.execute(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
