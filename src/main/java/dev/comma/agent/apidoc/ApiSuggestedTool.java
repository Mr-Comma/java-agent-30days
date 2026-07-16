package dev.comma.agent.apidoc;

import java.util.Arrays;
import java.util.List;

public enum ApiSuggestedTool {
    API_RISK_REVIEWER("api-risk-reviewer"),
    OPENAPI_INPUT_VALIDATOR("openapi-input-validator");

    private final String value;

    ApiSuggestedTool(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static List<String> allowedValues() {
        return Arrays.stream(values())
                .map(ApiSuggestedTool::value)
                .toList();
    }

    public static String allowedValuesText() {
        return String.join(" 或 ", allowedValues());
    }
}
