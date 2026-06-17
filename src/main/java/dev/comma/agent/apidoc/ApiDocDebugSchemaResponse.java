package dev.comma.agent.apidoc;

import java.util.List;

public record ApiDocDebugSchemaResponse(
        String endpoint,
        List<ApiDocDebugField> fields) {
}
