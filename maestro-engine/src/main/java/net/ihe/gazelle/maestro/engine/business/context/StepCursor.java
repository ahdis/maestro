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

package net.ihe.gazelle.maestro.engine.business.context;

import net.ihe.gazelle.maestro.api.business.test.Step;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Tracks iteration over the ordered list of {@link Step}.
 */
public final class StepCursor {

   private final List<Step> steps;
   private int index;

   private StepCursor(List<Step> orderedSteps) {
      this.steps = List.copyOf(orderedSteps);
      this.index = -1;
   }

   public static StepCursor empty() {
      return new StepCursor(List.of());
   }

   public static StepCursor from(List<Step> orderedSteps) {
      return new StepCursor(orderedSteps);
   }

   public boolean hasNext() {
      return index < steps.size() - 1;
   }

   public Step next() {
      if (!hasNext()) {
         throw new NoSuchElementException("No more steps to execute");
      }
      return steps.get(++index);
   }

   public boolean hasCurrent() {
      return index > -1;
   }

   public Step current() {
      if (!hasCurrent()) {
         throw new IllegalStateException("No step has been executed yet");
      }
      return steps.get(index);
   }
}

