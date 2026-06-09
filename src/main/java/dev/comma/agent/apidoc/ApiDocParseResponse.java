package dev.comma.agent.apidoc;

import java.util.List;

public record ApiDocParseResponse(int endpointCount, List<ApiEndpoint> endpoints) {
}
