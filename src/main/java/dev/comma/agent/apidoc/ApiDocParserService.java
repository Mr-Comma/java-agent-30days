package dev.comma.agent.apidoc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ApiDocParserService {

    private static final List<String> HTTP_METHODS = List.of("get", "post", "put", "delete", "patch", "head", "options", "trace");

    private final ObjectMapper objectMapper;

    public ApiDocParserService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ApiDocParseResponse parse(String openApiJson) {
        if (openApiJson == null || openApiJson.isBlank()) {
            return new ApiDocParseResponse(0, List.of());
        }

        try {
            JsonNode root = objectMapper.readTree(openApiJson);
            JsonNode paths = root.path("paths");
            if (!paths.isObject()) {
                return new ApiDocParseResponse(0, List.of());
            }

            List<ApiEndpoint> endpoints = new ArrayList<>();
            Iterator<Map.Entry<String, JsonNode>> pathEntries = paths.fields();
            while (pathEntries.hasNext()) {
                Map.Entry<String, JsonNode> pathEntry = pathEntries.next();
                collectEndpoints(pathEntry.getKey(), pathEntry.getValue(), endpoints);
            }
            return new ApiDocParseResponse(endpoints.size(), endpoints);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Invalid OpenAPI JSON", exception);
        }
    }

    private void collectEndpoints(String path, JsonNode operations, List<ApiEndpoint> endpoints) {
        if (!operations.isObject()) {
            return;
        }

        Iterator<Map.Entry<String, JsonNode>> operationEntries = operations.fields();
        while (operationEntries.hasNext()) {
            Map.Entry<String, JsonNode> operationEntry = operationEntries.next();
            String method = operationEntry.getKey().toLowerCase(Locale.ROOT);
            if (!HTTP_METHODS.contains(method)) {
                continue;
            }
            JsonNode operation = operationEntry.getValue();
            String summary = operation.path("summary").asText(operation.path("operationId").asText(""));
            endpoints.add(new ApiEndpoint(method.toUpperCase(Locale.ROOT), path, summary));
        }
    }
}
