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

package net.ihe.gazelle.maestro.quarkus.ws;

/**
 * Defines constants used for open API examples
 */
public class Constants {

   private Constants() {
   }

   /**
    * Test suite run example
    */
   static final String TEST_SUITE_RUN = """
          {
            "testSuite": {
              "id": "testSuite1",
              "name": "test suite name",
              "testReferences":[
                {
                  "testId": "test1"
                }
              ]
            },
            "accessControlList": {
              "owners": [
                "gazelle",
                "tm-integration"
              ],
              "isPublic": true
            },
            "tests": [
              {
                "id": "test1",
                "name": "test name",
                "steps": [
                  {
                    "name": "step1",
                    "type": "ASSERT_EQUALS",
                    "properties": [
                      {
                        "name": "expected",
                        "value": "test",
                        "type": "STRING"
                      },
                      {
                        "name": "actual",
                        "value": "test",
                        "type": "STRING"
                      }
                    ]
                  }
                ]
              }
            ]
          }
         """;

   /**
    * Test run example
    */
   static final String TEST_RUN = """
         {
           "test": {
             "id": "test1",
             "name": "test name",
             "steps": [
               {
                 "name": "step1",
                 "type": "ASSERT_EQUALS",
                 "properties": [
                   {
                     "name": "expected",
                     "value": "test",
                     "type": "STRING"
                   },
                   {
                     "name": "actual",
                     "value": "test",
                     "type": "STRING"
                   }
                 ]
               }
             ]
           },
           "accessControlList": {
             "isPublic": true
           }
         }
         """;
}
