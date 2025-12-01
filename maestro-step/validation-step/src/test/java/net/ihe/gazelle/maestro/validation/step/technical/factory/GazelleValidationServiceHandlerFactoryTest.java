package net.ihe.gazelle.maestro.validation.step.technical.factory;

import net.ihe.gazelle.maestro.spi.business.Handler;
import net.ihe.gazelle.maestro.spi.technical.HandlerContext;
import net.ihe.gazelle.maestro.validation.step.technical.handler.GazelleValidationServiceHandler;
import net.ihe.gazelle.servicemetadata.api.business.ConsumedInterface;
import net.ihe.gazelle.servicemetadata.api.business.HttpRestBinding;
import net.ihe.gazelle.servicemetadata.api.business.ProvidedInterface;
import net.ihe.gazelle.validation.v2.api.technical.ws.ValidationServiceController;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GazelleValidationServiceHandlerFactoryTest {

   private final HandlerContext handlerContext = new HandlerContext("GV");
   private final GazelleValidationServiceHandlerFactory factory = new GazelleValidationServiceHandlerFactory();

   @Test
   void shouldExposeHandlerType() {
      assertEquals(GazelleValidationServiceHandler.class, factory.getHandlerType());
   }

   @Test
   void shouldDescribeConsumedInterfaces() {
      List<ConsumedInterface> consumed = factory.getConsumedInterfaces();
      assertEquals(2, consumed.size());
      assertEquals("Validation Service", consumed.getFirst().getInterfaceName());
      assertEquals(ValidationServiceController.INTERFACE_NAME, consumed.getLast().getInterfaceName());
   }

   @Test
   void shouldCreateV1HandlerWhenInterfaceVersionStartsWithOne() {
      ProvidedInterface providedInterface = httpProvidedInterface("http://localhost:8080/validation", "1.2.0");

      Handler handler = factory.createHandler(handlerContext, providedInterface, null);

      assertInstanceOf(GazelleValidationServiceHandler.class, handler);
   }

   @Test
   void shouldCreateV2HandlerWhenInterfaceVersionIsNotV1() {
      ProvidedInterface providedInterface = httpProvidedInterface("http://localhost:8080/validation", "2.0.0");

      Handler handler = factory.createHandler(handlerContext, providedInterface, null);

      assertInstanceOf(GazelleValidationServiceHandler.class, handler);
   }

   @Test
   void shouldFailWhenNoBindingsAvailable() {
      ProvidedInterface providedInterface = new ProvidedInterface();
      providedInterface.setInterfaceName("Validation Service");

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> factory.createHandler(handlerContext, providedInterface, null));
      assertEquals("Service Validation Service has no bindings", exception.getMessage());
   }

   @Test
   void shouldFailWhenUriIsInvalid() {
      ProvidedInterface providedInterface = httpProvidedInterface("://bad-uri", "2.0.0");

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> factory.createHandler(handlerContext, providedInterface, null));
      assertEquals("Can't initiate validation handler due to exception while creating client from server url",
            exception.getMessage());
   }

   private ProvidedInterface httpProvidedInterface(String url, String version) {
      HttpRestBinding binding = new HttpRestBinding();
      binding.setServiceUrl(url);
      ProvidedInterface providedInterface = new ProvidedInterface();
      providedInterface.setInterfaceName("Validation Service");
      providedInterface.setInterfaceVersion(version);
      providedInterface.addBinding(binding);
      return providedInterface;
   }
}
