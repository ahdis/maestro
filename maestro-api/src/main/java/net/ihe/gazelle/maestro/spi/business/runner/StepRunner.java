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

package net.ihe.gazelle.maestro.spi.business.runner;

import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;

import java.util.concurrent.CompletableFuture;

public interface StepRunner {

    /**
     * @param stepRun contain step to run
     * @return report builder with information to build report of step execution
     */
    CompletableFuture<StepRunReportBuilder> run(StepRun stepRun);

    /**
     * @return Canonical name of step that stepRunner can manage
     */
    String getSupportedStep();
}
