package net.ihe.gazelle.maestro.technical.factory;

import net.ihe.gazelle.maestro.api.business.testrun.Maestro;
import net.ihe.gazelle.maestro.business.engine.MaestroImpl;
import net.ihe.gazelle.maestro.technical.SessionStoreContextImpl;
import net.ihe.gazelle.maestro.technical.StepRunnerProviderSPI;
import net.ihe.gazelle.maestro.technical.configuration.ServiceRegistryDAOImpl;
import net.ihe.gazelle.maestro.technical.configuration.ServiceRegistryImpl;
import net.ihe.gazelle.maestro.technical.jobs.MicroprofileMaestroProducer;
import net.ihe.gazelle.maestro.technical.jobs.StepMockEmitter;
import net.ihe.gazelle.maestro.technical.jobs.StepRunnerProvider;
import net.ihe.gazelle.maestro.technical.jobs.TestRunMockEmitter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MaestroFactoryTest {

    MaestroFactory maestroFactory;

    @BeforeEach
    public void setUp() {
        TestRunMockEmitter testRunMockEmitter = new TestRunMockEmitter();
        StepMockEmitter stepMockEmitter = new StepMockEmitter();
        maestroFactory = new MaestroFactory(new ServiceRegistryImpl(new ServiceRegistryDAOImpl()),
                new MicroprofileMaestroProducer(testRunMockEmitter, stepMockEmitter),
                new SessionStoreContextImpl());
    }

    @Test
    void getMaestroTest(){
        Maestro maestro = maestroFactory.getMaestro();
        assertNotNull(maestro);
        assertInstanceOf(MaestroImpl.class, maestro);
    }

    @Test
    void getStepRunnerProviderTest(){
        StepRunnerProvider provider = maestroFactory.getStepRunnerProvider();
        assertNotNull(provider);
        assertInstanceOf(StepRunnerProviderSPI.class, provider);
    }

}
