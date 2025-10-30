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

import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.property.PropertyResolver;
import net.ihe.gazelle.maestro.api.business.test.Step;

import java.io.Serial;
import java.util.List;

/**
 * StepRun is the execution of a Step with the test context to be able to resolve reference-properties.
 */
public class StepRun extends Step {

   @Serial
   private static final long serialVersionUID = 6529685098267757690L;

   public StepRun(Step step, List<Property> context) {
      super(step);
      // we copy the context properties to avoid shared references
      super.addProperties(context.stream().map(Property::copy).toList());
      // Now we can resolve all properties for this step run only.
      PropertyResolver.resolveProperties(this.properties.values());
   }

}
