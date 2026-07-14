package dev.comma.agent.apidoc;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ReadmeApiDocExampleTest {

    @Test
    void documentsDebugFieldTable() throws IOException {
        String readme = Files.readString(Path.of("README.md"), StandardCharsets.UTF_8);

        assertThat(readme).contains("| 字段 | 顺序 | 分组 | 中文标题 | UI 说明 | 可见性 | 渲染类型 | 可复制 | 交互提示 | Agent 动作 | 目标节点 | 节点输入路径 | 交接 payload 键 | 节点必需 | 降级值 | 校验规则 | 缺字段策略 | 策略级别 | 可重试 | 操作提示 | 失败升级 | 升级条件 | 升级提示 | 升级优先级 | 升级类别 | 升级 SLA | 升级联系点 | 升级 Runbook | Runbook 步骤 | 升级责任角色 | Runbook 预期结果 | READY 时含义 | NEEDS_INPUT 时含义 | 前端/Agent 用法 |");
        assertThat(readme).contains("| `workflowStatus` | 10 | `routing` | 工作流状态 | 判断 OpenAPI 输入是否已具备进入风险审查的条件。 | `summary` | `badge` | `是` | 复制状态用于路由判断 | `route-by-status` | `status-router-node` | `$.workflowStatus` | `workflowStatus` | `是` | `NEEDS_INPUT` | 值必须为 `READY` 或 `NEEDS_INPUT`；缺失/非法时使用 `fallbackValue`。 | 缺失或非法时应用 `fallbackValue` 并路由到输入补全。 | `needs-input` | `否` | 请补充有效 OpenAPI/Swagger JSON 后重新分析。 | `openapi-input-owner` | 当 `workflowStatus` 缺失、非法或持续为 `NEEDS_INPUT` 且无法自动补全时升级。 | 已将输入问题升级给 openapi-input-owner：请补充有效 OpenAPI/Swagger JSON 后重新分析。 | 10 | `input` | 当天补齐 | `openapi-input-owner#docs-intake` | `openapi-input-runbook#validate-paths` | 校验 `paths` 并要求重新提交有效 OpenAPI/Swagger JSON。 | OpenAPI 输入负责人 | 拿到包含 `paths` 的有效 OpenAPI/Swagger JSON，并重新分析。 | 已解析到接口，可进入风险审查 | 缺少 `paths` 或未解析到接口 | 作为主路由状态，决定进入审查还是补输入 |");
        assertThat(readme).contains("| `suggestedTool` | 30 | `routing` | 建议工具 | 给出下一步建议调用的 Agent 工具或工作流节点。 | `summary` | `tool-link` | `是` | 触发建议工具节点 | `invoke-suggested-tool` | `tool-dispatch-node` | `$.suggestedTool` | `suggestedTool` | `是` | `openapi-input-validator` | 值必须为 `api-risk-reviewer` 或 `openapi-input-validator`；缺失/非法时使用 `fallbackValue`。 | 缺失或非法时应用 `fallbackValue`，避免调用未知工具。 | `fallback` | `是` | 已自动降级为 openapi-input-validator，可继续分发到工具节点。 | `none` | 无需升级；缺失或非法时使用 `fallbackValue` 继续工具分发。 | 无需升级：已降级到 openapi-input-validator 并可继续分发。 | 90 | `fallback` | 自动降级继续 | `agent-runtime#safe-fallback` | `agent-runtime-runbook#tool-fallback` | 将未知工具降级为 `openapi-input-validator` 后继续分发。 | Agent 运行时 | 未知工具已降级为 `openapi-input-validator` 并继续分发。 | 推荐调用 `api-risk-reviewer` | 推荐调用 `openapi-input-validator` | 映射到下一步工具或 Agent 节点 |");
        assertThat(readme).contains("| `blockingReason` | 40 | `routing` | 阻塞原因 | 说明当前阻塞原因；READY 时为空。 | `summary` | `text` | `是` | 复制阻塞原因用于补输入说明 | `collect-missing-input` | `input-collector-node` | `$.blockingReason` | `blockingReason` | `否` | `null` | `READY` 时允许为 `null`；`NEEDS_INPUT` 时应为非空缺输入说明。 | `READY` 可缺省为 `null`；`NEEDS_INPUT` 缺失时提示重新运行分析。 | `rerun-analysis` | `否` | 请将阻塞原因展示给用户，并要求重新提交有效接口文档。 | `api-docs-operator` | 当 `NEEDS_INPUT` 路径缺少阻塞原因或原因无法指导补输入时升级。 | 已将阻塞原因问题升级给 api-docs-operator：请展示原因并要求重新提交接口文档。 | 30 | `operator` | 1 个工作日内处理 | `api-docs-operator#blocking-reason` | `api-docs-operator-runbook#collect-blocking-input` | 收集缺失 `paths` 或解析失败原因并反馈给输入方。 | API 文档助手值班人 | 阻塞原因已反馈给输入方，并等待重新提交接口文档。 | `null`，没有阻塞原因 | 返回缺输入原因 | 展示阻塞提示，避免编造接口 |");
        assertThat(readme).contains("| `reviewPromptVariables` | 60 | `prompt` | 审查 Prompt 变量 | 传给 PromptTemplate 或工作流节点的结构化变量。 | `detail` | `json` | `否` | 展开查看结构化 Prompt 变量 | `bind-prompt-variables` | `prompt-template-node` | `$.reviewPromptVariables` | `reviewPromptVariables` | `是` | INPUT_REQUIRED 变量对象 | 应包含 `workflowStage`、`suggestedTool`、`blockingReason`、`firstReviewModule`、`firstReviewAction`；缺失时使用 `fallbackValue`。 | 缺失固定键时应用 `fallbackValue`，避免 PromptTemplate 变量漂移。 | `fallback` | `是` | 已使用 INPUT_REQUIRED 变量对象，可继续渲染补输入 Prompt。 | `none` | 无需升级；缺失固定键时使用 `fallbackValue` 继续渲染补输入 Prompt。 | 无需升级：已使用 INPUT_REQUIRED 变量对象继续渲染补输入 Prompt。 | 90 | `fallback` | 自动降级继续 | `agent-runtime#safe-fallback` | `agent-runtime-runbook#prompt-variable-fallback` | 补齐 `INPUT_REQUIRED` 变量对象后继续渲染。 | Agent 运行时 | Prompt 变量已补齐为 INPUT_REQUIRED 对象并可继续渲染。 | 输出首个模块和动作等结构化变量 | 首个模块/动作保持 `null` | 给 PromptTemplate 或工作流节点传参 |");
        assertThat(readme).contains("| `reviewPromptPreview` | 70 | `prompt` | 审查 Prompt 预览 | 根据结构化变量渲染出的可执行审查请求预览。 | `summary` | `prompt-preview` | `是` | 复制 Prompt 预览给 Agent 节点 | `copy-prompt-preview` | `prompt-preview-node` | `$.reviewPromptPreview` | `reviewPromptPreview` | `是` | 请先补充有效 OpenAPI/Swagger JSON，不要编造接口。 | 应为非空审查或补输入请求；缺失/空白时使用 `fallbackValue`。 | 缺失或空白时应用 `fallbackValue`，要求先补充有效输入。 | `needs-input` | `否` | 请先收集有效 OpenAPI/Swagger JSON，再生成审查 Prompt。 | `prompt-input-owner` | 当 Prompt 预览缺失、空白或无法生成非编造审查请求时升级。 | 已将 Prompt 预览问题升级给 prompt-input-owner：请先收集有效接口文档。 | 40 | `prompt` | 当天收集输入 | `prompt-input-owner#prompt-intake` | `prompt-input-runbook#collect-non-hallucinated-prompt-input` | 收集非编造接口输入后再生成审查 Prompt。 | Prompt 输入负责人 | 已收集有效接口输入，下一次可生成非编造审查 Prompt。 | 生成可执行的审查请求 | 生成补输入请求 | 作为调试预览，不替代结构化变量 |");
        assertThat(readme).contains("| `analysisTraceItems` | 80 | `trace` | 结构化分析轨迹 | 保留解析、聚合、排序、路由和建议生成的结构化执行轨迹。 | `detail` | `timeline` | `否` | 展开查看结构化执行轨迹 | `show-analysis-timeline` | `analysis-timeline-node` | `$.analysisTraceItems` | `analysisTraceItems` | `否` | 默认 route 轨迹 | 应为包含 `stage`、`status`、`message`、`nextAction`、`nextActionCode` 的非空对象列表；缺失/为空时使用 `fallbackValue`。 | 缺失或为空时应用 `fallbackValue`，保持时间线可展示。 | `fallback` | `是` | 已使用默认结构化轨迹，可继续展示时间线。 | `none` | 无需升级；缺失或为空时使用 `fallbackValue` 继续展示。 | 无需升级：已使用默认结构化轨迹继续展示。 | 90 | `fallback` | 自动降级继续 | `agent-runtime#safe-fallback` | `agent-runtime-runbook#trace-items-fallback` | 补写默认结构化轨迹后继续展示。 | Agent 运行时 | 调试面板展示默认结构化轨迹并保留继续处理入口。 | 输出结构化执行轨迹，route 状态为 `READY` | 输出结构化执行轨迹，route 状态为 `NEEDS_INPUT` | 给前端时间线或 Agent 编排层按 `stage/status/message/nextAction/nextActionCode` 展示执行过程 |");
    }

    @Test
    void documentsDebugSchemaResponseExample() throws IOException {
        String readme = Files.readString(Path.of("README.md"), StandardCharsets.UTF_8);

        assertThat(readme).contains("`/api-docs/analyze` 的响应里可以重点看这几个调试字段");
        assertThat(readme).contains("`analysisTrace` 记录解析、聚合、排序、路由和建议生成的关键步骤");
        assertThat(readme).contains("路由步骤会显式标出 `workflowStatus` 与 `suggestedTool` 的匹配结果");
        assertThat(readme).contains("`analysisTraceItems` 保留相同执行轨迹的结构化形态");
        assertThat(readme).contains("`stage`、`status`、`message`、`nextAction`、`nextActionCode`");
        assertThat(readme).contains("前端时间线或 Agent 编排层不用解析字符串也能展示阶段状态");
        assertThat(readme).contains("稳定机器动作标识做确定性分支");
        assertThat(readme).contains("`nextActionCodeAllowedValues` 公开 `nextActionCode` 的完整允许值列表");
        assertThat(readme).contains("`debug-schema` 的响应结构稳定面向前端和 Agent 编排层；本轮也把 `analysisTraceItems` 纳入 schema 字段清单");
        assertThat(readme).contains("\"endpoint\": \"/api-docs/analyze\"");
        assertThat(readme).contains("\"schemaVersion\": \"v1\"");
        assertThat(readme).contains("\"contractOwner\": \"api-docs-agent\"");
        assertThat(readme).contains("\"nextActionCodeAllowedValues\": [");
        assertThat(readme).contains("\"INSPECT_PARSED_ENDPOINTS\",");
        assertThat(readme).contains("\"REVIEW_MODULE_SUMMARY\",");
        assertThat(readme).contains("\"EXECUTE_REVIEW_PRIORITY\",");
        assertThat(readme).contains("\"START_API_RISK_REVIEW\",");
        assertThat(readme).contains("\"COLLECT_OPENAPI_INPUT\",");
        assertThat(readme).contains("\"REVIEW_RISK_AND_TEST_ADVICE\"");
        assertThat(readme).contains("`contractOwner` 标识这份契约由 API 文档助手维护");
        assertThat(readme).contains("\"fields\": [");
        assertThat(readme).contains("\"name\": \"workflowStatus\"");
        assertThat(readme).contains("\"jsonType\": \"string\"");
        assertThat(readme).contains("\"required\": true");
        assertThat(readme).contains("\"readyMeaning\": \"已解析到接口，可进入风险审查\"");
        assertThat(readme).contains("\"needsInputMeaning\": \"缺少 paths 或未解析到接口\"");
        assertThat(readme).contains("\"usage\": \"作为主路由状态，决定进入审查还是补输入\"");
        assertThat(readme).contains("\"displayOrder\": 10");
        assertThat(readme).contains("\"category\": \"routing\"");
        assertThat(readme).contains("\"uiLabel\": \"工作流状态\"");
        assertThat(readme).contains("\"uiDescription\": \"判断 OpenAPI 输入是否已具备进入风险审查的条件。\"");
        assertThat(readme).contains("\"visibility\": \"summary\"");
        assertThat(readme).contains("\"renderType\": \"badge\"");
        assertThat(readme).contains("\"copyable\": true");
        assertThat(readme).contains("\"interactionHint\": \"复制状态用于路由判断\"");
        assertThat(readme).contains("\"agentAction\": \"route-by-status\"");
        assertThat(readme).contains("\"targetNode\": \"status-router-node\"");
        assertThat(readme).contains("\"nodeInputPath\": \"$.workflowStatus\"");
        assertThat(readme).contains("\"handoffPayloadKey\": \"workflowStatus\"");
        assertThat(readme).contains("\"requiredForNode\": true");
        assertThat(readme).contains("\"fallbackValue\": \"NEEDS_INPUT\"");
        assertThat(readme).contains("\"validationRule\": \"值必须为 READY 或 NEEDS_INPUT；缺失/非法时使用 fallbackValue。\"");
        assertThat(readme).contains("\"missingFieldPolicy\": \"缺失或非法时应用 fallbackValue 并路由到输入补全。\"");
        assertThat(readme).contains("\"policySeverity\": \"needs-input\"");
        assertThat(readme).contains("\"retryable\": false");
        assertThat(readme).contains("\"operatorMessage\": \"请补充有效 OpenAPI/Swagger JSON 后重新分析。\"");
        assertThat(readme).contains("\"failureEscalation\": \"openapi-input-owner\"");
        assertThat(readme).contains("\"escalationCondition\": \"当 workflowStatus 缺失、非法或持续为 NEEDS_INPUT 且无法自动补全时升级。\"");
        assertThat(readme).contains("\"failureEscalationMessage\": \"已将输入问题升级给 openapi-input-owner：请补充有效 OpenAPI/Swagger JSON 后重新分析。\"");
        assertThat(readme).contains("\"escalationPriority\": 10");
        assertThat(readme).contains("\"escalationCategory\": \"input\"");
        assertThat(readme).contains("\"escalationSla\": \"当天补齐\"");
        assertThat(readme).contains("\"escalationContact\": \"openapi-input-owner#docs-intake\"");
        assertThat(readme).contains("\"escalationRunbook\": \"openapi-input-runbook#validate-paths\"");
        assertThat(readme).contains("\"runbookStep\": \"校验 paths 并要求重新提交有效 OpenAPI/Swagger JSON。\"");
        assertThat(readme).contains("\"escalationOwnerRole\": \"OpenAPI 输入负责人\"");
        assertThat(readme).contains("\"runbookExpectedOutcome\": \"拿到包含 paths 的有效 OpenAPI/Swagger JSON，并重新分析。\"");
        assertThat(readme).contains("\"source\": \"ApiDocAnalyzerService.workflowStatus\"");
        assertThat(readme).contains("\"readyExampleValue\": \"READY\"");
        assertThat(readme).contains("\"needsInputExampleValue\": \"NEEDS_INPUT\"");
        assertThat(readme).contains("\"name\": \"reviewPromptPreview\"");
        assertThat(readme).contains("\"usage\": \"作为调试预览，不替代结构化变量\"");
        assertThat(readme).contains("\"category\": \"prompt\"");
        assertThat(readme).contains("\"uiLabel\": \"审查 Prompt 预览\"");
        assertThat(readme).contains("\"uiDescription\": \"根据结构化变量渲染出的可执行审查请求预览。\"");
        assertThat(readme).contains("\"renderType\": \"prompt-preview\"");
        assertThat(readme).contains("\"interactionHint\": \"复制 Prompt 预览给 Agent 节点\"");
        assertThat(readme).contains("\"agentAction\": \"copy-prompt-preview\"");
        assertThat(readme).contains("\"targetNode\": \"prompt-preview-node\"");
        assertThat(readme).contains("\"nodeInputPath\": \"$.reviewPromptPreview\"");
        assertThat(readme).contains("\"handoffPayloadKey\": \"reviewPromptPreview\"");
        assertThat(readme).contains("\"fallbackValue\": \"请先补充有效 OpenAPI/Swagger JSON，不要编造接口。\"");
        assertThat(readme).contains("\"validationRule\": \"应为非空审查或补输入请求；缺失/空白时使用 fallbackValue。\"");
        assertThat(readme).contains("\"missingFieldPolicy\": \"缺失或空白时应用 fallbackValue，要求先补充有效输入。\"");
        assertThat(readme).contains("\"policySeverity\": \"needs-input\"");
        assertThat(readme).contains("\"operatorMessage\": \"请先收集有效 OpenAPI/Swagger JSON，再生成审查 Prompt。\"");
        assertThat(readme).contains("\"failureEscalation\": \"prompt-input-owner\"");
        assertThat(readme).contains("\"escalationCondition\": \"当 Prompt 预览缺失、空白或无法生成非编造审查请求时升级。\"");
        assertThat(readme).contains("\"failureEscalationMessage\": \"已将 Prompt 预览问题升级给 prompt-input-owner：请先收集有效接口文档。\"");
        assertThat(readme).contains("\"escalationPriority\": 40");
        assertThat(readme).contains("\"escalationCategory\": \"prompt\"");
        assertThat(readme).contains("\"escalationSla\": \"当天收集输入\"");
        assertThat(readme).contains("\"escalationContact\": \"prompt-input-owner#prompt-intake\"");
        assertThat(readme).contains("\"escalationRunbook\": \"prompt-input-runbook#collect-non-hallucinated-prompt-input\"");
        assertThat(readme).contains("\"runbookStep\": \"收集非编造接口输入后再生成审查 Prompt。\"");
        assertThat(readme).contains("\"escalationOwnerRole\": \"Prompt 输入负责人\"");
        assertThat(readme).contains("\"runbookExpectedOutcome\": \"已收集有效接口输入，下一次可生成非编造审查 Prompt。\"");
        assertThat(readme).contains("`retryable` 标识字段缺失/非法后是否适合自动重试目标节点");
        assertThat(readme).contains("`operatorMessage` 给出不可自动处理或已降级后的中文操作提示");
        assertThat(readme).contains("`failureEscalation` 标识不可重试或无法自动处理时应升级到的上游/人工角色");
        assertThat(readme).contains("`escalationCondition` 说明触发失败升级的最小条件");
        assertThat(readme).contains("`escalationPriority` 用数字给多个升级候选排序");
        assertThat(readme).contains("`escalationCategory` 则把升级候选归入 `input`、`operator`、`prompt` 或 `fallback` 等稳定分组");
        assertThat(readme).contains("`escalationSla` 给不同升级类别提供响应期望");
        assertThat(readme).contains("`escalationContact` 给升级目标补充稳定联系入口");
        assertThat(readme).contains("`escalationRunbook` 进一步给出处理步骤入口");
        assertThat(readme).contains("`runbookStep` 把该入口拆成一句可展示、可执行的下一步说明");
        assertThat(readme).contains("`escalationOwnerRole` 标识处理该升级或降级步骤的中文责任角色");
        assertThat(readme).contains("`runbookExpectedOutcome` 描述 Runbook 步骤完成后应看到的稳定结果");
        assertThat(readme).contains("`requiredForNode` 标识字段是否是目标节点执行时的默认必需输入");
        assertThat(readme).contains("reviewPromptVariables");
        assertThat(readme).contains("\"visibility\": \"detail\"");
        assertThat(readme).contains("\"source\": \"ApiDocAnalyzerService.reviewPromptPreview\"");
        assertThat(readme).contains("\"usage\": \"给前端时间线或 Agent 编排层按 stage/status/message/nextAction/nextActionCode 展示执行过程\"");
        assertThat(readme).contains("\"nextActionCode\": \"START_API_RISK_REVIEW\"");
        assertThat(readme).contains("\"nextActionCode\": \"COLLECT_OPENAPI_INPUT\"");
        assertThat(readme).contains("\"readyExampleValue\": \"请调用 api-risk-reviewer 审查 orders 模块");
        assertThat(readme).contains("\"needsInputExampleValue\": \"请调用 openapi-input-validator 处理 INPUT_REQUIRED 阶段");
        assertThat(readme).contains("`jsonType`、`required`、`displayOrder`、`category`、`uiLabel`、`uiDescription`、`visibility`、`renderType`、`copyable`、`interactionHint`、`agentAction`、`targetNode`、`nodeInputPath`、`handoffPayloadKey`、`requiredForNode`、`fallbackValue`、`validationRule`、`missingFieldPolicy`、`policySeverity`、`retryable`、`operatorMessage`、`failureEscalation`、`escalationCondition`、`failureEscalationMessage`、`escalationPriority`、`escalationCategory`、`escalationSla`、`escalationContact`、`escalationRunbook`、`runbookStep`、`escalationOwnerRole`、`runbookExpectedOutcome`、`source`、`readyExampleValue` 和 `needsInputExampleValue` 让调试面板可以不用硬编码就渲染字段类型");
        assertThat(readme).contains("其中 `visibility` 用于给调试面板一个默认展示建议");
        assertThat(readme).contains("`summary` 字段适合在首屏直接展示，`detail` 字段适合折叠到详情区");
        assertThat(readme).contains("完整字段清单如下");
    }

    @Test
    void documentsReadyDebugResponseFields() throws IOException {
        String readme = Files.readString(Path.of("README.md"), StandardCharsets.UTF_8);

        assertThat(readme).contains("解析到接口时，可以用下面这个请求验证可审查链路");
        assertThat(readme).contains("\"workflowStatus\": \"READY\"");
        assertThat(readme).contains("\"workflowStage\": \"REVIEW_READY\"");
        assertThat(readme).contains("\"suggestedTool\": \"api-risk-reviewer\"");
        assertThat(readme).contains("\"状态：READY，可以进入 API 风险审查。\"");
        assertThat(readme).contains("\"首个动作：P1 审查 orders 模块，先审查删除接口、权限控制和误删保护。\"");
        assertThat(readme).contains("\"blockingReason\": null");
        assertThat(readme).contains("\"firstReviewModule\": \"orders\"");
        assertThat(readme).contains("请调用 api-risk-reviewer 审查 orders 模块：先审查删除接口、权限控制和误删保护；请输出风险说明、测试建议和下一步行动。");
        assertThat(readme).contains("\"analysisTrace\": [");
        assertThat(readme).contains("\"route: workflowStatus=READY，suggestedTool=api-risk-reviewer。\"");
        assertThat(readme).contains("\"analysisTraceItems\": [");
        assertThat(readme).contains("{\"stage\": \"route\", \"status\": \"READY\", \"message\": \"workflowStatus=READY，suggestedTool=api-risk-reviewer。\", \"nextAction\": \"进入 API 风险审查。\", \"nextActionCode\": \"START_API_RISK_REVIEW\"}");
    }

    @Test
    void documentsMissingInputDebugResponseFields() throws IOException {
        String readme = Files.readString(Path.of("README.md"), StandardCharsets.UTF_8);

        assertThat(readme).contains("缺少 `paths` 或未解析到接口时");
        assertThat(readme).contains("\"workflowStatus\": \"NEEDS_INPUT\"");
        assertThat(readme).contains("\"workflowStage\": \"INPUT_REQUIRED\"");
        assertThat(readme).contains("\"suggestedTool\": \"openapi-input-validator\"");
        assertThat(readme).contains("\"debugHints\": [");
        assertThat(readme).contains("\"状态：NEEDS_INPUT，暂不进入风险审查。\"");
        assertThat(readme).contains("\"firstReviewModule\": null");
        assertThat(readme).contains("请调用 openapi-input-validator 处理 INPUT_REQUIRED 阶段：OpenAPI/Swagger JSON 缺少 paths 或未解析到接口；请先补充有效输入，不要编造接口。");
        assertThat(readme).contains("\"route: workflowStatus=NEEDS_INPUT，suggestedTool=openapi-input-validator。\"");
        assertThat(readme).contains("\"analysisTraceItems\": [");
        assertThat(readme).contains("{\"stage\": \"route\", \"status\": \"NEEDS_INPUT\", \"message\": \"workflowStatus=NEEDS_INPUT，suggestedTool=openapi-input-validator。\", \"nextAction\": \"补充有效 OpenAPI/Swagger JSON。\", \"nextActionCode\": \"COLLECT_OPENAPI_INPUT\"}");
    }
}
