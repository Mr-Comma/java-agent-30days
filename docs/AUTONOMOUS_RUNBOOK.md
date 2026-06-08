# Java Agent 30 天自动推进 Runbook

## 目录

- 项目根目录：`/Users/haiwang/Desktop/Java-Agent-Lab/my-agents/java-agent-30days`
- 运行报告：`run-reports/`
- 子任务记录：`agent-runs/`
- 每日学习记录：`logs/`

## 自动化任务

当前采用两类任务：

1. 18 点前冲刺任务：当天立即推进一次仓库，补齐可运行骨架或学习记录，并尝试 commit/push。
2. 长期自动迭代任务：每天固定时间检查计划、推进一个小迭代、记录报告、提交并 push。

## 手动检查命令

```bash
cd /Users/haiwang/Desktop/Java-Agent-Lab/my-agents/java-agent-30days
git status --short --branch
git log --oneline -5
git remote -v
python3 scripts/autonomous_tick.py
```

如果后续项目变成 Maven 项目，可补充：

```bash
mvn test
```

## 自动 tick 说明

`scripts/autonomous_tick.py` 是无模型、轻量检查脚本。它会：

- 检查关键文件是否存在。
- 检查 git 状态和最近提交。
- 输出一份 JSON 检查结果。

模型驱动的 cron job 会在此基础上决定下一步推进什么。
