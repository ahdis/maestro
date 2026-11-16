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

package net.ihe.gazelle.maestro.api.business;

import net.ihe.gazelle.maestro.api.business.message.DeleteEvidence;
import net.ihe.gazelle.maestro.api.business.message.PutEvidence;

/**
 * Defines operations for managing evidence associated with Maestro test run sessions.
 * <p>
 * Implementations handle commands that add or remove evidence for a specific session,
 * run, and step.
 */
public interface MaestroSessionService {

    /**
     * Adds evidence to a Maestro test run session for the specified run and step.
     *
     * @param putEvidence the command containing the session identifier, run identifier, step name, and evidence content to add
     */
    void putEvidence(PutEvidence putEvidence);

    /**
     * Deletes evidence associated with the specified Maestro session, run, and step.
     *
     * @param deleteEvidence the command containing the session identifier, run identifier, step name, and evidence name to delete
     */
    void deleteEvidence(DeleteEvidence deleteEvidence);
}
