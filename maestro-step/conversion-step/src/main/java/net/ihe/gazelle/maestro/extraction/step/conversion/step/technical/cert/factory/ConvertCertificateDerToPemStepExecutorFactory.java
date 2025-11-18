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

package net.ihe.gazelle.maestro.extraction.step.conversion.step.technical.cert.factory;

import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.extraction.step.conversion.step.business.base64.encode.EncodeBase64StepDefinition;
import net.ihe.gazelle.maestro.extraction.step.conversion.step.business.base64.encode.EncodeBase64StepExecutor;
import net.ihe.gazelle.maestro.extraction.step.conversion.step.business.cert.pem.ConvertCertificateDerToPemStepDefinition;
import net.ihe.gazelle.maestro.extraction.step.conversion.step.business.cert.pem.ConvertCertificateDerToPemStepExecutor;
import net.ihe.gazelle.maestro.extraction.step.conversion.step.business.cert.pem.PemConverterService;
import net.ihe.gazelle.maestro.extraction.step.conversion.step.technical.cert.PemConverterServiceImpl;
import net.ihe.gazelle.maestro.spi.business.Handler;
import net.ihe.gazelle.maestro.spi.business.StepExecutor;
import net.ihe.gazelle.maestro.spi.technical.StepExecutorFactory;

import java.util.Map;

/**
 * Factory to create ConvertCertificateDerToPemStepExecutor
 */
public class ConvertCertificateDerToPemStepExecutorFactory implements StepExecutorFactory {

    @Override
    public String getSupportedStep() {
        return ConvertCertificateDerToPemStepDefinition.TYPE;
    }

    @Override
    public Map<String, Class<? extends Handler>> getRequiredServices(Step step) {
        return Map.of();
    }


    @Override
    public StepExecutor createStepExecutor(Step step, Map<String, Handler> handlers) {
        PemConverterService pemConverterService = new PemConverterServiceImpl();
        return new ConvertCertificateDerToPemStepExecutor(pemConverterService);
    }

}
