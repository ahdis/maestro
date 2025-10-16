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

package net.ihe.gazelle.maestro.itb.step.technical.gateway.service.catchitbreport;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.testreport.Output;
import net.ihe.gazelle.maestro.api.business.testreport.OutputBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.itb.step.technical.gateway.client.ItbHttpClient;
import net.ihe.gazelle.maestro.itb.step.technical.gateway.dto.ItbTriggerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Dependent
@Default
public class CatchItbReportService {

    private static final Logger log = LoggerFactory.getLogger(CatchItbReportService.class);
    public static final String PDF_ITB_VALIDATION_REPORT = "PDF_ITB_Validation_Report";
    public static final String XML_ITB_VALIDATION_REPORT = "XML_ITB_Validation_Report";
    private final ItbHttpClient itbHttpClient;

    @Inject
    public CatchItbReportService(ItbHttpClient itbHttpClient) {
        this.itbHttpClient = itbHttpClient;
    }

    /**
     * Method to build a StepRunReportBuilder from an ItbTriggerDTO
     *
     * @param itbTriggerDTO the DTO containing the test report and session information
     * @return a StepRunReportBuilder populated with the parsed report data
     * @throws CatchItbReportServiceException if there is an error parsing the report
     */
    public StepRunReportBuilder buildItbReport(ItbTriggerDTO itbTriggerDTO) {
        try {
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
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(itbTriggerDTO.getTestReport()));
            Document docReport = builder.parse(is);

            NodeList element = docReport.getDocumentElement().getChildNodes();

            StepRunReportBuilder testReport = new StepRunReportBuilder();
            testReport.setType("ITB");
            testReport.setStepId(itbTriggerDTO.getTestSession().getTestCaseIdentifier());
            testReport.setOutputs(buildOutputs(itbTriggerDTO));
            testReport.setResult(getResult(element));
            return testReport;
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new CatchItbReportServiceException("Error while parsing ITB report: " + e.getMessage(), e);
        }
    }

    private static void optionalEntityEscape(DocumentBuilderFactory factory) {
        try {
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        } catch (IllegalArgumentException ignored) {
            // Some XML parsers may not support these attributes; ignore if unsupported
        }
    }

    private static StepResult getResult(NodeList element) {
        String status = element.item(7).getTextContent();
        if (status == null || status.isEmpty()) {
            throw new CatchItbReportServiceException("Status is null or empty in the ITB report.");
        }
        if (status.equalsIgnoreCase("SUCCESS")) {
            return StepResult.PASSED;
        } else if (status.equalsIgnoreCase("FAILURE")) {
            return StepResult.FAILED;
        } else {
            return StepResult.UNDEFINED;
        }
    }

    private List<OutputBuilder> buildOutputs(ItbTriggerDTO itbTriggerDTO) {
        List<OutputBuilder> outputs = new ArrayList<>();
        outputs.add(extractXmlFromReport(itbTriggerDTO));

        try {
            String logs = itbHttpClient.getTestLogs(itbTriggerDTO.getTestSession().getTestSessionIdentifier());
            outputs.add(new OutputBuilder()
                    .setName("logs")
                    .setContent(logs.getBytes())
                    .setFormat(Output.FORMAT_TEXT)
            );
        } catch (Exception e) {
            log.warn("Could not retrieve logs for session {}: {}", itbTriggerDTO.getTestSession().getTestSessionIdentifier(), e.getMessage());
        }

        try {
            byte[] pdf = itbHttpClient.requestPDFReport(itbTriggerDTO.getTestSession().getTestSessionIdentifier(), itbTriggerDTO.getTestSession().getTestCaseIdentifier());
            outputs.add(new OutputBuilder()
                    .setName(PDF_ITB_VALIDATION_REPORT)
                    .setContent(pdf)
                    .setFormat(Output.FORMAT_PDF)
            );
        } catch (Exception e) {
            log.warn("Could not request pdf for session {}: {}", itbTriggerDTO.getTestSession().getTestSessionIdentifier(), e.getMessage());
        }

        return outputs;
    }

    private OutputBuilder extractXmlFromReport(ItbTriggerDTO itbTriggerDTO) {
        return new OutputBuilder()
                .setName(XML_ITB_VALIDATION_REPORT)
                .setContent(itbTriggerDTO.getTestReport().getBytes())
                .setFormat(Output.FORMAT_XML)
                ;
    }
}
