package net.ihe.gazelle.maestro.technical;

import net.ihe.gazelle.maestro.api.business.testrun.Step;
import net.ihe.gazelle.maestro.technical.configuration.ServiceRegistryDAOImpl;
import net.ihe.gazelle.maestro.technical.configuration.ServiceRegistryImpl;
import net.ihe.gazelle.maestro.technical.handler.HandlerProviderSPI;
import net.ihe.gazelle.maestro.technical.jobs.StepRunnerProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class StepRunnerProviderSPITest {

    @Test
    void createNotSupportedStep(){
        Step step = new DummyStep();
        StepRunnerProvider provider = new StepRunnerProviderSPI(new HandlerProviderSPI(new ServiceRegistryImpl(new ServiceRegistryDAOImpl()), null));
        assertThrows(IllegalArgumentException.class, () -> provider.create(step));
    }

}
