# Java Agent 30 Days

一个面向 Java 程序员的 AI Agent 30 天学习与实战项目。

## 目标

每天完成一个可运行的小迭代，长期沉淀成一个 Java 企业级 Agent 项目模板。

## 主线

1. Java Agent 基础：LLM、Prompt、Tool Calling、上下文、多轮对话。
2. 开源项目学习：LangChain4j、Spring AI Alibaba、LangGraph4j、Hermes Agent。
3. 程序员助手：代码理解、接口文档、日志分析、测试建议。
4. 企业级能力：权限、审计、异步任务、记忆、可观测性。

## 每日记录

- 计划：`PLAN.md`
- 日志：`logs/`

## 第一个作品方向

Java Agent API 文档助手：输入 Swagger / Knife4j 地址，自动识别模块、生成接口文档、分析风险、生成测试建议。

## 当前可运行骨架

本仓库已包含一个最小 Spring Boot Agent 骨架，当前先用 mock 响应保留 `/chat` 入口，并支持识别时间问题后调用内置 `time` 工具；mock agent 的角色名和空 prompt 默认问题可通过 `src/main/resources/application.yml` 的 `agent.chat` 配置调整。`/chat` 还支持用 `sessionId` 做最小内存上下文，响应会返回当前轮次和上一轮 prompt。

API 文档助手方向已增加一个最小 OpenAPI/Swagger JSON 解析入口：`POST /api-docs/parse` 会从 `paths` 中抽取接口方法、路径和摘要；`POST /api-docs/analyze` 会基于解析出的接口生成确定性的中文摘要、模块聚合视图、模块风险等级、模块风险分布、模块审查优先级、最小审查计划、首要审查模块、审查排序原因、Agent 分析角色、人类可读事实、结构化事实列表、工作流状态、工作流阶段、建议工具、审查 Prompt 模板、审查 Prompt 变量、审查 Prompt 预览、阻塞原因、推荐下一步行动、拆分后的任务目标/约束/期望输出、工作流执行清单、分析轨迹、任务、风险提示和测试建议 mock，作为后续接入 LLM 分析与工作流编排前的稳定领域能力。后续逐步替换为真实 LLM、流式输出和更多工具调用。

```bash
mvn test
mvn spring-boot:run
curl "http://localhost:8080/chat?prompt=hello"
curl --get --data-urlencode "prompt=现在几点" "http://localhost:8080/chat"
curl --get --data-urlencode "sessionId=api-docs" --data-urlencode "prompt=继续分析接口" "http://localhost:8080/chat"
curl -X POST http://localhost:8080/api-docs/parse \
  -H 'Content-Type: application/json' \
  -d '{"openApiJson":"{\"openapi\":\"3.0.1\",\"paths\":{\"/users\":{\"get\":{\"summary\":\"List users\"}}}}"}'
curl -X POST http://localhost:8080/api-docs/analyze \
  -H 'Content-Type: application/json' \
  -d '{"openApiJson":"{\"openapi\":\"3.0.1\",\"paths\":{\"/users\":{\"get\":{\"summary\":\"List users\"},\"post\":{\"summary\":\"Create user\"}}}}"}'
curl http://localhost:8080/api-docs/debug-schema
```

`/api-docs/analyze` 的响应里可以重点看这几个调试字段：`workflowStatus` 判断是否可进入审查，`suggestedTool` 给出下一步工具名，`debugHints` 给调试面板展示人类可读提示，`reviewPromptVariables` 保留结构化 Prompt 变量，`reviewPromptPreview` 展示可直接交给 Agent 节点执行的中文审查请求。`GET /api-docs/debug-schema` 会返回这些字段的 READY/NEEDS_INPUT 含义和前端/Agent 用法，并在完整字段表里列出顺序、分组、中文标题、UI 说明、默认可见性、渲染类型、可复制建议、交互提示、Agent 动作、目标节点、节点输入路径、交接 payload 键、节点必需性、降级值、校验规则、缺字段策略、策略级别、可重试建议、操作提示、失败升级角色、升级条件、升级提示、升级优先级、升级类别和来源定位，方便调试面板不解析 README 也能渲染字段说明。

`debug-schema` 的响应结构稳定面向前端和 Agent 编排层：

```json
{
  "endpoint": "/api-docs/analyze",
  "schemaVersion": "v1",
  "contractOwner": "api-docs-agent",
  "fields": [
    {
      "name": "workflowStatus",
      "jsonType": "string",
      "required": true,
      "readyMeaning": "已解析到接口，可进入风险审查",
      "needsInputMeaning": "缺少 paths 或未解析到接口",
      "usage": "作为主路由状态，决定进入审查还是补输入",
      "displayOrder": 10,
      "category": "routing",
      "uiLabel": "工作流状态",
      "uiDescription": "判断 OpenAPI 输入是否已具备进入风险审查的条件。",
      "visibility": "summary",
      "renderType": "badge",
      "copyable": true,
      "interactionHint": "复制状态用于路由判断",
      "agentAction": "route-by-status",
      "targetNode": "status-router-node",
      "nodeInputPath": "$.workflowStatus",
      "handoffPayloadKey": "workflowStatus",
      "requiredForNode": true,
      "fallbackValue": "NEEDS_INPUT",
      "validationRule": "值必须为 READY 或 NEEDS_INPUT；缺失/非法时使用 fallbackValue。",
      "missingFieldPolicy": "缺失或非法时应用 fallbackValue 并路由到输入补全。",
      "policySeverity": "needs-input",
      "retryable": false,
      "operatorMessage": "请补充有效 OpenAPI/Swagger JSON 后重新分析。",
      "failureEscalation": "openapi-input-owner",
      "escalationCondition": "当 workflowStatus 缺失、非法或持续为 NEEDS_INPUT 且无法自动补全时升级。",
      "failureEscalationMessage": "已将输入问题升级给 openapi-input-owner：请补充有效 OpenAPI/Swagger JSON 后重新分析。",
      "escalationPriority": 10,
      "escalationCategory": "input",
      "source": "ApiDocAnalyzerService.workflowStatus",
      "readyExampleValue": "READY",
      "needsInputExampleValue": "NEEDS_INPUT"
    },
    {
      "name": "reviewPromptVariables",
      "jsonType": "object",
      "required": true,
      "readyMeaning": "输出首个模块和动作等结构化变量",
      "needsInputMeaning": "首个模块/动作保持 null",
      "usage": "给 PromptTemplate 或工作流节点传参",
      "displayOrder": 60,
      "category": "prompt",
      "uiLabel": "审查 Prompt 变量",
      "uiDescription": "传给 PromptTemplate 或工作流节点的结构化变量。",
      "visibility": "detail",
      "renderType": "json",
      "copyable": false,
      "interactionHint": "展开查看结构化 Prompt 变量",
      "agentAction": "bind-prompt-variables",
      "targetNode": "prompt-template-node",
      "nodeInputPath": "$.reviewPromptVariables",
      "handoffPayloadKey": "reviewPromptVariables",
      "requiredForNode": true,
      "fallbackValue": {
        "workflowStage": "INPUT_REQUIRED",
        "suggestedTool": "openapi-input-validator",
        "blockingReason": "OpenAPI/Swagger JSON 缺少 paths 或未解析到接口。",
        "firstReviewModule": null,
        "firstReviewAction": null
      },
      "validationRule": "应包含 workflowStage、suggestedTool、blockingReason、firstReviewModule、firstReviewAction；缺失时使用 fallbackValue。",
      "missingFieldPolicy": "缺失固定键时应用 fallbackValue，避免 PromptTemplate 变量漂移。",
      "policySeverity": "fallback",
      "retryable": true,
      "operatorMessage": "已使用 INPUT_REQUIRED 变量对象，可继续渲染补输入 Prompt。",
      "failureEscalation": "none",
      "escalationCondition": "无需升级；缺失固定键时使用 fallbackValue 继续渲染补输入 Prompt。",
      "failureEscalationMessage": "无需升级：已使用 INPUT_REQUIRED 变量对象继续渲染补输入 Prompt。",
      "escalationPriority": 90,
      "escalationCategory": "fallback",
      "source": "ApiDocAnalyzerService.reviewPromptVariables"
    },
    {
      "name": "reviewPromptPreview",
      "jsonType": "string",
      "required": true,
      "readyMeaning": "生成可执行的审查请求",
      "needsInputMeaning": "生成补输入请求",
      "usage": "作为调试预览，不替代结构化变量",
      "displayOrder": 70,
      "category": "prompt",
      "uiLabel": "审查 Prompt 预览",
      "uiDescription": "根据结构化变量渲染出的可执行审查请求预览。",
      "visibility": "summary",
      "renderType": "prompt-preview",
      "copyable": true,
      "interactionHint": "复制 Prompt 预览给 Agent 节点",
      "agentAction": "copy-prompt-preview",
      "targetNode": "prompt-preview-node",
      "nodeInputPath": "$.reviewPromptPreview",
      "handoffPayloadKey": "reviewPromptPreview",
      "requiredForNode": true,
      "fallbackValue": "请先补充有效 OpenAPI/Swagger JSON，不要编造接口。",
      "validationRule": "应为非空审查或补输入请求；缺失/空白时使用 fallbackValue。",
      "missingFieldPolicy": "缺失或空白时应用 fallbackValue，要求先补充有效输入。",
      "policySeverity": "needs-input",
      "retryable": false,
      "operatorMessage": "请先收集有效 OpenAPI/Swagger JSON，再生成审查 Prompt。",
      "failureEscalation": "prompt-input-owner",
      "escalationCondition": "当 Prompt 预览缺失、空白或无法生成非编造审查请求时升级。",
      "failureEscalationMessage": "已将 Prompt 预览问题升级给 prompt-input-owner：请先收集有效接口文档。",
      "escalationPriority": 40,
      "escalationCategory": "prompt",
      "source": "ApiDocAnalyzerService.reviewPromptPreview",
      "readyExampleValue": "请调用 api-risk-reviewer 审查 orders 模块：先审查删除接口、权限控制和误删保护；请输出风险说明、测试建议和下一步行动。",
      "needsInputExampleValue": "请调用 openapi-input-validator 处理 INPUT_REQUIRED 阶段：OpenAPI/Swagger JSON 缺少 paths 或未解析到接口；请先补充有效输入，不要编造接口。"
    }
  ]
}
```

`schemaVersion` 用来标识这份调试字段契约的版本，前端或 Agent 编排层可以据此判断字段说明是否兼容当前渲染逻辑。`contractOwner` 标识这份契约由 API 文档助手维护，便于调试面板或编排层在多份 schema 中归属责任边界。`jsonType`、`required`、`displayOrder`、`category`、`uiLabel`、`uiDescription`、`visibility`、`renderType`、`copyable`、`interactionHint`、`agentAction`、`targetNode`、`nodeInputPath`、`handoffPayloadKey`、`requiredForNode`、`fallbackValue`、`validationRule`、`missingFieldPolicy`、`policySeverity`、`retryable`、`operatorMessage`、`failureEscalation`、`escalationCondition`、`failureEscalationMessage`、`escalationPriority`、`escalationCategory`、`source`、`readyExampleValue` 和 `needsInputExampleValue` 让调试面板可以不用硬编码就渲染字段类型、必填提示、展示顺序、字段分组、中文标题、字段说明、默认可见性、推荐渲染组件、是否可复制、交互提示、Agent 动作、目标节点、节点输入路径、交接 payload 键、节点必需性、降级值、节点输入校验规则、缺字段处理策略、策略级别、自动重试建议、操作提示、失败升级角色、升级触发条件、升级提示、升级排序、升级分类、来源定位和双路径最小样例。

其中 `visibility` 用于给调试面板一个默认展示建议：`summary` 字段适合在首屏直接展示，`detail` 字段适合折叠到详情区；`renderType` 用于建议字段的默认展示组件，例如状态徽标、工具链接、列表、JSON 或 Prompt 预览；`copyable` 则告诉前端该字段是否适合一键复制；`interactionHint` 进一步说明复制、展开或触发工具节点等默认交互；`agentAction` 给 Agent 编排层一个稳定的机器动作标识；`targetNode` 把动作映射到具体工作流节点名；`nodeInputPath` 说明节点应从 `/api-docs/analyze` 响应读取哪个字段作为输入；`handoffPayloadKey` 则说明读取后组装到节点 payload 时使用的稳定键名；`requiredForNode` 标识字段是否是目标节点执行时的默认必需输入；`fallbackValue` 说明节点缺少字段时可采用的安全降级值；`validationRule` 描述节点应用降级值前的最小输入校验规则；`missingFieldPolicy` 描述字段缺失或非法时节点应采用降级、补输入或重新分析提示；`policySeverity` 用稳定枚举区分可自动降级、需要补输入或建议重新分析；`retryable` 标识字段缺失/非法后是否适合自动重试目标节点；`operatorMessage` 给出不可自动处理或已降级后的中文操作提示；`failureEscalation` 标识不可重试或无法自动处理时应升级到的上游/人工角色；`escalationCondition` 说明触发失败升级的最小条件；`failureEscalationMessage` 给调试面板展示稳定的升级结果提示；`escalationPriority` 用数字给多个升级候选排序，数值越小越应优先处理；`escalationCategory` 则把升级候选归入 `input`、`operator`、`prompt` 或 `fallback` 等稳定分组，方便调试面板按类别筛选。它们都只描述 UI/编排建议，不改变 `/api-docs/analyze` 的业务行为。

完整字段清单如下：

| 字段 | 顺序 | 分组 | 中文标题 | UI 说明 | 可见性 | 渲染类型 | 可复制 | 交互提示 | Agent 动作 | 目标节点 | 节点输入路径 | 交接 payload 键 | 节点必需 | 降级值 | 校验规则 | 缺字段策略 | 策略级别 | 可重试 | 操作提示 | 失败升级 | 升级条件 | 升级提示 | 升级优先级 | 升级类别 | READY 时含义 | NEEDS_INPUT 时含义 | 前端/Agent 用法 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `workflowStatus` | 10 | `routing` | 工作流状态 | 判断 OpenAPI 输入是否已具备进入风险审查的条件。 | `summary` | `badge` | `是` | 复制状态用于路由判断 | `route-by-status` | `status-router-node` | `$.workflowStatus` | `workflowStatus` | `是` | `NEEDS_INPUT` | 值必须为 `READY` 或 `NEEDS_INPUT`；缺失/非法时使用 `fallbackValue`。 | 缺失或非法时应用 `fallbackValue` 并路由到输入补全。 | `needs-input` | `否` | 请补充有效 OpenAPI/Swagger JSON 后重新分析。 | `openapi-input-owner` | 当 `workflowStatus` 缺失、非法或持续为 `NEEDS_INPUT` 且无法自动补全时升级。 | 已将输入问题升级给 openapi-input-owner：请补充有效 OpenAPI/Swagger JSON 后重新分析。 | 10 | `input` | 已解析到接口，可进入风险审查 | 缺少 `paths` 或未解析到接口 | 作为主路由状态，决定进入审查还是补输入 |
| `workflowStage` | 20 | `routing` | 工作流阶段 | 标识当前应进入审查节点还是输入补全节点。 | `summary` | `badge` | `是` | 复制阶段用于工作流分支 | `branch-by-stage` | `workflow-branch-node` | `$.workflowStage` | `workflowStage` | `是` | `INPUT_REQUIRED` | 值必须为 `REVIEW_READY` 或 `INPUT_REQUIRED`；缺失/非法时使用 `fallbackValue`。 | 缺失或非法时应用 `fallbackValue` 并进入输入补全阶段。 | `needs-input` | `否` | 请补充有效 OpenAPI/Swagger JSON 后重新分析。 | `openapi-input-owner` | 当 `workflowStage` 缺失、非法或持续为 `INPUT_REQUIRED` 且无法进入审查阶段时升级。 | 已将阶段问题升级给 openapi-input-owner：请确认输入可进入审查阶段。 | 20 | `input` | `REVIEW_READY`，审查节点可执行 | `INPUT_REQUIRED`，输入校验节点可执行 | 显示当前工作流阶段，便于调试面板分组 |
| `suggestedTool` | 30 | `routing` | 建议工具 | 给出下一步建议调用的 Agent 工具或工作流节点。 | `summary` | `tool-link` | `是` | 触发建议工具节点 | `invoke-suggested-tool` | `tool-dispatch-node` | `$.suggestedTool` | `suggestedTool` | `是` | `openapi-input-validator` | 值必须为 `api-risk-reviewer` 或 `openapi-input-validator`；缺失/非法时使用 `fallbackValue`。 | 缺失或非法时应用 `fallbackValue`，避免调用未知工具。 | `fallback` | `是` | 已自动降级为 openapi-input-validator，可继续分发到工具节点。 | `none` | 无需升级；缺失或非法时使用 `fallbackValue` 继续工具分发。 | 无需升级：已降级到 openapi-input-validator 并可继续分发。 | 90 | `fallback` | 推荐调用 `api-risk-reviewer` | 推荐调用 `openapi-input-validator` | 映射到下一步工具或 Agent 节点 |
| `blockingReason` | 40 | `routing` | 阻塞原因 | 说明当前阻塞原因；READY 时为空。 | `summary` | `text` | `是` | 复制阻塞原因用于补输入说明 | `collect-missing-input` | `input-collector-node` | `$.blockingReason` | `blockingReason` | `否` | `null` | `READY` 时允许为 `null`；`NEEDS_INPUT` 时应为非空缺输入说明。 | `READY` 可缺省为 `null`；`NEEDS_INPUT` 缺失时提示重新运行分析。 | `rerun-analysis` | `否` | 请将阻塞原因展示给用户，并要求重新提交有效接口文档。 | `api-docs-operator` | 当 `NEEDS_INPUT` 路径缺少阻塞原因或原因无法指导补输入时升级。 | 已将阻塞原因问题升级给 api-docs-operator：请展示原因并要求重新提交接口文档。 | 30 | `operator` | `null`，没有阻塞原因 | 返回缺输入原因 | 展示阻塞提示，避免编造接口 |
| `debugHints` | 50 | `human-hint` | 调试提示 | 给调试面板展示的人类可读运行提示。 | `summary` | `list` | `否` | 展开查看人类调试提示 | `show-human-hints` | `debug-panel-node` | `$.debugHints` | `debugHints` | `是` | 请重新运行 `/api-docs/analyze` 获取调试提示。 | 应为非空字符串列表；缺失/为空时使用 `fallbackValue`。 | 缺失或为空时应用 `fallbackValue`，保持调试面板有提示。 | `fallback` | `是` | 已使用默认调试提示，可继续展示给调试面板。 | `none` | 无需升级；缺失或为空时使用 `fallbackValue` 继续展示。 | 无需升级：已使用默认调试提示继续展示。 | 90 | `fallback` | 展示状态、工具和首个审查动作 | 展示状态、工具和缺输入原因 | 给人类调试面板直接展示 |
| `reviewPromptVariables` | 60 | `prompt` | 审查 Prompt 变量 | 传给 PromptTemplate 或工作流节点的结构化变量。 | `detail` | `json` | `否` | 展开查看结构化 Prompt 变量 | `bind-prompt-variables` | `prompt-template-node` | `$.reviewPromptVariables` | `reviewPromptVariables` | `是` | INPUT_REQUIRED 变量对象 | 应包含 `workflowStage`、`suggestedTool`、`blockingReason`、`firstReviewModule`、`firstReviewAction`；缺失时使用 `fallbackValue`。 | 缺失固定键时应用 `fallbackValue`，避免 PromptTemplate 变量漂移。 | `fallback` | `是` | 已使用 INPUT_REQUIRED 变量对象，可继续渲染补输入 Prompt。 | `none` | 无需升级；缺失固定键时使用 `fallbackValue` 继续渲染补输入 Prompt。 | 无需升级：已使用 INPUT_REQUIRED 变量对象继续渲染补输入 Prompt。 | 90 | `fallback` | 输出首个模块和动作等结构化变量 | 首个模块/动作保持 `null` | 给 PromptTemplate 或工作流节点传参 |
| `reviewPromptPreview` | 70 | `prompt` | 审查 Prompt 预览 | 根据结构化变量渲染出的可执行审查请求预览。 | `summary` | `prompt-preview` | `是` | 复制 Prompt 预览给 Agent 节点 | `copy-prompt-preview` | `prompt-preview-node` | `$.reviewPromptPreview` | `reviewPromptPreview` | `是` | 请先补充有效 OpenAPI/Swagger JSON，不要编造接口。 | 应为非空审查或补输入请求；缺失/空白时使用 `fallbackValue`。 | 缺失或空白时应用 `fallbackValue`，要求先补充有效输入。 | `needs-input` | `否` | 请先收集有效 OpenAPI/Swagger JSON，再生成审查 Prompt。 | `prompt-input-owner` | 当 Prompt 预览缺失、空白或无法生成非编造审查请求时升级。 | 已将 Prompt 预览问题升级给 prompt-input-owner：请先收集有效接口文档。 | 40 | `prompt` | 生成可执行的审查请求 | 生成补输入请求 | 作为调试预览，不替代结构化变量 |

解析到接口时，可以用下面这个请求验证可审查链路：

```bash
curl -X POST http://localhost:8080/api-docs/analyze \
  -H 'Content-Type: application/json' \
  -d '{"openApiJson":"{\"openapi\":\"3.0.1\",\"paths\":{\"/orders/{id}\":{\"delete\":{\"summary\":\"Delete order\"}},\"/users\":{\"get\":{\"summary\":\"List users\"}}}}"}'
```

关键响应字段会稳定指向首个审查动作，方便前端或 Agent 节点直接路由：

```json
{
  "workflowStatus": "READY",
  "workflowStage": "REVIEW_READY",
  "suggestedTool": "api-risk-reviewer",
  "debugHints": [
    "状态：READY，可以进入 API 风险审查。",
    "工具：调用 api-risk-reviewer 执行首个审查动作。",
    "首个动作：P1 审查 orders 模块，先审查删除接口、权限控制和误删保护。"
  ],
  "reviewPromptVariables": {
    "workflowStage": "REVIEW_READY",
    "suggestedTool": "api-risk-reviewer",
    "blockingReason": null,
    "firstReviewModule": "orders",
    "firstReviewAction": "先审查删除接口、权限控制和误删保护。"
  },
  "reviewPromptPreview": "请调用 api-risk-reviewer 审查 orders 模块：先审查删除接口、权限控制和误删保护；请输出风险说明、测试建议和下一步行动。"
}
```

缺少 `paths` 或未解析到接口时，可以用下面这个最小请求验证缺输入链路：

```bash
curl -X POST http://localhost:8080/api-docs/analyze \
  -H 'Content-Type: application/json' \
  -d '{"openApiJson":"{\"openapi\":\"3.0.1\",\"paths\":{}}"}'
```

关键响应字段会稳定指向输入补全，而不是编造接口：

```json
{
  "workflowStatus": "NEEDS_INPUT",
  "workflowStage": "INPUT_REQUIRED",
  "blockingReason": "OpenAPI/Swagger JSON 缺少 paths 或未解析到接口。",
  "suggestedTool": "openapi-input-validator",
  "debugHints": [
    "状态：NEEDS_INPUT，暂不进入风险审查。",
    "工具：调用 openapi-input-validator 校验输入。",
    "原因：OpenAPI/Swagger JSON 缺少 paths 或未解析到接口。"
  ],
  "reviewPromptVariables": {
    "workflowStage": "INPUT_REQUIRED",
    "suggestedTool": "openapi-input-validator",
    "blockingReason": "OpenAPI/Swagger JSON 缺少 paths 或未解析到接口。",
    "firstReviewModule": null,
    "firstReviewAction": null
  },
  "reviewPromptPreview": "请调用 openapi-input-validator 处理 INPUT_REQUIRED 阶段：OpenAPI/Swagger JSON 缺少 paths 或未解析到接口；请先补充有效输入，不要编造接口。"
}
```
