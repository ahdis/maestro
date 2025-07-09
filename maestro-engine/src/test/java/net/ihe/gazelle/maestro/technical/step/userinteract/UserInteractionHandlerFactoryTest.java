/*
 * Copyright 2025 IHE International.
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

package net.ihe.gazelle.maestro.technical.step.userinteract;

import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.maestro.business.step.userinteract.UserInteractionHandler;
import net.ihe.gazelle.maestro.business.step.userinteract.UserInteractionHandlerImpl;
import net.ihe.gazelle.servicemetadata.api.business.ConsumedInterface;
import net.ihe.gazelle.servicemetadata.api.business.ProvidedInterface;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.wildfly.common.Assert.assertNotNull;
import static org.wildfly.common.Assert.assertTrue;

class UserInteractionHandlerFactoryTest {

    private final UserInteractionHandlerFactory factory = new UserInteractionHandlerFactory();

    @Test
    void should_be_type_simulation_handler() {
        assertEquals(UserInteractionHandlerImpl.class, factory.getHandlerType());
    }

    @Test
    void should_create_simulation_handler() {
        ProvidedInterface providedInterface = new ProvidedInterface();
        providedInterface.setInterfaceVersion("1.0.0");
        providedInterface.setInterfaceName("test");
        Handler handler = factory.createHandler(providedInterface, null);
        assertNotNull(handler);
        assertTrue(handler instanceof UserInteractionHandlerImpl);
    }

    @Test
    void should_get_consumed_interface() {
        ConsumedInterface consumedInterface = factory.getConsumedInterface();
        assertNotNull(consumedInterface);
        assertEquals(UserInteractionHandler.INTERFACE_NAME, consumedInterface.getInterfaceName());
    }
}
