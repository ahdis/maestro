package net.ihe.gazelle.technical.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ihe.gazelle.servicemetadata.api.domain.structure.Service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ServiceRegistryDAOImpl implements ServiceRegistryDAO {

    @Override
    public List<Service> loadFile(String servicesFilePath) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File jsonFile = new File(servicesFilePath);
            return objectMapper.readValue(jsonFile.toString(), new TypeReference<List<Service>>() {
                @Override
                public Type getType() {
                    return super.getType();
                }
            });
        } catch (IOException e) {
            throw new IncorrectFilePath("File path " + servicesFilePath + " can't be resolve", e.getCause());
        }
    }

}
