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

package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.maestro.api.business.test.Test;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;

import java.util.List;

/**
 * Service to record a TestReport and all its references.
 */
public interface TestReportRecordingService {

   /** Opens incremental recording with access to the validated execution inputs. */
   default ExecutionRecordingSession openSession(List<Test> definitions,
           java.util.function.Supplier<net.ihe.gazelle.security.business.acl.AccessControlList> acl,
           java.util.function.Supplier<java.util.List<net.ihe.gazelle.maestro.api.business.property.Property>> inputs) {
      return openSession(definitions, acl);
   }

   /** Opens isolated recording state; legacy implementations retain final-only recording. */
   default ExecutionRecordingSession openSession(List<Test> definitions,
           java.util.function.Supplier<net.ihe.gazelle.security.business.acl.AccessControlList> acl) {
      return new FinalReportRecordingSession(this, definitions);
   }

   /**
    * Records a TestReport and its references without resolving producer output aliases.
    * Use this for historical reports whose original test definitions are unavailable.
    *
    * @param report the TestReport to record
    * @return the location of the recorded TestReport
    * @throws TestReportRecordingException if it can't record the TestReport
    */
   default String recordTestReport(TestReport report) {
      return recordTestReport(report, List.of());
   }

   /** Records using the original test definitions to resolve producer output aliases. */
   String recordTestReport(TestReport report, List<Test> testDefinitions);

}
