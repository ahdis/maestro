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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import com.kereval.gazelle.datahouse.api.business.record.Item;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStepDefinition;

final class WorkflowRecordItemClient extends RecordItemClientMock {
    final CountDownLatch writing = new CountDownLatch(1);
    final CountDownLatch release = new CountDownLatch(1);
    boolean fail;
    int validationWrites;
    @Override public String recordItem(Item item) {
        if (ValidationStepDefinition.REPORT.equals(item.getType())) {
            validationWrites++;
            writing.countDown();
            try {
                if (!release.await(10, TimeUnit.SECONDS)) throw new IllegalStateException("Test write gate timed out");
            } catch (InterruptedException error) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(error);
            }
            if (fail) throw new IllegalStateException("Simulated storage failure");
        }
        return super.recordItem(item);
    }
}
