package dev.comma.agent.apidoc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
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
                .extracting(ApiDocDebugField::displayOrder)
                .containsExactly(10, 20, 30, 40, 50, 60, 70);
        assertThat(response.fields())
                .extracting(ApiDocDebugField::category)
                .containsExactly(
                        "routing",
                        "routing",
                        "routing",
                        "routing",
                        "human-hint",
                        "prompt",
                        "prompt");
        assertThat(response.fields())
                .extracting(ApiDocDebugField::uiLabel)
                .containsExactly(
                        "工作流状态",
                        "工作流阶段",
                        "建议工具",
                        "阻塞原因",
                        "调试提示",
                        "审查 Prompt 变量",
                        "审查 Prompt 预览");
        assertThat(response.fields())
                .extracting(ApiDocDebugField::uiDescription)
                .containsExactly(
                        "判断 OpenAPI 输入是否已具备进入风险审查的条件。",
                        "标识当前应进入审查节点还是输入补全节点。",
                        "给出下一步建议调用的 Agent 工具或工作流节点。",
                        "说明当前阻塞原因；READY 时为空。",
                        "给调试面板展示的人类可读运行提示。",
                        "传给 PromptTemplate 或工作流节点的结构化变量。",
                        "根据结构化变量渲染出的可执行审查请求预览。");
        assertThat(response.fields())
                .extracting(ApiDocDebugField::visibility)
                .containsExactly("summary", "summary", "summary", "summary", "summary", "detail", "summary");
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
                .extracting(ApiDocDebugField::readyExampleValue)
                .containsExactly(
                        "READY",
                        "REVIEW_READY",
                        "api-risk-reviewer",
                        null,
                        List.of("状态：READY，可以进入 API 风险审查。"),
                        readyReviewPromptVariablesExample(),
                        "请调用 api-risk-reviewer 审查 orders 模块：先审查删除接口、权限控制和误删保护；请输出风险说明、测试建议和下一步行动。");
        assertThat(response.fields())
                .extracting(ApiDocDebugField::needsInputExampleValue)
                .containsExactly(
                        "NEEDS_INPUT",
                        "INPUT_REQUIRED",
                        "openapi-input-validator",
                        "OpenAPI/Swagger JSON 缺少 paths 或未解析到接口。",
                        List.of("状态：NEEDS_INPUT，暂不进入风险审查。"),
                        needsInputReviewPromptVariablesExample(),
                        "请调用 openapi-input-validator 处理 INPUT_REQUIRED 阶段：OpenAPI/Swagger JSON 缺少 paths 或未解析到接口；请先补充有效输入，不要编造接口。");
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
                        10,
                        "routing",
                        "工作流状态",
                        "判断 OpenAPI 输入是否已具备进入风险审查的条件。",
                        "summary",
                        "ApiDocAnalyzerService.workflowStatus",
                        "READY",
                        "NEEDS_INPUT"));
        assertThat(fields.get(3))
                .isEqualTo(new ApiDocDebugField(
                        "blockingReason",
                        "string|null",
                        false,
                        "null，没有阻塞原因",
                        "返回缺输入原因",
                        "展示阻塞提示，避免编造接口",
                        40,
                        "routing",
                        "阻塞原因",
                        "说明当前阻塞原因；READY 时为空。",
                        "summary",
                        "ApiDocAnalyzerService.blockingReason",
                        null,
                        "OpenAPI/Swagger JSON 缺少 paths 或未解析到接口。"));
        assertThat(fields.get(6))
                .isEqualTo(new ApiDocDebugField(
                        "reviewPromptPreview",
                        "string",
                        true,
                        "生成可执行的审查请求",
                        "生成补输入请求",
                        "作为调试预览，不替代结构化变量",
                        70,
                        "prompt",
                        "审查 Prompt 预览",
                        "根据结构化变量渲染出的可执行审查请求预览。",
                        "summary",
                        "ApiDocAnalyzerService.reviewPromptPreview",
                        "请调用 api-risk-reviewer 审查 orders 模块：先审查删除接口、权限控制和误删保护；请输出风险说明、测试建议和下一步行动。",
                        "请调用 openapi-input-validator 处理 INPUT_REQUIRED 阶段：OpenAPI/Swagger JSON 缺少 paths 或未解析到接口；请先补充有效输入，不要编造接口。"));
    }

    private Map<String, Object> readyReviewPromptVariablesExample() {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("workflowStage", "REVIEW_READY");
        values.put("suggestedTool", "api-risk-reviewer");
        values.put("blockingReason", null);
        values.put("firstReviewModule", "orders");
        values.put("firstReviewAction", "先审查删除接口、权限控制和误删保护。");
        return values;
    }

    private Map<String, Object> needsInputReviewPromptVariablesExample() {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("workflowStage", "INPUT_REQUIRED");
        values.put("suggestedTool", "openapi-input-validator");
        values.put("blockingReason", "OpenAPI/Swagger JSON 缺少 paths 或未解析到接口。");
        values.put("firstReviewModule", null);
        values.put("firstReviewAction", null);
        return values;
    }
}
