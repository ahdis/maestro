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

package net.ihe.gazelle.maestrorecording.adapter;

import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.spi.business.recording.StepOutputDissector;
import net.ihe.gazelle.maestro.spi.business.recording.StepOutputDissectorFactory;

import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;

/**
 * Default provider backed by {@link ServiceLoader}. It inspects available
 * {@link StepOutputDissectorFactory} implementations and returns the first one
 * that claims support for the provided step. When no factory matches, the
 * legacy attachment-only dissector is returned.
 */
public class SPIStepOutputDissectorProvider implements StepOutputDissectorProvider {

   private final List<StepOutputDissectorFactory> factories;

   /**
    * Default constructor
    */
   public SPIStepOutputDissectorProvider() {
      this(loadFactories());
   }

   SPIStepOutputDissectorProvider(List<StepOutputDissectorFactory> dissectorFactories) {
      this.factories = List.copyOf(Objects.requireNonNull(dissectorFactories, "dissector factories must not be null"));
   }

   @Override
   public StepOutputDissector getDissector(StepRunReport stepRunReport) {
      return factories.stream()
            .filter(factory -> factory.supports(stepRunReport.getType()))
            .findFirst()
            .map(StepOutputDissectorFactory::create)
            .orElse(null);
   }

   private static List<StepOutputDissectorFactory> loadFactories() {
      ServiceLoader<StepOutputDissectorFactory> loader = ServiceLoader.load(StepOutputDissectorFactory.class);
      return loader.stream()
            .map(ServiceLoader.Provider::get)
            .toList();
   }
}
