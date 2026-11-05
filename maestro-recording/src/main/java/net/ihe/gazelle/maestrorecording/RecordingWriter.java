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

package net.ihe.gazelle.maestrorecording;

import net.ihe.gazelle.maestro.spi.business.recording.plan.FutureReferencePlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.ItemPlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.ReferencePlan;
import net.ihe.gazelle.security.business.acl.AccessControlList;

import java.util.ArrayList;
import java.util.List;

/** Per-execution writer retaining successful writes across incremental plans. */
public final class RecordingWriter {
    private final RecordItemService.History history = new RecordItemService.History();
    private final RecordItemService service;

    RecordingWriter(RecordItemService service) {
        this.service = service;
    }

    public synchronized <T> List<ReferencePlan> recordReferences(
            ItemPlan<T> plan, AccessControlList acl) {
        List<ReferencePlan> recorded = new ArrayList<>();
        for (FutureReferencePlan<T> future : plan.futureReferencePlans()) {
            String id = service.recordReferencedObject(future.referencePlan(), acl, history);
            ReferencePlan ref = future.referencePlan();
            recorded.add(
                    switch (ref.getRefType()) {
                        case ITEM_ID ->
                                ReferencePlan.forItemId(ref.getName(), ref.getTargetType(), id);
                        case ATTACHMENT ->
                                ReferencePlan.forAttachmentId(
                                        ref.getName(), ref.getTargetType(), id);
                        case URL -> ReferencePlan.forURL(ref.getName(), ref.getTargetType(), id);
                    });
            if (future.mutation() != null) {
                future.mutation()
                        .mutateReference(plan.itemObject(), future.referencePlan().getName(), id);
            }
        }
        return List.copyOf(recorded);
    }

    public synchronized <T> String record(ItemPlan<T> plan, AccessControlList acl) {
        return service.recordItemPlan(plan, acl, history);
    }

    /** Releases payloads after finalization. */
    public synchronized void close() {
        history.items.clear();
        history.attachments.clear();
        history.inProgress.clear();
    }
}
