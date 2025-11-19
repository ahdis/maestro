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

package technical.userinteract;

import net.ihe.gazelle.lang.ExecutionRuntimeException;
import net.ihe.gazelle.maestro.api.business.MaestroObserver;
import net.ihe.gazelle.maestro.api.business.UserInteractionHandler;
import net.ihe.gazelle.maestro.api.business.message.InteractWithUser;
import net.ihe.gazelle.maestro.api.business.message.UserInteractionCompleted;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class UserInteractionHandlerImpl implements UserInteractionHandler {

    private final MaestroObserver observer;

    public UserInteractionHandlerImpl(MaestroObserver observer) {
        this.observer = observer;
    }

    @Override
    public void displayMessage(String interactionTitle, String message) {
        CompletableFuture<UserInteractionCompleted> future = observer.interactWithUser(new InteractWithUser(interactionTitle, message));

        try {
            //TODO : handle timeout here
            future.get();
        } catch (ExecutionException e) {
            throw new ExecutionRuntimeException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

}
