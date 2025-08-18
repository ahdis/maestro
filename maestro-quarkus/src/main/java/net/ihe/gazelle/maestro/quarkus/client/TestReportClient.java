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

package net.ihe.gazelle.maestro.quarkus.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.HttpHeaders;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportSummary;
import net.ihe.gazelle.maestro.api.technical.dto.report.TestReportSummaryDTO;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This client is used to send the Test Report to TM once test set run execution ends
 * <br> It uses the callback URL and authorization token stored
 */
@ApplicationScoped
public class TestReportClient {

    private static final Logger LOG = LoggerFactory.getLogger(TestReportClient.class);

    @ConfigProperty(name = "gzl.datahouse.url")
    String dataHouseUrl;

    public void sendTestReport(String callBackAuthorization, String callBackUrl, TestReportSummary testReportSummary) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(callBackUrl);
            httpPost.setHeader(HttpHeaders.AUTHORIZATION, callBackAuthorization);

            ObjectMapper mapper = new ObjectMapper();
            String locationPrefix = dataHouseUrl + "/items/";
            String json = mapper.writeValueAsString(new TestReportSummaryDTO(testReportSummary, locationPrefix));

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
