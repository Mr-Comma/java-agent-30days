package dev.comma.agent.apidoc;

import java.util.List;

public record ApiDocAnalysisResponse(
        int endpointCount,
        String summary,
        String topPriorityModule,
        String analysisRole,
        String analysisFacts,
        List<ApiAnalysisFact> analysisFactItems,
        String analysisTask,
        List<ApiEndpointAdvice> advices,
        List<ApiModuleSummary> modules,
        List<ApiReviewStep> reviewPlan) {
}
