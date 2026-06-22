package dev.comma.agent.apidoc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ApiDocDebugSchemaServiceTest {

    private final ApiDocDebugSchemaService service = new ApiDocDebugSchemaService();

    @Test
    void returnsStableDebugFieldSchemaForWorkflowRouting() {
        ApiDocDebugSchemaResponse response = service.schema();

        assertThat(response.endpoint()).isEqualTo("/api-docs/analyze");
        assertThat(response.schemaVersion()).isEqualTo("v1");
        assertThat(response.contractOwner()).isEqualTo("api-docs-agent");
        assertThat(response.fields())
                .extracting(ApiDocDebugField::name)
                .containsExactly(
                        "workflowStatus",
                        "workflowStage",
                        "suggestedTool",
                        "blockingReason",
                        "debugHints",
                        "reviewPromptVariables",
                        "reviewPromptPreview");
        assertThat(response.fields())
                .extracting(ApiDocDebugField::jsonType)
                .containsExactly("string", "string", "string", "string|null", "array<string>", "object", "string");
        assertThat(response.fields())
                .extracting(ApiDocDebugField::required)
                .containsExactly(true, true, true, false, true, true, true);
        assertThat(response.fields())
                .extracting(ApiDocDebugField::source)
                .containsExactly(
                        "ApiDocAnalyzerService.workflowStatus",
                        "ApiDocAnalyzerService.workflowStage",
                        "ApiDocAnalyzerService.suggestedTool",
                        "ApiDocAnalyzerService.blockingReason",
                        "ApiDocAnalyzerService.debugHints",
                        "ApiDocAnalyzerService.reviewPromptVariables",
                        "ApiDocAnalyzerService.reviewPromptPreview");
        assertThat(response.fields())
                .extracting(ApiDocDebugField::exampleValue)
                .containsExactly(
                        "READY",
                        "REVIEW_READY",
                        "api-risk-reviewer",
                        null,
                        List.of("状态：READY，可以进入 API 风险审查。"),
                        Map.of(
                                "workflowStage", "REVIEW_READY",
                                "suggestedTool", "api-risk-reviewer",
                                "firstReviewModule", "orders"),
                        "请调用 api-risk-reviewer 审查 orders 模块：先审查删除接口、权限控制和误删保护；请输出风险说明、测试建议和下一步行动。");
    }

    @Test
    void documentsReadyAndNeedsInputMeaningsForCriticalFields() {
        List<ApiDocDebugField> fields = service.schema().fields();

        assertThat(fields.get(0))
                .isEqualTo(new ApiDocDebugField(
                        "workflowStatus",
                        "string",
                        true,
                        "已解析到接口，可进入风险审查",
                        "缺少 paths 或未解析到接口",
                        "作为主路由状态，决定进入审查还是补输入",
                        "ApiDocAnalyzerService.workflowStatus",
                        "READY"));
        assertThat(fields.get(3))
                .isEqualTo(new ApiDocDebugField(
                        "blockingReason",
                        "string|null",
                        false,
                        "null，没有阻塞原因",
                        "返回缺输入原因",
                        "展示阻塞提示，避免编造接口",
                        "ApiDocAnalyzerService.blockingReason",
                        null));
        assertThat(fields.get(6))
                .isEqualTo(new ApiDocDebugField(
                        "reviewPromptPreview",
                        "string",
                        true,
                        "生成可执行的审查请求",
                        "生成补输入请求",
                        "作为调试预览，不替代结构化变量",
                        "ApiDocAnalyzerService.reviewPromptPreview",
                        "请调用 api-risk-reviewer 审查 orders 模块：先审查删除接口、权限控制和误删保护；请输出风险说明、测试建议和下一步行动。"));
    }
}
