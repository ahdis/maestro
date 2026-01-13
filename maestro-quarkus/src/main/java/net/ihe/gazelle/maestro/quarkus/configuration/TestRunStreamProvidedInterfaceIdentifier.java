package net.ihe.gazelle.maestro.quarkus.configuration;

import net.ihe.gazelle.maestro.quarkus.websocket.TestStreamSocketController;
import net.ihe.gazelle.servicemetadata.api.business.ProvidedInterface;
import net.ihe.gazelle.servicemetadata.api.business.ProvidedInterfaceBuilder;
import net.ihe.gazelle.servicemetadata.api.business.WebSocketBindingBuilder;
import net.ihe.gazelle.servicemetadata.api.technical.ProvidedInterfaceIdentifier;
import org.eclipse.microprofile.config.ConfigProvider;

import java.util.Collections;

public class TestRunStreamProvidedInterfaceIdentifier implements ProvidedInterfaceIdentifier {

   private final String maestroUrl;

   public TestRunStreamProvidedInterfaceIdentifier() {
      this(
            ConfigProvider.getConfig()
                  .getOptionalValue("gzl.maestro.url", String.class)
                  .orElseThrow(() -> new IllegalStateException(
                        "Service registry URL not configured. Please set the 'gzl.maestro.url' property."
                  ))
      );
   }

   private TestRunStreamProvidedInterfaceIdentifier(String maestroUrl) {
      this.maestroUrl = maestroUrl;
   }

   @Override
   public ProvidedInterface getProvidedInterface() {

      return new ProvidedInterfaceBuilder()
            .setInterfaceName(TestStreamSocketController.INTERFACE_NAME)
            .setInterfaceVersion(TestStreamSocketController.INTERFACE_VERSION)
            .setBindings(Collections.singleton(
                  new WebSocketBindingBuilder()
                        .setWebSocketUrl(maestroUrl.replace("http", "ws")))
            )
            .build();

   }
}
