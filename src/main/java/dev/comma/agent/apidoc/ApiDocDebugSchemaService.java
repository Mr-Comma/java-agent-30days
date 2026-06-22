package dev.comma.agent.apidoc;

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
                                "ApiDocAnalyzerService.workflowStatus",
                                "READY"),
                        new ApiDocDebugField(
                                "workflowStage",
                                "string",
                                true,
                                "REVIEW_READY，审查节点可执行",
                                "INPUT_REQUIRED，输入校验节点可执行",
                                "显示当前工作流阶段，便于调试面板分组",
                                "ApiDocAnalyzerService.workflowStage",
                                "REVIEW_READY"),
                        new ApiDocDebugField(
                                "suggestedTool",
                                "string",
                                true,
                                "推荐调用 api-risk-reviewer",
                                "推荐调用 openapi-input-validator",
                                "映射到下一步工具或 Agent 节点",
                                "ApiDocAnalyzerService.suggestedTool",
                                "api-risk-reviewer"),
                        new ApiDocDebugField(
                                "blockingReason",
                                "string|null",
                                false,
                                "null，没有阻塞原因",
                                "返回缺输入原因",
                                "展示阻塞提示，避免编造接口",
                                "ApiDocAnalyzerService.blockingReason",
                                null),
                        new ApiDocDebugField(
                                "debugHints",
                                "array<string>",
                                true,
                                "展示状态、工具和首个审查动作",
                                "展示状态、工具和缺输入原因",
                                "给人类调试面板直接展示",
                                "ApiDocAnalyzerService.debugHints",
                                List.of("状态：READY，可以进入 API 风险审查。")),
                        new ApiDocDebugField(
                                "reviewPromptVariables",
                                "object",
                                true,
                                "输出首个模块和动作等结构化变量",
                                "首个模块/动作保持 null",
                                "给 PromptTemplate 或工作流节点传参",
                                "ApiDocAnalyzerService.reviewPromptVariables",
                                Map.of(
                                        "workflowStage", "REVIEW_READY",
                                        "suggestedTool", "api-risk-reviewer",
                                        "firstReviewModule", "orders")),
                        new ApiDocDebugField(
                                "reviewPromptPreview",
                                "string",
                                true,
                                "生成可执行的审查请求",
                                "生成补输入请求",
                                "作为调试预览，不替代结构化变量",
                                "ApiDocAnalyzerService.reviewPromptPreview",
                                "请调用 api-risk-reviewer 审查 orders 模块：先审查删除接口、权限控制和误删保护；请输出风险说明、测试建议和下一步行动。")));
    }
}
