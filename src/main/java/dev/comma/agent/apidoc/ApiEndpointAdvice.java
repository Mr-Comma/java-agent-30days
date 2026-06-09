package dev.comma.agent.apidoc;

import java.util.List;

public record ApiEndpointAdvice(String method, String path, String riskHint, String testSuggestion) {

    private static final List<String> WRITE_METHODS = List.of("POST", "PUT", "PATCH", "DELETE");

    public static ApiEndpointAdvice from(ApiEndpoint endpoint) {
        String riskHint = riskHint(endpoint);
        String testSuggestion = testSuggestion(endpoint);
        return new ApiEndpointAdvice(endpoint.method(), endpoint.path(), riskHint, testSuggestion);
    }

    private static String riskHint(ApiEndpoint endpoint) {
        if ("DELETE".equals(endpoint.method())) {
            return "删除接口需要重点确认权限、幂等性和误删保护。";
        }
        if (WRITE_METHODS.contains(endpoint.method())) {
            return "写操作接口需要重点确认参数校验、权限控制和失败回滚。";
        }
        if (endpoint.path().contains("{")) {
            return "路径参数接口需要重点确认参数边界和不存在资源的返回。";
        }
        return "读接口需要重点确认分页、过滤条件和空结果返回。";
    }

    private static String testSuggestion(ApiEndpoint endpoint) {
        if ("DELETE".equals(endpoint.method())) {
            return "补充成功删除、重复删除、无权限删除和资源不存在用例。";
        }
        if (WRITE_METHODS.contains(endpoint.method())) {
            return "补充成功提交、必填缺失、非法参数和无权限访问用例。";
        }
        if (endpoint.path().contains("{")) {
            return "补充合法 ID、非法 ID、资源不存在和无权限访问用例。";
        }
        return "补充正常列表、空列表、分页边界和筛选条件用例。";
    }
}
