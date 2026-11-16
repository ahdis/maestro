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

package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.maestro.api.business.MaestroSessionService;
import net.ihe.gazelle.maestro.api.business.message.DeleteEvidence;
import net.ihe.gazelle.maestro.api.business.message.PutEvidence;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayItemProperty;
import net.ihe.gazelle.maestro.engine.business.context.SessionStore;
import net.ihe.gazelle.maestro.engine.business.context.TestRunSession;

public class MaestroSessionServiceImpl implements MaestroSessionService {

    private final SessionStore<TestRunSession> sessionStore;

    private final MaestroFacade maestro;

    public MaestroSessionServiceImpl(SessionStore<TestRunSession> sessionStore) {
        this(sessionStore, null);
    }

    public MaestroSessionServiceImpl(SessionStore<TestRunSession> sessionStore, MaestroFacade maestro) {
        this.sessionStore = sessionStore;
        this.maestro = maestro;
    }

    @Override
    public void putEvidence(PutEvidence putEvidence) {
        TestRunSession session = sessionStore.getSession(putEvidence.getSessionId());
        if (session == null) {
            throw new TestReportRecordingException("Evidence session is no longer active");
        }
        if (putEvidence.getContent() == null
                || putEvidence.getContent().getName() == null
                || putEvidence.getContent().getName().isBlank()) {
            throw new TestReportRecordingException("Evidence requires a name and content");
        }
        if (putEvidence.getContent() instanceof ByteArrayItemProperty) {
            throw new TestReportRecordingException(
                    "Evidence attachment references must be created by the server");
        }
        synchronized (session) {
            session.assertAcceptingEvidence();
            var observer =
                    maestro == null
                            ? null
                            : maestro.evidenceObserver(putEvidence.getSessionId()).orElse(null);
            if (maestro != null && observer == null) {
                throw new TestReportRecordingException(
                        "Evidence recording observer is no longer active");
            }
            var accepted =
                    new PutEvidence()
                            .setSessionId(putEvidence.getSessionId())
                            .setRunId(putEvidence.getRunId())
                            .setStepName(putEvidence.getStepName())
                            .setContent(
                                    observer == null
                                            ? putEvidence.getContent().copy()
                                            : observer.recordEvidence(
                                                    putEvidence.getContent().copy()));
            session.addEvidence(accepted);
            sessionStore.putSession(putEvidence.getSessionId(), session, session.getExpirationTimestamp());
            if (observer != null) {
                observer.onEvidenceRecorded(accepted);
            }
        }
    }

    @Override
    public void deleteEvidence(DeleteEvidence deleteEvidence) {
        TestRunSession session = sessionStore.getSession(deleteEvidence.getSessionId());
        if (session == null) {
            throw new TestReportRecordingException("Evidence session is no longer active");
        }
        session.removeEvidence(deleteEvidence);
        sessionStore.putSession(deleteEvidence.getSessionId(), session, session.getExpirationTimestamp());
    }

}
