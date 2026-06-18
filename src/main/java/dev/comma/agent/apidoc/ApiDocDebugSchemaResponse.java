package dev.comma.agent.apidoc;

import java.util.List;

public record ApiDocDebugSchemaResponse(
        String endpoint,
        String schemaVersion,
        String contractOwner,
        List<ApiDocDebugField> fields) {
}
