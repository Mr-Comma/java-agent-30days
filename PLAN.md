# Java Agent 30-Day Learning Plan

> 目标：面向 Java 程序员，做出一个可持续迭代的 Agent 学习与实战项目。

## 总目标

- 学会 Java 侧 Agent 核心能力：LLM 接入、Tool Calling、RAG、记忆、工作流编排、MCP、流式输出、审计与权限。
- 每天完成一个最小可运行增量，并沉淀一条 GitHub 记录。
- 最终形成一个可展示的 Java Agent 项目模板，方便继续扩展成企业级助手。

## 推荐技术栈

- Java 17+
- Spring Boot 3
- LangChain4j
- Spring AI / Spring AI Alibaba
- LangGraph4j
- SSE / WebSocket
- Redis / MySQL（后期）
- 本地文件 / Swagger / GitHub API / Jira 或类 Jira 工具

## 30 天路线

### 第 1 周：跑通基础 Agent

Day 1：选定仓库结构，跑通最小 Spring Boot + LLM 调用。
- 产出：项目骨架、README、第一条学习记录。

Day 2：接入第一个模型，输出流式响应。
- 产出：`/chat` 接口。

Day 3：加一个简单工具调用。
- 产出：`weather` 或 `time` 工具。

Day 4：加入系统提示词和角色定义。
- 产出：prompt 配置化。

Day 5：增加对话上下文。
- 产出：多轮聊天。

Day 6：写一个单元测试，验证工具调用。
- 产出：测试用例。

Day 7：周总结，整理踩坑和学习笔记。
- 产出：周报文档。

### 第 2 周：学习开源 Agent 架构

Day 8：阅读 langchain4j 核心示例。
Day 9：模仿一个 tool calling demo。
Day 10：学习 LangGraph4j 的图编排。
Day 11：尝试用状态机组织任务。
Day 12：引入 RAG 的最小闭环。
Day 13：给知识库加本地文档读取。
Day 14：整理“哪些模式适合 Java 企业场景”。

### 第 3 周：做程序员助手

Day 15：读取 Git 仓库结构。
Day 16：自动总结代码模块。
Day 17：生成接口文档草稿。
Day 18：根据 Swagger 生成工具描述。
Day 19：生成测试建议。
Day 20：分析日志并给出修复建议。
Day 21：周总结，沉淀成 README 亮点。

### 第 4 周：做企业化能力

Day 22：加入权限分层。
Day 23：加入操作审计。
Day 24：加入任务队列或异步执行。
Day 25：加入失败重试与回滚提示。
Day 26：加入记忆存储。
Day 27：加入配置中心/环境切换。
Day 28：做一个 Web UI 或 API 文档页。
Day 29：整理架构图和演示脚本。
Day 30：收尾，写总复盘和 GitHub 项目说明。

## 每日固定动作

- 看一个开源项目或一个源码片段。
- 做一个最小功能。
- 写一条 GitHub 记录。
- 记录今天学到的一个观点。

## 今天开始的第 1 天目标

- 确定项目名和目录结构。
- 建好本地仓库。
- 写下学习路线与今天的执行记录。
- 为明天的模型接入预留入口。
