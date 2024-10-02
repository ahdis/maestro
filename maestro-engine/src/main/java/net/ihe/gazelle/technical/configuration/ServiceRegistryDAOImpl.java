package net.ihe.gazelle.technical.configuration;

import jakarta.enterprise.context.ApplicationScoped;
import net.ihe.gazelle.servicemetadata.api.domain.structure.Interface;
import net.ihe.gazelle.servicemetadata.api.domain.structure.RestBinding;
import net.ihe.gazelle.servicemetadata.api.domain.structure.Service;
import net.ihe.gazelle.validation.api.domain.metadata.structure.ValidationInterface;

import java.util.ArrayList;
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
        Service matchboxService = new Service();
        matchboxService.setName("matchbox");
        Interface matchboxInter = new ValidationInterface();
        RestBinding binding = new RestBinding();
        binding.setServiceUrl("https://gazelle.ihe.net/matchboxv3/gazelle/validation");
        matchboxInter.addBinding(binding);
        matchboxService.setProvidedInterfaces(List.of(matchboxInter));

        Service xmlService = new Service();
        xmlService.setName("xmlValidator");
        Interface xmlInter = new ValidationInterface();
        RestBinding xmlBinding = new RestBinding();
        xmlBinding.setServiceUrl("http://localhost:8081/phschematron/rest/validation");
        xmlInter.addBinding(xmlBinding);
        xmlService.setProvidedInterfaces(List.of(xmlInter));

        List services = new ArrayList();
        services.add(matchboxService);
        services.add(xmlService);
        return services;
//    } catch (IOException e) {
//            throw new ReadingFileException("cannot read " + servicesFilePath, e);
//        }
    }

}
