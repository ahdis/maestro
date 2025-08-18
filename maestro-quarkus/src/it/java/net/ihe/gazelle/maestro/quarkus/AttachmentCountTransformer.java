package net.ihe.gazelle.maestro.quarkus;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class AttachmentCountTransformer extends ResponseDefinitionTransformer {

    private final ObjectMapper mapper = new ObjectMapper();
    private final Pattern filenamePattern = Pattern.compile("filename=\"");
    private final Pattern contentDispositionPattern = Pattern.compile("Content-Disposition:", Pattern.CASE_INSENSITIVE);

    @Override
    public String getName() {
        return "attachment-count-transformer";
    }

    @Override
    public boolean applyGlobally() {
        return false; // apply only when stub asks for it
    }

    @Override
    public ResponseDefinition transform(Request request,
                                        ResponseDefinition responseDefinition,
                                        FileSource files,
                                        Parameters parameters) {

        String body = request.getBodyAsString();
        int count = 0;

        // 1) JSON array direct or attachments property
        if (body != null && !body.isEmpty()) {
            try {
                JsonNode node = mapper.readTree(body);
                if (node.isArray()) {
                    count = node.size();
                } else if (node.has("attachments") && node.get("attachments").isArray()) {
                    count = node.get("attachments").size();
                }
            } catch (IOException ignored) {
                // not JSON -> continue to heuristics
            }
        }

        // 2) multipart / form-data heuristics
        if (count == 0 && body != null && !body.isEmpty()) {
            Matcher m = filenamePattern.matcher(body);
            int filenameCount = 0;
            while (m.find()) filenameCount++;
            if (filenameCount > 0) {
                count = filenameCount;
            } else {
                Matcher m2 = contentDispositionPattern.matcher(body);
                int dispCount = 0;
                while (m2.find()) dispCount++;
                if (dispCount > 0) count = dispCount;
            }
        }

        // 3) build IDs
        List<String> ids = new ArrayList<>();
        if (count > 0) {
            IntStream.range(0, count).forEach(i ->
                    ids.add("mock-attachment-" + UUID.randomUUID().toString())
            );
        }

        String responseJson;
        try {
            responseJson = mapper.writeValueAsString(ids);
        } catch (Exception e) {
            responseJson = "[]";
        }

        // Build and return a new ResponseDefinition (don't try to mutate the provided one)
        return ResponseDefinitionBuilder
                .responseDefinition()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(responseJson)
                .build();
    }
}