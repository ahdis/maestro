/*
 * Copyright 2025-2026 IHE International.
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

import net.ihe.gazelle.maestro.api.business.message.UserInteractionCompleted;
import net.ihe.gazelle.maestro.api.business.test.SupportedInput;
import net.ihe.gazelle.maestro.spi.business.Handler;

import java.util.List;

/**
 * The user interaction handler is used to ask additional actions to the user during test execution
 */
public interface UserInteractionHandler extends Handler {

   /**
    * The name of the service associated with user interaction
    */
   String SERVICE_NAME = "User Interaction Service";

   /**
    * The name of the interface of the user interaction
    */
   String INTERFACE_NAME = "User Interaction API";

   /**
    * Displays a message to the user during test execution.
    *
    * @param interactionTitle the title of the interaction
    * @param message the message to display
    * @param timeout the maximum time to wait for user interaction, in milliseconds
    * @return the result of the user interaction
    */
   default UserInteractionCompleted displayMessage(String interactionTitle, String message, long timeout) {
      return null;
   }

   /**
    * Displays a message to the user during test execution.
    *
    * @param interactionTitle the title of the interaction
    * @param message the message to display
    * @param timeout the maximum time to wait for user interaction, in milliseconds
    * @param supportedInputs the list of supported inputs
    * @return the result of the user interaction
    */
   default UserInteractionCompleted displayMessage(String interactionTitle, String message, long timeout, List<SupportedInput> supportedInputs) {
      return displayMessage(interactionTitle, message, timeout);
   }
}
