package net.ihe.gazelle.maestro.recording;

import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.StepOutputDissector;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.StepOutputDissectorFactory;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.StepOutputDissectorProvider;

import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;

/**
 * Default provider backed by {@link ServiceLoader}. It inspects available
 * {@link StepOutputDissectorFactory} implementations and returns the first one
 * that claims support for the provided step. When no factory matches, the
 * legacy attachment-only dissector is returned.
 */
public class SPIStepOutputDissectorProvider implements StepOutputDissectorProvider {

   private final List<StepOutputDissectorFactory> factories;

   public SPIStepOutputDissectorProvider() {
      this(loadFactories());
   }

   SPIStepOutputDissectorProvider(List<StepOutputDissectorFactory> factories) {
      this.factories = List.copyOf(Objects.requireNonNull(factories, "factories"));
   }

   @Override
   public StepOutputDissector resolve(StepRunReport stepRunReport) {
      return factories.stream()
            .filter(factory -> factory.supports(stepRunReport))
            .findFirst()
            .map(StepOutputDissectorFactory::create)
            .orElse(null);
   }

   private static List<StepOutputDissectorFactory> loadFactories() {
      ServiceLoader<StepOutputDissectorFactory> loader = ServiceLoader.load(StepOutputDissectorFactory.class);
      return loader.stream()
            .map(ServiceLoader.Provider::get)
            .toList();
   }
}
