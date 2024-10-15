package net.ihe.gazelle.technical.configuration;

import jakarta.enterprise.context.ApplicationScoped;
import net.ihe.gazelle.servicemetadata.api.domain.structure.Service;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import io.github.achachraf.typeson.interlay.Typeson;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ServiceRegistryDAOImpl implements ServiceRegistryDAO {

    @ConfigProperty(name = "service.registry.file.path", defaultValue = "/opt/maestro/services.json")
    String servicesFilePath;

    @Override
    public List<Service> loadFile() {
        try {
            Typeson typeson = new Typeson();
            Path filePath = Path.of(servicesFilePath);
            String json = Files.readString(filePath, Charset.defaultCharset());
            ArrayList services = typeson.unmarshall(json, ArrayList.class);
            if(services!=null && !services.isEmpty() && services.getFirst() instanceof Service) {
                return services;
            }
            throw new ReadingFileException(servicesFilePath + " return an empty or null service list");
        } catch (IOException e) {
            throw new ReadingFileException("cannot read " + servicesFilePath, e);
        }
    }

}
