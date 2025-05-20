package net.ihe.gazelle;

import net.ihe.gazelle.maestro.technical.configuration.ServiceRegistryDAOImpl;
import net.ihe.gazelle.maestro.technical.configuration.ServiceRegistryImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServiceRegistryImplTest {

    @Test
    void getServiceWithServiceNameNull(){
        ServiceRegistryImpl serviceRegistry = new ServiceRegistryImpl(new ServiceRegistryDAOImpl());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> serviceRegistry.getService(null));
        assertEquals("service name is required", exception.getMessage());
    }

}
