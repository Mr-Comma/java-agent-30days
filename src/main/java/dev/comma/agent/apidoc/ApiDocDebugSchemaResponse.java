package dev.comma.agent.apidoc;

import java.util.List;
import java.util.Map;

public record ApiDocDebugSchemaResponse(
        String endpoint,
        String schemaVersion,
        String contractOwner,
        List<String> workflowStatusAllowedValues,
        List<String> workflowStageAllowedValues,
        List<String> suggestedToolAllowedValues,
        List<String> nextActionCodeAllowedValues,
        Map<String, List<String>> nextActionCodesByStage,
        List<ApiDocDebugField> fields) {
}
