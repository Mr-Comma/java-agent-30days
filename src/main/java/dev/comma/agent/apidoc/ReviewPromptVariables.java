package dev.comma.agent.apidoc;

public record ReviewPromptVariables(
        String workflowStage,
        String suggestedTool,
        String blockingReason,
        String firstReviewModule,
        String firstReviewAction,
        String expectedOutputInstruction) {
}
