package dev.comma.agent.apidoc;

import java.util.List;

public record ApiToolCallPlan(
        String tool,
        String reason,
        List<String> payloadKeys) {
}
