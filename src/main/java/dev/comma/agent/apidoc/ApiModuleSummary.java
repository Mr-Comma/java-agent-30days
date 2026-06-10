package dev.comma.agent.apidoc;

public record ApiModuleSummary(
        String module, int endpointCount, int writeOperationCount, String riskLevel, String testFocus) {
}
