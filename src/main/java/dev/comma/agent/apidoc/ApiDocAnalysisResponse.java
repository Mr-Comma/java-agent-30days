package dev.comma.agent.apidoc;

import java.util.List;

public record ApiDocAnalysisResponse(int endpointCount, List<ApiEndpointAdvice> advices) {
}
