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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.testreport.ItemBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;
import net.ihe.gazelle.maestro.api.business.testrun.MaestroEventProducer;
import net.ihe.gazelle.maestro.itb.step.technical.gateway.client.ItbHttpClient;
import net.ihe.gazelle.maestro.itb.step.technical.gateway.dto.ItbTriggerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
    private final ItbHttpClient itbHttpClient;
    private final MaestroEventProducer maestroProducer;

    @Inject
    public CatchItbReportService(MaestroEventProducer maestroProducer, ItbHttpClient itbHttpClient) {
        this.itbHttpClient = itbHttpClient;
        this.maestroProducer = maestroProducer;
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

    /**
     * Method to push report to the messaging channel
     */
    public void pushReport(StepRun stepRun, StepRunReportBuilder stepRunReportBuilder) {
        maestroProducer.returnStepReport(stepRun, stepRunReportBuilder);
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

    private List<ItemBuilder> buildOutputs(ItbTriggerDTO itbTriggerDTO) {
        List<ItemBuilder> outputs = new ArrayList<>();
        outputs.add(extractXmlFromReport(itbTriggerDTO));

        try {
            String logs = itbHttpClient.getTestLogs(itbTriggerDTO.getTestSession().getTestSessionIdentifier());
            outputs.add(new ItemBuilder().setName("LOGS").setContent(wrapDataInJson(logs)));
        } catch (Exception e) {
            log.warn("Could not retrieve logs for session {}: {}", itbTriggerDTO.getTestSession().getTestSessionIdentifier(), e.getMessage());
        }

        try {
            byte[] pdf = itbHttpClient.requestPDFReport(itbTriggerDTO.getTestSession().getTestSessionIdentifier(), itbTriggerDTO.getTestSession().getTestCaseIdentifier());
            String encodedPDF = java.util.Base64.getEncoder().encodeToString(pdf);
            outputs.add(new ItemBuilder().setName("PDF").setContent(wrapDataInJson(encodedPDF)));
        } catch (Exception e) {
            log.warn("Could not request pdf for session {}: {}", itbTriggerDTO.getTestSession().getTestSessionIdentifier(), e.getMessage());
        }

        return outputs;
    }

    private ItemBuilder extractXmlFromReport(ItbTriggerDTO itbTriggerDTO) {
        String encodedPDF = java.util.Base64.getEncoder().encodeToString(itbTriggerDTO.getTestReport().getBytes());
        return new ItemBuilder().setName("RAW_XML").setContent(wrapDataInJson(encodedPDF));
    }

    /**
     * We need to wrap the XML content in a JSON object to be compatible with the datahouse persistence service
     * @param data the data to wrap
     * @return the wrapped data as a byte array
     */
    private byte[] wrapDataInJson(String data) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("content", data);
        return node.toString().getBytes();
    }
}
