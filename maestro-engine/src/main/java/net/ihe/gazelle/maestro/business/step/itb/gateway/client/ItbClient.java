package net.ihe.gazelle.maestro.business.step.itb.gateway.client;

import net.ihe.gazelle.maestro.business.step.itb.gateway.model.request.TestRequest;

public interface ItbClient {

    /**
     * Method to execute test request
     *
     * @param testRequest request to send
     * @return json response from test service call
     */
    String startTest(TestRequest testRequest);

    /**
     * Method to retrieve ITB test session logs
     *
     * @param sessionID ITB test session identifier
     * @return ITB test session logs
     */
    String getTestLogs(String sessionID);

    /**
     * Method to retrieve ITB test session logs
     *
     * @param sessionID ITB test session identifier
     * @return ITB test session logs
     */
    byte[] requestPDFReport(String sessionID, String testCaseName);
}
