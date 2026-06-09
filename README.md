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

API 文档助手方向已增加一个最小 OpenAPI/Swagger JSON 解析入口：`POST /api-docs/parse` 会从 `paths` 中抽取接口方法、路径和摘要；`POST /api-docs/analyze` 会基于解析出的接口生成确定性的中文摘要、模块聚合视图、风险提示和测试建议 mock，作为后续接入 LLM 分析与工作流编排前的稳定领域能力。后续逐步替换为真实 LLM、流式输出和更多工具调用。

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
```
