package net.ihe.gazelle.maestro.business.step.itb.gateway.ws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.WebApplicationException;
import net.ihe.gazelle.maestro.business.step.itb.gateway.dto.ItbSystemDTO;
import net.ihe.gazelle.maestro.business.step.itb.gateway.dto.ItbTestSessionDTO;
import net.ihe.gazelle.maestro.business.step.itb.gateway.dto.ItbTriggerDTO;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.io.IOException;

@Provider
@Consumes("application/json")
@Produces("application/json")
public class ItbTriggerDTOMessageBodyReader implements MessageBodyReader<ItbTriggerDTO>, MessageBodyWriter<ItbTriggerDTO> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == ItbTriggerDTO.class;
    }

    @Override
    public ItbTriggerDTO readFrom(Class<ItbTriggerDTO> type, Type genericType, Annotation[] annotations,
                                  MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
                                  InputStream entityStream) throws IOException, WebApplicationException {

        JsonNode rootNode = mapper.readTree(entityStream);

        ItbSystemDTO systemDTO = null;
        ItbTestSessionDTO testSessionDTO = null;
        String testReport = null;

        for (JsonNode inputNode : rootNode.path("inputs")) {
            String name = inputNode.path("name").asText();

            switch (name) {
                case "system":
                    systemDTO = parseSystemDTO(inputNode.path("item"));
                    break;
                case "testSession":
                    testSessionDTO = parseTestSessionDTO(inputNode.path("item"));
                    break;
                case "testReport":
                    testReport = inputNode.path("value").asText();
                    break;
            }
        }

        return new ItbTriggerDTO(systemDTO, testSessionDTO, testReport);
    }

    private ItbSystemDTO parseSystemDTO(JsonNode itemNode) {
        Long id = null;
        String shortName = null;
        String fullName = null;

        for (JsonNode node : itemNode) {
            String name = node.path("name").asText();

            switch (name) {
                case "id":
                    id = Long.parseLong(node.path("value").asText());
                    break;
                case "shortName":
                    shortName = node.path("value").asText();
                    break;
                case "fullName":
                    fullName = node.path("value").asText();
                    break;
            }
        }

        return new ItbSystemDTO(id, shortName, fullName);
    }

    private ItbTestSessionDTO parseTestSessionDTO(JsonNode itemNode) {
        String testSuiteId = null;
        String testCaseId = null;
        String testSessionId = null;

        for (JsonNode node : itemNode) {
            String name = node.path("name").asText();

            switch (name) {
                case "testSuiteIdentifier":
                    testSuiteId = node.path("value").asText();
                    break;
                case "testCaseIdentifier":
                    testCaseId = node.path("value").asText();
                    break;
                case "testSessionIdentifier":
                    testSessionId = node.path("value").asText();
                    break;
            }
        }

        return new ItbTestSessionDTO(testSuiteId, testCaseId, testSessionId);
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == ItbTriggerDTO.class;
    }

    @Override
    public void writeTo(ItbTriggerDTO itbTriggerDTO, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        mapper.writeValue(entityStream, itbTriggerDTO);
    }
}
