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
import net.ihe.gazelle.maestro.spi.business.recording.StepOutputProjection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SPIStepOutputDissectorProviderTest {

   @Test
   void shouldReturnDissectorFromFirstSupportingFactory() {
      RecordingFactory unsupportedFactory = new RecordingFactory("UNSUPPORTED");
      RecordingFactory supportedFactory = new RecordingFactory("SIMULATION");
      SPIStepOutputDissectorProvider provider = new SPIStepOutputDissectorProvider(
            List.of(unsupportedFactory, supportedFactory));

      StepRunReport step = new StepRunReport().setType("SIMULATION");
      StepOutputDissector dissector = provider.getDissector(step);

      assertThat(dissector).isSameAs(supportedFactory.createdDissector);
      assertThat(unsupportedFactory.createCalled).isFalse();
      assertThat(supportedFactory.createCalled).isTrue();
   }

   @Test
   void shouldReturnNullWhenNoFactorySupportsStep() {
      RecordingFactory factory = new RecordingFactory("VALIDATION");
      SPIStepOutputDissectorProvider provider = new SPIStepOutputDissectorProvider(List.of(factory));

      StepOutputDissector dissector = provider.getDissector(new StepRunReport().setType("SIMULATION"));

      assertThat(dissector).isNull();
      assertThat(factory.createCalled).isFalse();
   }

   private static final class RecordingFactory implements StepOutputDissectorFactory {
      private final String supportedType;
      private final StepOutputDissector createdDissector = context -> StepOutputProjection.empty();
      private boolean createCalled;

      private RecordingFactory(String supportedType) {
         this.supportedType = supportedType;
      }

      @Override
      public boolean supports(final String stepType) {
         return supportedType.equals(stepType);
      }

      @Override
      public StepOutputDissector create() {
         createCalled = true;
         return createdDissector;
      }
   }
}
