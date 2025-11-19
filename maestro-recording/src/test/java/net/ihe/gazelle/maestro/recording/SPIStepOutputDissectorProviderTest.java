package net.ihe.gazelle.maestro.recording;

import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.StepOutputDissector;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.StepOutputDissectorFactory;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.StepOutputProjection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SPIStepOutputDissectorProviderTest {

   @Test
   void shouldReturnDissectorFromFirstSupportingFactory() {
      RecordingFactory unsupportedFactory = new RecordingFactory("UNSUPPORTED");
      RecordingFactory supportedFactory = new RecordingFactory("SIMULATION");
      SPIStepOutputDissectorProvider provider = new SPIStepOutputDissectorProvider(
            List.of(unsupportedFactory, supportedFactory));

      StepRunReport step = new StepRunReport().setType("SIMULATION");
      StepOutputDissector dissector = provider.resolve(step);

      assertThat(dissector).isSameAs(supportedFactory.createdDissector);
      assertThat(unsupportedFactory.createCalled).isFalse();
      assertThat(supportedFactory.createCalled).isTrue();
   }

   @Test
   void shouldReturnNullWhenNoFactorySupportsStep() {
      RecordingFactory factory = new RecordingFactory("VALIDATION");
      SPIStepOutputDissectorProvider provider = new SPIStepOutputDissectorProvider(List.of(factory));

      StepOutputDissector dissector = provider.resolve(new StepRunReport().setType("SIMULATION"));

      assertThat(dissector).isNull();
      assertThat(factory.createCalled).isFalse();
   }

   private static final class RecordingFactory implements StepOutputDissectorFactory {
      private final String supportedType;
      private final StepOutputDissector createdDissector = context -> StepOutputProjection.empty();
      private boolean createCalled;

      private RecordingFactory(String supportedType) {
         this.supportedType = supportedType;
      }

      @Override
      public boolean supports(StepRunReport stepRunReport) {
         return stepRunReport != null && supportedType.equals(stepRunReport.getType());
      }

      @Override
      public StepOutputDissector create() {
         createCalled = true;
         return createdDissector;
      }
   }
}
