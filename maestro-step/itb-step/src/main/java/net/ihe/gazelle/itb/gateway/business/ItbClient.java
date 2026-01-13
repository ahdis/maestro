/*
 * Copyright 2026 IHE International.
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
