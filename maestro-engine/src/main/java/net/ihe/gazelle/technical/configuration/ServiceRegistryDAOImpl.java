package net.ihe.gazelle.technical.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import net.ihe.gazelle.servicemetadata.api.domain.structure.Binding;
import net.ihe.gazelle.servicemetadata.api.domain.structure.Interface;
import net.ihe.gazelle.servicemetadata.api.domain.structure.RestBinding;
import net.ihe.gazelle.servicemetadata.api.domain.structure.Service;
import net.ihe.gazelle.validation.api.domain.metadata.structure.ValidationInterface;

import java.util.List;

@ApplicationScoped
public class ServiceRegistryDAOImpl implements ServiceRegistryDAO {

    @Override
    public List<Service> loadFile(String servicesFilePath) {
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            File jsonFile = new File(servicesFilePath);
//            return objectMapper.readValue(jsonFile, new TypeReference<>() {
//                @Override
//                public Type getType() {
//                    return super.getType();
//                }
//            });
        Service service = new Service();
        service.setName("matchbox");
        Interface inter = new ValidationInterface();
        RestBinding binding = new RestBinding();
        binding.setServiceUrl("https://gazelle.ihe.net/matchboxv3/gazelle/validation");
        inter.addBinding(binding);
        service.setProvidedInterfaces(List.of(inter));
        return List.of(service);
//    } catch (IOException e) {
//            throw new ReadingFileException("cannot read " + servicesFilePath, e);
//        }
    }

}
