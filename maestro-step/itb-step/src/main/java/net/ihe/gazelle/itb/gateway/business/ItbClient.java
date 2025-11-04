package net.ihe.gazelle.itb.gateway.business;

import net.ihe.gazelle.itb.gateway.business.request.TestRequest;

public interface ItbClient {
   /**
    * Method to execute test request
    *
    * @param testRequest request to send
    * @return SessionId of the started ITB test session
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
