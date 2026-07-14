package dev.comma.agent.apidoc;

import java.util.Arrays;
import java.util.List;

public enum ApiAnalysisNextActionCode {
    INSPECT_PARSED_ENDPOINTS,
    REVIEW_MODULE_SUMMARY,
    EXECUTE_REVIEW_PRIORITY,
    START_API_RISK_REVIEW,
    COLLECT_OPENAPI_INPUT,
    REVIEW_RISK_AND_TEST_ADVICE;

    public static List<String> allowedValues() {
        return Arrays.stream(values())
                .map(Enum::name)
                .toList();
    }
}
