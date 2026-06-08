#!/usr/bin/env python3
import json
import subprocess
from datetime import datetime
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

def run(cmd):
    proc = subprocess.run(cmd, cwd=ROOT, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    return {"cmd": cmd, "code": proc.returncode, "output": proc.stdout.strip()}

required = ["README.md", "PLAN.md", "DAILY_RULE.md", "logs", "docs/AUTONOMOUS_RUNBOOK.md", "AGENTS.md"]
missing = [item for item in required if not (ROOT / item).exists()]
result = {
    "time": datetime.now().isoformat(timespec="seconds"),
    "root": str(ROOT),
    "missing": missing,
    "git_status": run(["git", "status", "--short", "--branch"]),
    "git_log": run(["git", "log", "--oneline", "-5"]),
    "remote": run(["git", "remote", "-v"]),
}
print(json.dumps(result, ensure_ascii=False, indent=2))
raise SystemExit(1 if missing else 0)
