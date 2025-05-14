package net.ihe.gazelle.maestro.technical.configuration;

import jakarta.enterprise.context.ApplicationScoped;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import net.ihe.gazelle.servicemetadata.api.business.Service;
import net.ihe.gazelle.servicemetadata.technical.jaxrs.ServiceDTO;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class ServiceRegistryDAOImpl implements ServiceRegistryDAO {

    @ConfigProperty(name = "gzl.service.registry.file.path")
    String servicesFilePath;

    @Override
    public List<Service> getServices() {
        try {
            Path filePath = Path.of(servicesFilePath);
            TextSerDes serDes = new JacksonSerDes();
            String json = Files.readString(filePath, Charset.defaultCharset());
            List<Service> services = Arrays.stream(serDes.deserialize(json, ServiceDTO[].class))
                    .map(ServiceDTO::getBusinessObject)
                    .toList();
            if (!services.isEmpty() && services.getFirst() != null) {
                return services;
            }
            throw new ReadingFileException(servicesFilePath + " return an empty service list");
        } catch (IOException e) {
            throw new ReadingFileException("cannot read " + servicesFilePath, e);
        }
    }

}
