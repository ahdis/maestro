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

package net.ihe.gazelle.maestro.api.business.userinteract;

import net.ihe.gazelle.maestro.api.business.message.GradingCompleted;
import net.ihe.gazelle.maestro.spi.business.Handler;

import java.util.List;

/**
 * The grading handler is used to ask additional test grading manually.
 */
public interface GradingHandler extends Handler {

    /**
     * The name of the service associated with grading
     */
    String SERVICE_NAME = "Grading Service";

    /**
     * The name of the interface of the grading
     */
    String INTERFACE_NAME = "Grading API";

    /**
     * Displays a message during the grading process.
     *
     * @param criteria the criteria associated with this message
     * @return an instance of {@code GradingCompleted} representing the result of the grading
     */
    default GradingCompleted displayMessage(List<String> criteria, long timeout) {
        return null;
    }
}
