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
import net.ihe.gazelle.maestro.api.technical.event.StepRunFinished;
import net.ihe.gazelle.maestro.api.technical.event.StepRunStarted;
import net.ihe.gazelle.maestro.api.technical.event.TestRunFinished;
import net.ihe.gazelle.maestro.api.technical.event.TestRunStarted;
import net.ihe.gazelle.maestro.api.technical.event.TestSuiteFinished;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.EntityBuilder;
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
public class ApacheTestReportClient implements MaestroCaller {

   private static final Logger LOG = LoggerFactory.getLogger(ApacheTestReportClient.class);

   String callBackAuthorization;
   String callBackUrl;

   public ApacheTestReportClient(String callBackUrl) {
      this(null, callBackUrl);
   }

   public ApacheTestReportClient(String callBackAuthorization, String callBackUrl) {
      this.callBackAuthorization = callBackAuthorization;
      this.callBackUrl = callBackUrl;
   }

   @Override
   public void onTestSuiteFinished(TestSuiteFinished testSuiteFinished) {
      sendTestReport(testSuiteFinished.reportSummary());
   }

   private void sendTestReport(TestReportSummary testReportSummary) {
      try (CloseableHttpClient client = HttpClients.createDefault()) {
         HttpPost httpPost = new HttpPost(callBackUrl);
         if (callBackAuthorization != null) {
            httpPost.setHeader(HttpHeaders.AUTHORIZATION, callBackAuthorization);
         }

         ObjectMapper mapper = new ObjectMapper();
         String json = mapper.writeValueAsString(new TestReportSummaryDTO(testReportSummary));

         httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
         try (HttpEntity entity = EntityBuilder.create()
               .setText(json)
               .build()) {
            httpPost.setEntity(entity);
            client.execute(httpPost, new BasicHttpClientResponseHandler());
         }
      } catch (Exception e) {
         LOG.error(e.getMessage(), e);
      }
   }

}
