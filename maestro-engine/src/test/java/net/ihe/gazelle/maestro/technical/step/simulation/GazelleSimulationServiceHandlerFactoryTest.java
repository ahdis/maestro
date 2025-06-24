package net.ihe.gazelle.maestro.technical.step.simulation;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.maestro.technical.configuration.ConfigProviderImpl;
import net.ihe.gazelle.servicemetadata.api.business.ConsumedInterface;
import net.ihe.gazelle.servicemetadata.api.business.HttpRestBinding;
import net.ihe.gazelle.servicemetadata.api.business.ProvidedInterface;
import net.ihe.gazelle.simulation.jaxrs.api.technical.ws.SimulationAPI;
import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.wildfly.common.Assert.assertNotNull;
import static org.wildfly.common.Assert.assertTrue;

@QuarkusTest
class GazelleSimulationServiceHandlerFactoryTest {

    private final GazelleSimulationServiceHandlerFactory factory = new GazelleSimulationServiceHandlerFactory();

    @Inject
    Config config;

    @Test
    void should_be_type_simulation_handler() {
        assertEquals(GazelleSimulationServiceHandler.class, factory.getHandlerType());
    }

    @Test
    void should_get_consumed_interface() {
        ConsumedInterface consumedInterface = factory.getConsumedInterface();
        assertNotNull(consumedInterface);
        assertEquals(SimulationAPI.INTERFACE_NAME, consumedInterface.getInterfaceName());
    }

    @Test
    void should_create_simulation_handler() {
        HttpRestBinding binding = new HttpRestBinding();
        binding.setServiceUrl("http://localhost:80");
        ProvidedInterface providedInterface = new ProvidedInterface();
        providedInterface.addBinding(binding);
        providedInterface.setInterfaceVersion("1.0.0");
        providedInterface.setInterfaceName("test");
        Handler handler = factory.createHandler(providedInterface, new ConfigProviderImpl(config));
        assertNotNull(handler);
        assertTrue(handler instanceof GazelleSimulationServiceHandler);
    }

    @Test
    void no_binding_simulation_handler_throws_exception() {
        ProvidedInterface providedInterface = new ProvidedInterface();
        providedInterface.setInterfaceVersion("1.0.0");
        providedInterface.setInterfaceName("test");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> factory.createHandler(providedInterface, null)
        );
        assertEquals("Service test has no bindings", exception.getMessage());
    }
}
