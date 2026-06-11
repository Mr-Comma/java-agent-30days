package dev.comma.agent.apidoc;

import java.util.List;

public record ApiDocAnalysisResponse(
        int endpointCount,
        String summary,
        String topPriorityModule,
        String analysisContext,
        List<ApiEndpointAdvice> advices,
        List<ApiModuleSummary> modules,
        List<ApiReviewStep> reviewPlan) {
}
