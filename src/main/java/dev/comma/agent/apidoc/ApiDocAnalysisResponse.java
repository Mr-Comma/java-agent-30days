package dev.comma.agent.apidoc;

import java.util.List;

public record ApiDocAnalysisResponse(
        int endpointCount,
        String summary,
        String topPriorityModule,
        String analysisRole,
        String analysisFacts,
        List<ApiAnalysisFact> analysisFactItems,
        String workflowStatus,
        String workflowStage,
        String suggestedTool,
        String reviewPromptTemplate,
        ReviewPromptVariables reviewPromptVariables,
        String reviewPromptPreview,
        List<String> debugHints,
        String blockingReason,
        String recommendedNextAction,
        String taskGoal,
        String taskConstraints,
        String expectedOutput,
        List<String> executionChecklist,
        List<String> analysisTrace,
        List<ApiAnalysisTraceItem> analysisTraceItems,
        String analysisTask,
        List<ApiEndpointAdvice> advices,
        List<ApiModuleSummary> modules,
        List<ApiReviewStep> reviewPlan) {
}
