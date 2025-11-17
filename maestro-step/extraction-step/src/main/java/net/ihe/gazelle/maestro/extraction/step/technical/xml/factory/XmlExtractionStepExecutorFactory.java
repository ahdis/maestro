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

package net.ihe.gazelle.maestro.extraction.step.technical.xml.factory;

import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.spi.business.Handler;
import net.ihe.gazelle.maestro.spi.business.StepExecutor;
import net.ihe.gazelle.maestro.spi.technical.StepExecutorFactory;
import net.ihe.gazelle.maestro.extraction.step.business.xml.XmlExtractionStepDefinition;
import net.ihe.gazelle.maestro.extraction.step.business.xml.XmlExtractionStepExecutor;
import net.ihe.gazelle.maestro.extraction.step.technical.xml.XmlExtractionServiceImpl;

import java.util.Map;

/**
 * Factory to create XmlExtractionStepExecutor
 */
public class XmlExtractionStepExecutorFactory implements StepExecutorFactory {

   @Override
    public String getSupportedStep() {
        return XmlExtractionStepDefinition.TYPE;
    }

    @Override
    public Map<String, Class<? extends Handler>> getRequiredServices(Step step) {
        return Map.of();
    }


   @Override
   public StepExecutor createStepExecutor(Step step, Map<String, Handler> handlers) {
      return new XmlExtractionStepExecutor(new XmlExtractionServiceImpl());
   }

}
