package net.ihe.gazelle.maestro.quarkus.configuration;

import net.ihe.gazelle.maestro.quarkus.ws.TestRunController;
import net.ihe.gazelle.servicemetadata.api.business.HttpRestBindingBuilder;
import net.ihe.gazelle.servicemetadata.api.business.ProvidedInterface;
import net.ihe.gazelle.servicemetadata.api.business.ProvidedInterfaceBuilder;
import net.ihe.gazelle.servicemetadata.api.technical.ProvidedInterfaceIdentifier;
import org.eclipse.microprofile.config.ConfigProvider;

import java.util.Collections;

public class TestRunProvidedInterfaceIdentifier implements ProvidedInterfaceIdentifier {

   private final String maestroUrl;

   public TestRunProvidedInterfaceIdentifier() {
      this(
            ConfigProvider.getConfig()
                  .getOptionalValue("gzl.maestro.url", String.class)
                  .orElseThrow(() -> new IllegalStateException(
                        "Service registry URL not configured. Please set the 'gzl.maestro.url' property."
                  ))
      );
   }

   private TestRunProvidedInterfaceIdentifier(String maestroUrl) {
      this.maestroUrl = maestroUrl;
   }

   @Override
   public ProvidedInterface getProvidedInterface() {

      return new ProvidedInterfaceBuilder()
            .setInterfaceName(TestRunController.INTERFACE_NAME)
            .setInterfaceVersion(TestRunController.INTERFACE_VERSION)
            .setBindings(Collections.singleton(
                  new HttpRestBindingBuilder()
                        .setServiceUrl(maestroUrl))
            )
            .build();

   }
}
