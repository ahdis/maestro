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

package net.ihe.gazelle.maestro.validation.step.technical.factory;

import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.maestro.validation.step.business.ServiceClientException;
import net.ihe.gazelle.maestro.validation.step.technical.handler.GazelleValidationServiceHandler;
import net.ihe.gazelle.servicemetadata.api.business.ConsumedInterface;
import net.ihe.gazelle.servicemetadata.api.business.HttpRestBinding;
import net.ihe.gazelle.servicemetadata.api.business.ProvidedInterface;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.wildfly.common.Assert.assertNotNull;

class GazelleValidationServiceHandlerFactoryTest {

    private final GazelleValidationServiceHandlerFactory factory = new GazelleValidationServiceHandlerFactory();

    @Test
    void should_be_type_validation_handler() {
        assertEquals(GazelleValidationServiceHandler.class, factory.getHandlerType());
    }

    @Test
    void should_get_consumed_interface() {
        ConsumedInterface consumedInterface = factory.getConsumedInterface();
        assertNotNull(consumedInterface);
        assertEquals("Validation Service", consumedInterface.getInterfaceName());
//        assertEquals(ValidationServiceController.INTERFACE_NAME, consumedInterface.getInterfaceName());
    }

    @Test
    void should_create_validation_handler() {
        HttpRestBinding binding = new HttpRestBinding();
        binding.setServiceUrl("http://localhost:80");
        ProvidedInterface providedInterface = new ProvidedInterface();
        providedInterface.addBinding(binding);
        providedInterface.setInterfaceVersion("1.0.0");
        Handler handler = factory.createHandler(providedInterface, null);
        assertNotNull(handler);
    }

    @Test
    void no_binding_simulation_handler_throws_exception() {
        ProvidedInterface providedInterface = new ProvidedInterface();
        assertThrows(NoSuchElementException.class,() -> factory.createHandler(providedInterface, null));
    }

    @Test
    void bad_uri_syntax_simulation_handler_throws_exception() {
        HttpRestBinding binding = new HttpRestBinding();
        binding.setServiceUrl("bad uri");
        ProvidedInterface providedInterface = new ProvidedInterface();
        providedInterface.addBinding(binding);
        assertThrows(ServiceClientException.class,() -> factory.createHandler(providedInterface, null));
    }

}
