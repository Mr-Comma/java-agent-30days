package dev.comma.agent.apidoc;

public record ApiAnalysisTraceItem(
        String stage,
        String status,
        String message,
        String nextAction,
        String nextActionCode) {
}
