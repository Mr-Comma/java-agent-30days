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

        assertThat(readme).contains("| 字段 | 顺序 | 分组 | 中文标题 | UI 说明 | 可见性 | 渲染类型 | 可复制 | 交互提示 | Agent 动作 | 目标节点 | 节点输入路径 | 交接 payload 键 | 节点必需 | 降级值 | READY 时含义 | NEEDS_INPUT 时含义 | 前端/Agent 用法 |");
        assertThat(readme).contains("| `workflowStatus` | 10 | `routing` | 工作流状态 | 判断 OpenAPI 输入是否已具备进入风险审查的条件。 | `summary` | `badge` | `是` | 复制状态用于路由判断 | `route-by-status` | `status-router-node` | `$.workflowStatus` | `workflowStatus` | `是` | `NEEDS_INPUT` | 已解析到接口，可进入风险审查 | 缺少 `paths` 或未解析到接口 | 作为主路由状态，决定进入审查还是补输入 |");
        assertThat(readme).contains("| `suggestedTool` | 30 | `routing` | 建议工具 | 给出下一步建议调用的 Agent 工具或工作流节点。 | `summary` | `tool-link` | `是` | 触发建议工具节点 | `invoke-suggested-tool` | `tool-dispatch-node` | `$.suggestedTool` | `suggestedTool` | `是` | `openapi-input-validator` | 推荐调用 `api-risk-reviewer` | 推荐调用 `openapi-input-validator` | 映射到下一步工具或 Agent 节点 |");
        assertThat(readme).contains("| `blockingReason` | 40 | `routing` | 阻塞原因 | 说明当前阻塞原因；READY 时为空。 | `summary` | `text` | `是` | 复制阻塞原因用于补输入说明 | `collect-missing-input` | `input-collector-node` | `$.blockingReason` | `blockingReason` | `否` | `null` | `null`，没有阻塞原因 | 返回缺输入原因 | 展示阻塞提示，避免编造接口 |");
        assertThat(readme).contains("| `reviewPromptVariables` | 60 | `prompt` | 审查 Prompt 变量 | 传给 PromptTemplate 或工作流节点的结构化变量。 | `detail` | `json` | `否` | 展开查看结构化 Prompt 变量 | `bind-prompt-variables` | `prompt-template-node` | `$.reviewPromptVariables` | `reviewPromptVariables` | `是` | INPUT_REQUIRED 变量对象 | 输出首个模块和动作等结构化变量 | 首个模块/动作保持 `null` | 给 PromptTemplate 或工作流节点传参 |");
        assertThat(readme).contains("| `reviewPromptPreview` | 70 | `prompt` | 审查 Prompt 预览 | 根据结构化变量渲染出的可执行审查请求预览。 | `summary` | `prompt-preview` | `是` | 复制 Prompt 预览给 Agent 节点 | `copy-prompt-preview` | `prompt-preview-node` | `$.reviewPromptPreview` | `reviewPromptPreview` | `是` | 请先补充有效 OpenAPI/Swagger JSON，不要编造接口。 | 生成可执行的审查请求 | 生成补输入请求 | 作为调试预览，不替代结构化变量 |");
    }

    @Test
    void documentsDebugSchemaResponseExample() throws IOException {
        String readme = Files.readString(Path.of("README.md"), StandardCharsets.UTF_8);

        assertThat(readme).contains("`debug-schema` 的响应结构稳定面向前端和 Agent 编排层");
        assertThat(readme).contains("\"endpoint\": \"/api-docs/analyze\"");
        assertThat(readme).contains("\"schemaVersion\": \"v1\"");
        assertThat(readme).contains("\"contractOwner\": \"api-docs-agent\"");
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
        assertThat(readme).contains("`requiredForNode` 标识字段是否是目标节点执行时的默认必需输入");
        assertThat(readme).contains("reviewPromptVariables");
        assertThat(readme).contains("\"visibility\": \"detail\"");
        assertThat(readme).contains("\"source\": \"ApiDocAnalyzerService.reviewPromptPreview\"");
        assertThat(readme).contains("\"readyExampleValue\": \"请调用 api-risk-reviewer 审查 orders 模块");
        assertThat(readme).contains("\"needsInputExampleValue\": \"请调用 openapi-input-validator 处理 INPUT_REQUIRED 阶段");
        assertThat(readme).contains("`jsonType`、`required`、`displayOrder`、`category`、`uiLabel`、`uiDescription`、`visibility`、`renderType`、`copyable`、`interactionHint`、`agentAction`、`targetNode`、`nodeInputPath`、`handoffPayloadKey`、`requiredForNode`、`fallbackValue`、`source`、`readyExampleValue` 和 `needsInputExampleValue` 让调试面板可以不用硬编码就渲染字段类型、必填提示、展示顺序、字段分组、中文标题、字段说明、默认可见性、推荐渲染组件、是否可复制、交互提示、Agent 动作、目标节点、节点输入路径、交接 payload 键、节点必需性、降级值、来源定位和双路径最小样例。");
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
    }
}
