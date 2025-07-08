package net.ihe.gazelle.maestro.technical.step.validation;

import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.maestro.business.step.validation.ServiceClientException;
import net.ihe.gazelle.servicemetadata.api.business.ConsumedInterface;
import net.ihe.gazelle.servicemetadata.api.business.HttpRestBinding;
import net.ihe.gazelle.servicemetadata.api.business.ProvidedInterface;
import net.ihe.gazelle.validation.v2.api.technical.ws.ValidationServiceController;
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
