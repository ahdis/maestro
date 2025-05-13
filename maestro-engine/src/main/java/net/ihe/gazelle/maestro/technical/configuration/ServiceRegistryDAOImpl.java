package net.ihe.gazelle.maestro.technical.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.achachraf.typeson.interlay.Typeson;
import jakarta.enterprise.context.ApplicationScoped;
import net.ihe.gazelle.servicemetadata.api.business.Service;
import net.ihe.gazelle.servicemetadata.technical.jaxrs.ServiceDTO;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@ApplicationScoped
public class ServiceRegistryDAOImpl implements ServiceRegistryDAO {

    @ConfigProperty(name = "gzl.service.registry.file.path")
    String servicesFilePath;

    @Override
    public List<Service> getServices() {
        try {
            Typeson typeson = new Typeson();
            Path filePath = Path.of(servicesFilePath);
            String json = Files.readString(filePath, Charset.defaultCharset());
            List<Service> services = typeson.unmarshallList(json, new TypeReference<List<ServiceDTO>>(){}).stream().map(
                    ServiceDTO::getBusinessObject
            ).toList();
            if (!services.isEmpty() && services.getFirst() != null) {
                return services;
            }
            throw new ReadingFileException(servicesFilePath + " return an empty service list");
        } catch (IOException e) {
            throw new ReadingFileException("cannot read " + servicesFilePath, e);
        }
    }

}
