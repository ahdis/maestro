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

package net.ihe.gazelle.maestro.xml.extraction.step.technical.factory;

import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.spi.business.Handler;
import net.ihe.gazelle.maestro.spi.business.StepExecutor;
import net.ihe.gazelle.maestro.spi.technical.StepExecutorFactory;
import net.ihe.gazelle.maestro.xml.extraction.step.business.XmlExtractorStepDefinition;
import net.ihe.gazelle.maestro.xml.extraction.step.business.XmlExtractorStepExecutor;
import net.ihe.gazelle.maestro.xml.extraction.step.technical.XmlExtractionServiceImpl;

import java.util.Map;

/**
 * Assertion Step is not implemented. It exists just for test purpose
 */
public class XmlExtractorStepExecutorFactory implements StepExecutorFactory {

   @Override
    public String getSupportedStep() {
        return XmlExtractorStepDefinition.TYPE;
    }

    @Override
    public Map<String, Class<? extends Handler>> getRequiredServices(Step step) {
        return Map.of();
    }


   @Override
   public StepExecutor createStepExecutor(Step step, Map<String, Handler> handlers) {
      return new XmlExtractorStepExecutor(new XmlExtractionServiceImpl());
   }

}
