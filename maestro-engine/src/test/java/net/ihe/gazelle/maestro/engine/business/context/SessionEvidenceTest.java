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

package net.ihe.gazelle.maestro.engine.business.context;

import net.ihe.gazelle.maestro.api.business.message.*;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.engine.business.MaestroSessionServiceImpl;
import org.junit.jupiter.api.Test;
import technical.dao.SessionStoreCache;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SessionEvidenceTest {
    private PutEvidence evidence(int run, String step, String name) {
        return new PutEvidence().setSessionId("execution").setRunId(run).setStepName(step)
                .setContent(new StringProperty(name, "reviewed"));
    }

    @Test
    void removesOnlyEvidenceMatchingRunStepAndName() {
        var session = new TestRunSession();
        var store = new SessionStoreCache<TestRunSession>();
        store.putSession("execution", session, 10000);
        var service = new MaestroSessionServiceImpl(store);
        var matching = evidence(0, "first", "note");
        service.putEvidence(matching);
        service.putEvidence(evidence(1, "first", "note"));
        service.putEvidence(evidence(0, "second", "note"));
        service.putEvidence(evidence(0, "first", "other"));
        var deletion = new DeleteEvidence().setSessionId("execution").setRunId(0)
                .setStepName("first").setEvidenceName("note");
        var restored = new DeleteEvidence().setSessionId("execution").setRunId(0)
                .setStepName("first").setEvidenceName("note");
        assertEquals(deletion, restored);
        assertEquals(deletion.hashCode(), restored.hashCode());
        assertNotEquals(null, deletion);
        assertNotEquals(deletion, new DeleteEvidence().setSessionId("other"));
        service.deleteEvidence(restored);
        assertEquals(3, session.getEvidences().size());
        assertFalse(session.getEvidences().contains(matching));
        session.getEvidences().clear();
        assertEquals(3, session.getEvidences().size());
    }

    @Test
    void restoredSessionKeepsPositionAndDefensivelyCopiesCollections() {
        var first = new Step().setName("first");
        var second = new Step().setName("second");
        var run = new TestRun().setTest(new net.ihe.gazelle.maestro.api.business.test.Test()
                .setSteps(List.of(first, second)));
        var session = new TestRunSession().setTestRun(run).setTestSessionId("qualification")
                .setContext(List.of(new StringProperty("patient", "123")))
                .setStepCursor(0).setStepReports(List.of(new StepRunReport().setStepName("first")))
                .setEvidences(List.of(evidence(0, "first", "note")));
        assertSame(run, session.getTestRun());
        assertEquals("qualification", session.getTestSessionId());
        assertEquals(0, session.currentStepIndex());
        assertSame(first, session.currentStep());
        session.getContext().clear();
        session.getStepReports().clear();
        assertEquals(1, session.getContext().size());
        assertEquals(1, session.getStepReports().size());
        session.setContext(null).setStepReports(null).setEvidences(null);
        assertEquals(1, session.getContext().size());
        assertEquals(1, session.getStepReports().size());
        assertEquals(1, session.getEvidences().size());
        assertSame(second, session.nextStep());
        assertFalse(session.hasNextStep());
        assertThrows(java.util.NoSuchElementException.class, session::nextStep);
        var empty = StepCursor.from(List.of());
        assertFalse(empty.hasCurrent());
        assertThrows(IllegalStateException.class, empty::current);
    }
}
