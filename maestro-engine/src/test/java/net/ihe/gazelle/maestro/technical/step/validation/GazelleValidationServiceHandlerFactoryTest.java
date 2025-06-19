package net.ihe.gazelle.maestro.technical.step.validation;

import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.maestro.business.step.validation.ServiceClientException;
import net.ihe.gazelle.maestro.business.step.validation.ValidationHandler;
import net.ihe.gazelle.servicemetadata.api.business.HttpRestBinding;
import net.ihe.gazelle.servicemetadata.api.business.ProvidedInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.wildfly.common.Assert.assertNotNull;

class GazelleValidationServiceHandlerFactoryTest {

    GazelleValidationServiceHandlerFactory factory;

    @BeforeEach
    public void setUp() {
        factory = new GazelleValidationServiceHandlerFactory();
    }

    @Test
    void getHandlerTypeTes(){
        assertEquals(ValidationHandler.class, factory.getHandlerType());
    }

    @Test
    void createHandlerTest(){
        HttpRestBinding binding = new HttpRestBinding();
        binding.setServiceUrl("http://localhost:80");
        ProvidedInterface providedInterface = new ProvidedInterface();
        providedInterface.addBinding(binding);
        providedInterface.setInterfaceVersion("1.0.0");
        Handler handler = factory.createHandler(providedInterface, null);
        assertNotNull(handler);
    }

    @Test
    void throwsIfNoBinding() {
        ProvidedInterface providedInterface = new ProvidedInterface();
        assertThrows(NoSuchElementException.class,() -> factory.createHandler(providedInterface, null));
    }

    @Test
    void throwsIfBadUriSyntax() {
        HttpRestBinding binding = new HttpRestBinding();
        binding.setServiceUrl("bad uri");
        ProvidedInterface providedInterface = new ProvidedInterface();
        providedInterface.addBinding(binding);
        assertThrows(ServiceClientException.class,() -> factory.createHandler(providedInterface, null));
    }

}
