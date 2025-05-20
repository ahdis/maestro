package net.ihe.gazelle.maestro.technical.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.jackson.ObjectMapperCustomizer;
import jakarta.inject.Singleton;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSubTypingDTOModule;

@Singleton
public class RegisterCustomModuleCustomizer implements ObjectMapperCustomizer {

    public void customize(ObjectMapper mapper) {
        mapper.registerModule(new JacksonSubTypingDTOModule());
    }
}
