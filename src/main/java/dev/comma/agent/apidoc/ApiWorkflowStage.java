package dev.comma.agent.apidoc;

import java.util.Arrays;
import java.util.List;

public enum ApiWorkflowStage {
    REVIEW_READY("REVIEW_READY"),
    INPUT_REQUIRED("INPUT_REQUIRED");

    private final String value;

    ApiWorkflowStage(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static List<String> allowedValues() {
        return Arrays.stream(values())
                .map(ApiWorkflowStage::value)
                .toList();
    }

    public static String allowedValuesText() {
        return String.join(" 或 ", allowedValues());
    }
}
