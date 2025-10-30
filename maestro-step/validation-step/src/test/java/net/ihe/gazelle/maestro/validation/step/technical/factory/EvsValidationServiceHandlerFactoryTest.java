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

import net.ihe.gazelle.maestro.spi.business.runner.Handler;
import net.ihe.gazelle.maestro.validation.step.technical.handler.EvsValidationHandler;
import net.ihe.gazelle.servicemetadata.api.business.ConsumedInterface;
import net.ihe.gazelle.servicemetadata.api.business.HttpRestBinding;
import net.ihe.gazelle.servicemetadata.api.business.ProvidedInterface;
import net.ihe.gazelle.servicemetadata.api.business.WebBinding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EvsValidationServiceHandlerFactoryTest {

    private EvsValidationServiceHandlerFactory factory;

    @BeforeEach
    void setUp() {
        factory = new EvsValidationServiceHandlerFactory();
    }

    @Test
    void should_be_type_validation_handler() {
        assertEquals(EvsValidationHandler.class, factory.getHandlerType());
    }

    @Test
    void should_get_consumed_interface() {
        List<ConsumedInterface> consumedInterfaces = factory.getConsumedInterfaces();
        assertEquals(1, consumedInterfaces.size());
        ConsumedInterface consumedInterfaceFirst = consumedInterfaces.getFirst();
        assertNotNull(consumedInterfaceFirst);
        assertEquals("EVS Client API", consumedInterfaceFirst.getInterfaceName());
        assertEquals("REST", consumedInterfaceFirst.getSupportedBindings().getFirst());
    }

    @Test
    void should_create_validation_handler() {
           // Mock ConfigProvider et Config

            HttpRestBinding binding = new HttpRestBinding();
            binding.setServiceUrl("http://localhost:80");
            ProvidedInterface providedInterface = new ProvidedInterface();
            providedInterface.addBinding(binding);
            providedInterface.setInterfaceVersion("1.0.0");
            Handler handler = factory.createHandler(providedInterface, null);
            assertNotNull(handler);

    }


    @Test
    void should_handle_multiple_bindings_in_provided_interface() {

            HttpRestBinding binding1 = new HttpRestBinding();
            binding1.setServiceUrl("http://localhost:80");
            HttpRestBinding binding2 = new HttpRestBinding();
            binding2.setServiceUrl("http://localhost:8080");

            ProvidedInterface providedInterface = new ProvidedInterface();
            providedInterface.addBinding(binding1);
            providedInterface.addBinding(binding2);
            providedInterface.setInterfaceVersion("1.0.0");

            Handler handler = factory.createHandler(providedInterface, null);
            assertNotNull(handler);

    }

    @Test
    void should_create_handler_when_config_value_is_null() {


            HttpRestBinding binding = new HttpRestBinding();
            binding.setServiceUrl("http://localhost:80");
            ProvidedInterface providedInterface = new ProvidedInterface();
            providedInterface.addBinding(binding);
            providedInterface.setInterfaceVersion("1.0.0");
            assertNotNull(factory.createHandler(providedInterface, null));

    }


    @Test
    void should_throw_exception_empty_provided_interface() {

        ProvidedInterface emptyProvidedInterface = new ProvidedInterface();
        // No bindings added
        Throwable throwable = assertThrows(IllegalArgumentException.class, () -> factory.createHandler(emptyProvidedInterface, null));
        assertNotNull(throwable);
        assertEquals("Provided interface has no bindings configured", throwable.getMessage());


    }

    @Test
    void should_throw_exception_when_service_url_is_blank() {
        HttpRestBinding binding = new HttpRestBinding();
        binding.setServiceUrl(""); // Blank URL
        ProvidedInterface providedInterface = new ProvidedInterface();
        providedInterface.addBinding(binding);
        providedInterface.setInterfaceVersion("1.0.0");

        Throwable throwable = assertThrows(IllegalArgumentException.class, () -> factory.createHandler(providedInterface, null));
        assertNotNull(throwable);
        assertEquals("Provided interface has no valid service URL", throwable.getMessage());

    }

    @Test
    void should_throw_exception_when_first_binding_is_not_http_rest() {
        ProvidedInterface providedInterface = new ProvidedInterface();
        providedInterface.addBinding(new WebBinding()); // Adding a non-HttpRestBinding
        Throwable throwable = assertThrows(IllegalArgumentException.class, () -> factory.createHandler(providedInterface, null));
        assertNotNull(throwable);
        assertEquals("First binding is not an HttpRestBinding", throwable.getMessage());
    }

    @Test
    void should_throw_exception_when_provided_interface_is_null() {
        Throwable throwable = assertThrows(IllegalArgumentException.class, () -> factory.createHandler(null, null));
        assertNotNull(throwable);
        assertEquals("ProvidedInterface cannot be null", throwable.getMessage());
    }


}
