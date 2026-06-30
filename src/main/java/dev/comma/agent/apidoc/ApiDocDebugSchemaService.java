package dev.comma.agent.apidoc;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ApiDocDebugSchemaService {

    public ApiDocDebugSchemaResponse schema() {
        return new ApiDocDebugSchemaResponse(
                "/api-docs/analyze",
                "v1",
                "api-docs-agent",
                List.of(
                        new ApiDocDebugField(
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
                                "请补充有效 OpenAPI/Swagger JSON 后重新分析。",
                                "openapi-input-owner",
                                "ApiDocAnalyzerService.workflowStatus",
                                "READY",
                                "NEEDS_INPUT"),
                        new ApiDocDebugField(
                                "workflowStage",
                                "string",
                                true,
                                "REVIEW_READY，审查节点可执行",
                                "INPUT_REQUIRED，输入校验节点可执行",
                                "显示当前工作流阶段，便于调试面板分组",
                                20,
                                "routing",
                                "工作流阶段",
                                "标识当前应进入审查节点还是输入补全节点。",
                                "summary",
                                "badge",
                                true,
                                "复制阶段用于工作流分支",
                                "branch-by-stage",
                                "workflow-branch-node",
                                "$.workflowStage",
                                "workflowStage",
                                true,
                                "INPUT_REQUIRED",
                                "值必须为 REVIEW_READY 或 INPUT_REQUIRED；缺失/非法时使用 fallbackValue。",
                                "缺失或非法时应用 fallbackValue 并进入输入补全阶段。",
                                "needs-input",
                                false,
                                "请补充有效 OpenAPI/Swagger JSON 后重新分析。",
                                "openapi-input-owner",
                                "ApiDocAnalyzerService.workflowStage",
                                "REVIEW_READY",
                                "INPUT_REQUIRED"),
                        new ApiDocDebugField(
                                "suggestedTool",
                                "string",
                                true,
                                "推荐调用 api-risk-reviewer",
                                "推荐调用 openapi-input-validator",
                                "映射到下一步工具或 Agent 节点",
                                30,
                                "routing",
                                "建议工具",
                                "给出下一步建议调用的 Agent 工具或工作流节点。",
                                "summary",
                                "tool-link",
                                true,
                                "触发建议工具节点",
                                "invoke-suggested-tool",
                                "tool-dispatch-node",
                                "$.suggestedTool",
                                "suggestedTool",
                                true,
                                "openapi-input-validator",
                                "值必须为 api-risk-reviewer 或 openapi-input-validator；缺失/非法时使用 fallbackValue。",
                                "缺失或非法时应用 fallbackValue，避免调用未知工具。",
                                "fallback",
                                true,
                                "已自动降级为 openapi-input-validator，可继续分发到工具节点。",
                                "none",
                                "ApiDocAnalyzerService.suggestedTool",
                                "api-risk-reviewer",
                                "openapi-input-validator"),
                        new ApiDocDebugField(
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
                                "请将阻塞原因展示给用户，并要求重新提交有效接口文档。",
                                "api-docs-operator",
                                "ApiDocAnalyzerService.blockingReason",
                                null,
                                "OpenAPI/Swagger JSON 缺少 paths 或未解析到接口。"),
                        new ApiDocDebugField(
                                "debugHints",
                                "array<string>",
                                true,
                                "展示状态、工具和首个审查动作",
                                "展示状态、工具和缺输入原因",
                                "给人类调试面板直接展示",
                                50,
                                "human-hint",
                                "调试提示",
                                "给调试面板展示的人类可读运行提示。",
                                "summary",
                                "list",
                                false,
                                "展开查看人类调试提示",
                                "show-human-hints",
                                "debug-panel-node",
                                "$.debugHints",
                                "debugHints",
                                true,
                                List.of("请重新运行 /api-docs/analyze 获取调试提示。"),
                                "应为非空字符串列表；缺失/为空时使用 fallbackValue。",
                                "缺失或为空时应用 fallbackValue，保持调试面板有提示。",
                                "fallback",
                                true,
                                "已使用默认调试提示，可继续展示给调试面板。",
                                "none",
                                "ApiDocAnalyzerService.debugHints",
                                List.of("状态：READY，可以进入 API 风险审查。"),
                                List.of("状态：NEEDS_INPUT，暂不进入风险审查。")),
                        new ApiDocDebugField(
                                "reviewPromptVariables",
                                "object",
                                true,
                                "输出首个模块和动作等结构化变量",
                                "首个模块/动作保持 null",
                                "给 PromptTemplate 或工作流节点传参",
                                60,
                                "prompt",
                                "审查 Prompt 变量",
                                "传给 PromptTemplate 或工作流节点的结构化变量。",
                                "detail",
                                "json",
                                false,
                                "展开查看结构化 Prompt 变量",
                                "bind-prompt-variables",
                                "prompt-template-node",
                                "$.reviewPromptVariables",
                                "reviewPromptVariables",
                                true,
                                fallbackReviewPromptVariables(),
                                "应包含 workflowStage、suggestedTool、blockingReason、firstReviewModule、firstReviewAction；缺失时使用 fallbackValue。",
                                "缺失固定键时应用 fallbackValue，避免 PromptTemplate 变量漂移。",
                                "fallback",
                                true,
                                "已使用 INPUT_REQUIRED 变量对象，可继续渲染补输入 Prompt。",
                                "none",
                                "ApiDocAnalyzerService.reviewPromptVariables",
                                readyReviewPromptVariablesExample(),
                                needsInputReviewPromptVariablesExample()),
                        new ApiDocDebugField(
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
                                "请先收集有效 OpenAPI/Swagger JSON，再生成审查 Prompt。",
                                "prompt-input-owner",
                                "ApiDocAnalyzerService.reviewPromptPreview",
                                "请调用 api-risk-reviewer 审查 orders 模块：先审查删除接口、权限控制和误删保护；请输出风险说明、测试建议和下一步行动。",
                                "请调用 openapi-input-validator 处理 INPUT_REQUIRED 阶段：OpenAPI/Swagger JSON 缺少 paths 或未解析到接口；请先补充有效输入，不要编造接口。")));
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
