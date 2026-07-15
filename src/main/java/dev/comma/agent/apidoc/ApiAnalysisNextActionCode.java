package dev.comma.agent.apidoc;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public static Map<String, List<String>> allowedValuesByStage() {
        Map<String, List<String>> values = new LinkedHashMap<>();
        values.put("parse", List.of(INSPECT_PARSED_ENDPOINTS.name()));
        values.put("aggregate", List.of(REVIEW_MODULE_SUMMARY.name()));
        values.put("prioritize", List.of(EXECUTE_REVIEW_PRIORITY.name()));
        values.put("route", List.of(START_API_RISK_REVIEW.name(), COLLECT_OPENAPI_INPUT.name()));
        values.put("advise", List.of(REVIEW_RISK_AND_TEST_ADVICE.name()));
        return values;
    }
}
