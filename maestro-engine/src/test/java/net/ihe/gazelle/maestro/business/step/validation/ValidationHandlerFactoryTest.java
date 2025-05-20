package net.ihe.gazelle.maestro.business.step.validation;

import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.servicemetadata.api.business.HttpRestBinding;
import net.ihe.gazelle.servicemetadata.api.business.ProvidedInterface;
import net.ihe.gazelle.servicemetadata.api.business.Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.wildfly.common.Assert.assertNotNull;

class ValidationHandlerFactoryTest {


    ValidationHandlerFactory factory;

    @BeforeEach
    public void setUp() {
        factory = new ValidationHandlerFactory();
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
        Service service = new Service();
        service.setName("test");
        service.setProvidedInterfaces(List.of(providedInterface));
        Handler handler = factory.createHandler(service);
        assertNotNull(handler);
    }

}
