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

package net.ihe.gazelle.itb.gateway.business;

import jakarta.inject.Inject;
import net.ihe.gazelle.itb.gateway.business.reporting.ItbReporting;
import net.ihe.gazelle.itb.gateway.business.reporting.ItbResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

public class ItbReportingService {

   private static final Logger LOG = LoggerFactory.getLogger(ItbReportingService.class);

   private final ItbClient itbClient;
   private final ItbSessionStore sessionStore;

   @Inject
   public ItbReportingService(ItbClient itbClient, ItbSessionStore sessionStore) {
      this.itbClient = itbClient;
      this.sessionStore = sessionStore;
   }

   /**
    * Callback to receive ITB reporting
    *
    * @param itbReporting the ITB reporting
    * @throws NoSuchElementException if no session is found for the given itb session ID
    */
   public void receiveReporting(ItbReporting itbReporting) {
      String sessionID = itbReporting.getTestSession().getTestSessionIdentifier();
      var future = sessionStore.get(sessionID);
      if (future != null) {
         itbReporting.setResult(parseResult(itbReporting.getTestReport()));
         itbReporting.setPdfReport(
            getPdfReport(itbReporting.getTestSession().getTestSessionIdentifier(),
                  itbReporting.getTestSession().getTestCaseIdentifier())
         );
         itbReporting.setLogs(
               getLogs(itbReporting.getTestSession().getTestSessionIdentifier())
         );
         future.complete(itbReporting);
         sessionStore.remove(sessionID);
      } else {
         throw new NoSuchElementException("No session found for ID " + sessionID);
      }
   }

   private byte[] getPdfReport(final String sessionId, final String testCaseIdentifier) {
      try {
         return itbClient.requestPDFReport(sessionId, testCaseIdentifier);
      } catch (Exception e) {
         LOG.atWarn().setCause(e).log("Could not request ITB PDF for session {}", sessionId);
         return null;
      }
   }

   private String getLogs(final String sessionID) {
      try {
         return itbClient.getTestLogs(sessionID);
      } catch (Exception e) {
         LOG.atWarn().setCause(e).log("Could not retrieve ITB logs for session {}", sessionID);
         return null;
      }
   }

   private ItbResult parseResult(String itbReport) {
      try {
         DocumentBuilder builder = getDocumentBuilder();
         try(InputStream is = new ByteArrayInputStream(itbReport.getBytes(StandardCharsets.UTF_8))) {
            Document docReport = builder.parse(is);
            NodeList element = docReport.getDocumentElement().getChildNodes();
            return getResult(element);
         }
      } catch (ParserConfigurationException | IOException | SAXException e) {
         LOG.error("Unable to parse ITB report result", e);
         return ItbResult.UNDEFINED;
      }
   }

   private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      // Harden XML parsing against XXE and external entity expansion
      factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
      factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
      factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
      factory.setXIncludeAware(false);
      factory.setExpandEntityReferences(false);
      // Forbids any external access during parsing
      optionalEntityEscape(factory);
      return factory.newDocumentBuilder();
   }

   private static void optionalEntityEscape(DocumentBuilderFactory factory) {
      try {
         factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
         factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
      } catch (IllegalArgumentException ignored) {
         // Some XML parsers may not support these attributes; ignore if unsupported
      }
   }

   private static ItbResult getResult(NodeList element) {
      String status = element.item(7).getTextContent();
      if (status == null || status.isEmpty()) {
         throw new CatchItbReportServiceException("Status is null or empty in the ITB report.");
      }
      if (status.equalsIgnoreCase("SUCCESS")) {
         return ItbResult.SUCCESS;
      } else if (status.equalsIgnoreCase("FAILURE")) {
         return ItbResult.FAILURE;
      } else {
         return ItbResult.UNDEFINED;
      }
   }


}
