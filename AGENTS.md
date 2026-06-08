# AGENTS.md

## 项目定位

这是 comma 的 Java Agent 30 天学习与实战仓库。目标是每天形成一个短小、可运行、可提交到 GitHub 的 Java Agent 迭代。

## 自动迭代规则

- 每次只推进一个小步，不做大而全重构。
- 优先选择能运行、能记录、能提交的任务。
- 每次运行前先看 `PLAN.md`、`DAILY_RULE.md`、`logs/` 最新记录和 `run-reports/` 最新报告。
- 如果当天还没有日志，先创建当天日志。
- 如果能实现代码，就创建最小 Maven/Spring/Java 示例；如果暂时不适合写代码，就补齐计划、任务拆分或学习记录。
- 每次结束前运行可用验证命令，例如 `git status`、`mvn test` 或脚本检查。
- 每次自动运行必须写入 `run-reports/autonomous-YYYYMMDD-HHMM.md`。
- 如产生代码或文档变更，提交 git commit，并尝试 push 到 `origin main`。
- 不要递归创建 cron job。
- 遇到阻塞时记录到报告，不要等待用户现场确认。

## 安全边界

- 不要改动本仓库外的业务项目。
- 不要删除用户历史资料。
- 不要读取或输出私钥、token、密码。
