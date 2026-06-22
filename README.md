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

`/api-docs/analyze` 的响应里可以重点看这几个调试字段：`workflowStatus` 判断是否可进入审查，`suggestedTool` 给出下一步工具名，`debugHints` 给调试面板展示人类可读提示，`reviewPromptVariables` 保留结构化 Prompt 变量，`reviewPromptPreview` 展示可直接交给 Agent 节点执行的中文审查请求。`GET /api-docs/debug-schema` 会返回这些字段的 READY/NEEDS_INPUT 含义和前端/Agent 用法，方便调试面板不解析 README 也能渲染字段说明。

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
      "source": "ApiDocAnalyzerService.workflowStatus"
    },
    {
      "name": "reviewPromptPreview",
      "jsonType": "string",
      "required": true,
      "readyMeaning": "生成可执行的审查请求",
      "needsInputMeaning": "生成补输入请求",
      "usage": "作为调试预览，不替代结构化变量",
      "source": "ApiDocAnalyzerService.reviewPromptPreview"
    }
  ]
}
```

`schemaVersion` 用来标识这份调试字段契约的版本，前端或 Agent 编排层可以据此判断字段说明是否兼容当前渲染逻辑。`contractOwner` 标识这份契约由 API 文档助手维护，便于调试面板或编排层在多份 schema 中归属责任边界。`jsonType`、`required` 和 `source` 让调试面板可以不用硬编码就渲染字段类型、必填提示和来源定位。

完整字段清单如下：

| 字段 | READY 时含义 | NEEDS_INPUT 时含义 | 前端/Agent 用法 |
| --- | --- | --- | --- |
| `workflowStatus` | 已解析到接口，可进入风险审查 | 缺少 `paths` 或未解析到接口 | 作为主路由状态，决定进入审查还是补输入 |
| `workflowStage` | `REVIEW_READY`，审查节点可执行 | `INPUT_REQUIRED`，输入校验节点可执行 | 显示当前工作流阶段，便于调试面板分组 |
| `suggestedTool` | 推荐调用 `api-risk-reviewer` | 推荐调用 `openapi-input-validator` | 映射到下一步工具或 Agent 节点 |
| `blockingReason` | `null`，没有阻塞原因 | 返回缺输入原因 | 展示阻塞提示，避免编造接口 |
| `debugHints` | 展示状态、工具和首个审查动作 | 展示状态、工具和缺输入原因 | 给人类调试面板直接展示 |
| `reviewPromptVariables` | 输出首个模块和动作等结构化变量 | 首个模块/动作保持 `null` | 给 PromptTemplate 或工作流节点传参 |
| `reviewPromptPreview` | 生成可执行的审查请求 | 生成补输入请求 | 作为调试预览，不替代结构化变量 |

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
