package dev.comma.agent.apidoc;

import java.util.Arrays;
import java.util.List;

public enum ApiWorkflowStatus {
    READY(
            ApiWorkflowStage.REVIEW_READY,
            ApiSuggestedTool.API_RISK_REVIEWER,
            ApiAnalysisNextActionCode.START_API_RISK_REVIEW),
    NEEDS_INPUT(
            ApiWorkflowStage.INPUT_REQUIRED,
            ApiSuggestedTool.OPENAPI_INPUT_VALIDATOR,
            ApiAnalysisNextActionCode.COLLECT_OPENAPI_INPUT);

    private final ApiWorkflowStage stage;
    private final ApiSuggestedTool suggestedTool;
    private final ApiAnalysisNextActionCode routeNextActionCode;

    ApiWorkflowStatus(ApiWorkflowStage stage, ApiSuggestedTool suggestedTool,
            ApiAnalysisNextActionCode routeNextActionCode) {
        this.stage = stage;
        this.suggestedTool = suggestedTool;
        this.routeNextActionCode = routeNextActionCode;
    }

    public String value() {
        return name();
    }

    public String stageValue() {
        return stage.value();
    }

    public String suggestedToolValue() {
        return suggestedTool.value();
    }

    public ApiAnalysisNextActionCode routeNextActionCode() {
        return routeNextActionCode;
    }

    public static ApiWorkflowStatus fromEndpointCount(int endpointCount) {
        if (endpointCount == 0) {
            return NEEDS_INPUT;
        }
        return READY;
    }

    public static List<String> allowedValues() {
        return Arrays.stream(values())
                .map(ApiWorkflowStatus::value)
                .toList();
    }

    public static String allowedValuesText() {
        return String.join(" 或 ", allowedValues());
    }
}
