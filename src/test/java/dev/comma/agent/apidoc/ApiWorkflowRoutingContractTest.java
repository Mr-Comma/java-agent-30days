package dev.comma.agent.apidoc;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ApiWorkflowRoutingContractTest {

    @Test
    void derivesRoutingValuesFromEndpointCount() {
        assertThat(ApiWorkflowStatus.fromEndpointCount(0)).isEqualTo(ApiWorkflowStatus.NEEDS_INPUT);
        assertThat(ApiWorkflowStatus.fromEndpointCount(2)).isEqualTo(ApiWorkflowStatus.READY);
    }

    @Test
    void exposesStableAllowedValuesForDebugSchema() {
        assertThat(ApiWorkflowStatus.allowedValues())
                .containsExactly("READY", "NEEDS_INPUT");
        assertThat(ApiWorkflowStage.allowedValues())
                .containsExactly("REVIEW_READY", "INPUT_REQUIRED");
        assertThat(ApiSuggestedTool.allowedValues())
                .containsExactly("api-risk-reviewer", "openapi-input-validator");
        assertThat(ApiWorkflowStatus.READY.stageValue()).isEqualTo(ApiWorkflowStage.REVIEW_READY.value());
        assertThat(ApiWorkflowStatus.READY.suggestedToolValue()).isEqualTo(ApiSuggestedTool.API_RISK_REVIEWER.value());
        assertThat(ApiWorkflowStatus.NEEDS_INPUT.stageValue()).isEqualTo(ApiWorkflowStage.INPUT_REQUIRED.value());
        assertThat(ApiWorkflowStatus.NEEDS_INPUT.suggestedToolValue()).isEqualTo(ApiSuggestedTool.OPENAPI_INPUT_VALIDATOR.value());
        assertThat(ApiWorkflowStatus.READY.routeNextActionCode()).isEqualTo(ApiAnalysisNextActionCode.START_API_RISK_REVIEW);
        assertThat(ApiWorkflowStatus.NEEDS_INPUT.routeNextActionCode()).isEqualTo(ApiAnalysisNextActionCode.COLLECT_OPENAPI_INPUT);
    }
}
