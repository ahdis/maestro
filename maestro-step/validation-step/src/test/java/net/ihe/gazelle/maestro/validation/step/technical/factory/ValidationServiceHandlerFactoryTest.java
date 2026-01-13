package net.ihe.gazelle.maestro.validation.step.technical.factory;

import net.ihe.gazelle.maestro.spi.business.Handler;
import net.ihe.gazelle.maestro.spi.technical.HandlerContext;
import net.ihe.gazelle.maestro.validation.step.technical.handler.ValidationServiceHandler;
import net.ihe.gazelle.servicemetadata.api.business.ConsumedInterface;
import net.ihe.gazelle.servicemetadata.api.business.HttpRestBinding;
import net.ihe.gazelle.servicemetadata.api.business.ProvidedInterface;
import net.ihe.gazelle.validation.clients.api.ValidationServiceFactoryProvider;
import net.ihe.gazelle.validation.clients.api.ValidationServiceInterfaceFactory;
import net.ihe.gazelle.validation.v2.api.business.ValidationService;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ValidationServiceHandlerFactoryTest {

   private static final String TEST_INTERFACE = "Validation Service API";
   private static final String EVS_INTERFACE = "EVS Client API";

   private final HandlerContext handlerContext = new HandlerContext("GV");

   @Test
   void shouldExposeHandlerType() {
      ValidationServiceHandlerFactory factory = new ValidationServiceHandlerFactory(providerWith(TEST_INTERFACE));

      assertEquals(ValidationServiceHandler.class, factory.getHandlerType());
   }

   @Test
   void shouldDescribeConsumedInterfacesWithoutEvs() {
      ValidationServiceHandlerFactory factory = new ValidationServiceHandlerFactory(providerWith(TEST_INTERFACE, EVS_INTERFACE));

      List<ConsumedInterface> consumed = factory.getConsumedInterfaces();

      assertEquals(1, consumed.size());
      assertEquals(TEST_INTERFACE, consumed.getFirst().getInterfaceName());
   }

   @Test
   void shouldCreateHandlerUsingMatchingFactory() {
      ValidationServiceHandlerFactory factory = new ValidationServiceHandlerFactory(providerWith(TEST_INTERFACE));
      ProvidedInterface providedInterface = httpProvidedInterface(TEST_INTERFACE, "http://localhost:8080/validation", "2.0.0");

      Handler handler = factory.createHandler(handlerContext, providedInterface, null);

      assertInstanceOf(ValidationServiceHandler.class, handler);
   }

   @Test
   void shouldFailWhenNoBindingsAvailable() {
      ValidationServiceHandlerFactory factory = new ValidationServiceHandlerFactory(providerWith(TEST_INTERFACE));
      ProvidedInterface providedInterface = new ProvidedInterface();
      providedInterface.setInterfaceName(TEST_INTERFACE);

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> factory.createHandler(handlerContext, providedInterface, null));
      assertEquals("Service " + TEST_INTERFACE + " has no bindings", exception.getMessage());
   }

   @Test
   void shouldFailWhenNoFactoryMatchesInterface() {
      ValidationServiceHandlerFactory factory = new ValidationServiceHandlerFactory(providerWith(TEST_INTERFACE));
      ProvidedInterface providedInterface = httpProvidedInterface("Unknown Service", "http://localhost:8080/validation", "2.0.0");

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> factory.createHandler(handlerContext, providedInterface, null));
      assertEquals("No validation service factory found for interface Unknown Service", exception.getMessage());
   }

   @Test
   void shouldFailWhenUriIsInvalid() {
      ValidationServiceHandlerFactory factory = new ValidationServiceHandlerFactory(providerWith(TEST_INTERFACE));
      ProvidedInterface providedInterface = httpProvidedInterface(TEST_INTERFACE, "://bad-uri", "2.0.0");

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> factory.createHandler(handlerContext, providedInterface, null));
      assertEquals("Can't initiate validation handler due to exception while creating client from server url",
            exception.getMessage());
   }

   private ValidationServiceFactoryProvider providerWith(String... interfaceNames) {
      List<ValidationServiceInterfaceFactory> factories = Stream.of(interfaceNames)
            .map(name -> (ValidationServiceInterfaceFactory) new StubValidationServiceInterfaceFactory(name, true))
            .toList();
      return () -> factories;
   }

   private ProvidedInterface httpProvidedInterface(String name, String url, String version) {
      HttpRestBinding binding = new HttpRestBinding();
      binding.setServiceUrl(url);
      ProvidedInterface providedInterface = new ProvidedInterface();
      providedInterface.setInterfaceName(name);
      providedInterface.setInterfaceVersion(version);
      providedInterface.addBinding(binding);
      return providedInterface;
   }

   private record StubValidationServiceInterfaceFactory(
           String interfaceName,
           boolean supports)
           implements ValidationServiceInterfaceFactory {

      @Override
         public String getInterfaceName() {
            return interfaceName;
         }

         @Override
         public boolean supports(ProvidedInterface providedInterface) {
            return supports;
         }

         @Override
         public ValidationService create(URI uri, ProvidedInterface providedInterface) {
            return mock(ValidationService.class);
         }
      }
}
