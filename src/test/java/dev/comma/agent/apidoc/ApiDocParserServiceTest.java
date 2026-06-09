package dev.comma.agent.apidoc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class ApiDocParserServiceTest {

    private final ApiDocParserService parserService = new ApiDocParserService(new ObjectMapper());

    @Test
    void extractsEndpointsFromOpenApiPaths() {
        String openApiJson = """
                {
                  "openapi": "3.0.1",
                  "paths": {
                    "/users": {
                      "get": {"summary": "List users"},
                      "post": {"operationId": "createUser"},
                      "parameters": []
                    },
                    "/orders/{id}": {
                      "delete": {"summary": "Delete order"}
                    }
                  }
                }
                """;

        ApiDocParseResponse response = parserService.parse(openApiJson);

        assertThat(response.endpointCount()).isEqualTo(3);
        assertThat(response.endpoints())
                .containsExactly(
                        new ApiEndpoint("GET", "/users", "List users"),
                        new ApiEndpoint("POST", "/users", "createUser"),
                        new ApiEndpoint("DELETE", "/orders/{id}", "Delete order"));
    }

    @Test
    void returnsEmptyResponseWhenPathsAreMissing() {
        ApiDocParseResponse response = parserService.parse("{\"openapi\":\"3.0.1\"}");

        assertThat(response.endpointCount()).isZero();
        assertThat(response.endpoints()).isEmpty();
    }

    @Test
    void rejectsInvalidJson() {
        assertThatThrownBy(() -> parserService.parse("not json"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid OpenAPI JSON");
    }
}
