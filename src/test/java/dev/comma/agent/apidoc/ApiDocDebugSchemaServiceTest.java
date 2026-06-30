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
                .extracting(ApiDocDebugField::renderType)
                .containsExactly("badge", "badge", "tool-link", "text", "list", "json", "prompt-preview");
        assertThat(response.fields())
                .extracting(ApiDocDebugField::copyable)
                .containsExactly(true, true, true, true, false, false, true);
        assertThat(response.fields())
                .extracting(ApiDocDebugField::interactionHint)
                .containsExactly(
                        "复制状态用于路由判断",
                        "复制阶段用于工作流分支",
                        "触发建议工具节点",
                        "复制阻塞原因用于补输入说明",
                        "展开查看人类调试提示",
                        "展开查看结构化 Prompt 变量",
                        "复制 Prompt 预览给 Agent 节点");
        assertThat(response.fields())
                .extracting(ApiDocDebugField::agentAction)
                .containsExactly(
                        "route-by-status",
                        "branch-by-stage",
                        "invoke-suggested-tool",
                        "collect-missing-input",
                        "show-human-hints",
                        "bind-prompt-variables",
                        "copy-prompt-preview");
        assertThat(response.fields())
                .extracting(ApiDocDebugField::targetNode)
                .containsExactly(
                        "status-router-node",
                        "workflow-branch-node",
                        "tool-dispatch-node",
                        "input-collector-node",
                        "debug-panel-node",
                        "prompt-template-node",
                        "prompt-preview-node");
        assertThat(response.fields())
                .extracting(ApiDocDebugField::nodeInputPath)
                .containsExactly(
                        "$.workflowStatus",
                        "$.workflowStage",
                        "$.suggestedTool",
                        "$.blockingReason",
                        "$.debugHints",
                        "$.reviewPromptVariables",
                        "$.reviewPromptPreview");
        assertThat(response.fields())
                .extracting(ApiDocDebugField::handoffPayloadKey)
                .containsExactly(
                        "workflowStatus",
                        "workflowStage",
                        "suggestedTool",
                        "blockingReason",
                        "debugHints",
                        "reviewPromptVariables",
                        "reviewPromptPreview");
        assertThat(response.fields())
                .extracting(ApiDocDebugField::requiredForNode)
                .containsExactly(true, true, true, false, true, true, true);
        assertThat(response.fields())
                .extracting(ApiDocDebugField::fallbackValue)
                .containsExactly(
                        "NEEDS_INPUT",
                        "INPUT_REQUIRED",
                        "openapi-input-validator",
                        null,
                        List.of("请重新运行 /api-docs/analyze 获取调试提示。"),
                        fallbackReviewPromptVariables(),
                        "请先补充有效 OpenAPI/Swagger JSON，不要编造接口。");
        assertThat(response.fields())
                .extracting(ApiDocDebugField::validationRule)
                .containsExactly(
                        "值必须为 READY 或 NEEDS_INPUT；缺失/非法时使用 fallbackValue。",
                        "值必须为 REVIEW_READY 或 INPUT_REQUIRED；缺失/非法时使用 fallbackValue。",
                        "值必须为 api-risk-reviewer 或 openapi-input-validator；缺失/非法时使用 fallbackValue。",
                        "READY 时允许为 null；NEEDS_INPUT 时应为非空缺输入说明。",
                        "应为非空字符串列表；缺失/为空时使用 fallbackValue。",
                        "应包含 workflowStage、suggestedTool、blockingReason、firstReviewModule、firstReviewAction；缺失时使用 fallbackValue。",
                        "应为非空审查或补输入请求；缺失/空白时使用 fallbackValue。");
        assertThat(response.fields())
                .extracting(ApiDocDebugField::missingFieldPolicy)
                .containsExactly(
                        "缺失或非法时应用 fallbackValue 并路由到输入补全。",
                        "缺失或非法时应用 fallbackValue 并进入输入补全阶段。",
                        "缺失或非法时应用 fallbackValue，避免调用未知工具。",
                        "READY 可缺省为 null；NEEDS_INPUT 缺失时提示重新运行分析。",
                        "缺失或为空时应用 fallbackValue，保持调试面板有提示。",
                        "缺失固定键时应用 fallbackValue，避免 PromptTemplate 变量漂移。",
                        "缺失或空白时应用 fallbackValue，要求先补充有效输入。");
        assertThat(response.fields())
                .extracting(ApiDocDebugField::policySeverity)
                .containsExactly(
                        "needs-input",
                        "needs-input",
                        "fallback",
                        "rerun-analysis",
                        "fallback",
                        "fallback",
                        "needs-input");
        assertThat(response.fields())
                .extracting(ApiDocDebugField::retryable)
                .containsExactly(false, false, true, false, true, true, false);
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
                        "badge",
                        true,
                        "复制状态用于路由判断",
                        "route-by-status",
                        "status-router-node",
                        "$.workflowStatus",
                        "workflowStatus",
                        true,
                        "NEEDS_INPUT",
                        "值必须为 READY 或 NEEDS_INPUT；缺失/非法时使用 fallbackValue。",
                        "缺失或非法时应用 fallbackValue 并路由到输入补全。",
                        "needs-input",
                        false,
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
                        "text",
                        true,
                        "复制阻塞原因用于补输入说明",
                        "collect-missing-input",
                        "input-collector-node",
                        "$.blockingReason",
                        "blockingReason",
                        false,
                        null,
                        "READY 时允许为 null；NEEDS_INPUT 时应为非空缺输入说明。",
                        "READY 可缺省为 null；NEEDS_INPUT 缺失时提示重新运行分析。",
                        "rerun-analysis",
                        false,
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
                        "prompt-preview",
                        true,
                        "复制 Prompt 预览给 Agent 节点",
                        "copy-prompt-preview",
                        "prompt-preview-node",
                        "$.reviewPromptPreview",
                        "reviewPromptPreview",
                        true,
                        "请先补充有效 OpenAPI/Swagger JSON，不要编造接口。",
                        "应为非空审查或补输入请求；缺失/空白时使用 fallbackValue。",
                        "缺失或空白时应用 fallbackValue，要求先补充有效输入。",
                        "needs-input",
                        false,
                        "ApiDocAnalyzerService.reviewPromptPreview",
                        "请调用 api-risk-reviewer 审查 orders 模块：先审查删除接口、权限控制和误删保护；请输出风险说明、测试建议和下一步行动。",
                        "请调用 openapi-input-validator 处理 INPUT_REQUIRED 阶段：OpenAPI/Swagger JSON 缺少 paths 或未解析到接口；请先补充有效输入，不要编造接口。"));
    }

    private Map<String, Object> fallbackReviewPromptVariables() {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("workflowStage", "INPUT_REQUIRED");
        values.put("suggestedTool", "openapi-input-validator");
        values.put("blockingReason", "OpenAPI/Swagger JSON 缺少 paths 或未解析到接口。");
        values.put("firstReviewModule", null);
        values.put("firstReviewAction", null);
        return values;
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
