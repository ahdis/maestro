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

package net.ihe.gazelle.maestro.itb.step.business;

import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.test.SupportedInput;
import net.ihe.gazelle.maestro.api.business.test.SupportedTextInput;
import net.ihe.gazelle.maestro.spi.business.StepDefinition;

import java.io.Serial;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * An ITB Step is an automated step used to execute a test in ITB. This step has 7 properties.
 */
public class ItbStepDefinition implements StepDefinition {

   public static final String TYPE = "ITB";

   // Inputs
   public static final String TEST_CASE = "testCase";
   public static final String ACTOR_ID = "actorID";

   // outputs
   public static final String XML_REPORT = "xmlReport";
   public static final String PDF_REPORT = "pdfReport";
   public static final String LOGS = "logs";

   @Serial
   private static final long serialVersionUID = 4790241684818825787L;

   @Override
   public String getType() {
      return TYPE;
   }

   @Override
   public List<SupportedInput> getSupportedInputs() {
      return List.of(
            new SupportedTextInput().setId(TEST_CASE).setLabel("Test Case").setRequired(true),
            new SupportedTextInput().setId(ACTOR_ID).setLabel("Actor ID").setRequired(true)
      );
   }

   @Override
   public Map<String, Class<? extends Property>> getOutputsDefinition() {
      return Map.of(
            XML_REPORT, ByteArrayProperty.class,
            PDF_REPORT, ByteArrayProperty.class,
            LOGS, StringProperty.class
      );
   }

}
