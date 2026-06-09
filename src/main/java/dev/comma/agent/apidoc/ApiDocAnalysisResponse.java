package dev.comma.agent.apidoc;

import java.util.List;

public record ApiDocAnalysisResponse(
        int endpointCount, String summary, List<ApiEndpointAdvice> advices, List<ApiModuleSummary> modules) {
}
