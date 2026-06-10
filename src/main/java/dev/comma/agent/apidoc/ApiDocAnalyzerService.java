package dev.comma.agent.apidoc;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ApiDocAnalyzerService {

    private final ApiDocParserService parserService;

    public ApiDocAnalyzerService(ApiDocParserService parserService) {
        this.parserService = parserService;
    }

    public ApiDocAnalysisResponse analyze(String openApiJson) {
        ApiDocParseResponse parseResponse = parserService.parse(openApiJson);
        List<ApiEndpointAdvice> advices = parseResponse.endpoints().stream()
                .map(ApiEndpointAdvice::from)
                .toList();
        List<ApiModuleSummary> modules = modules(parseResponse.endpoints());
        return new ApiDocAnalysisResponse(
                parseResponse.endpointCount(), summary(parseResponse.endpoints(), modules), advices, modules);
    }

    private List<ApiModuleSummary> modules(List<ApiEndpoint> endpoints) {
        Map<String, List<ApiEndpoint>> endpointsByModule = endpoints.stream()
                .collect(Collectors.groupingBy(endpoint -> moduleName(endpoint.path()), TreeMap::new, Collectors.toList()));
        return endpointsByModule.entrySet().stream()
                .map(entry -> new ApiModuleSummary(
                        entry.getKey(),
                        entry.getValue().size(),
                        writeOperationCount(entry.getValue()),
                        riskLevel(entry.getValue()),
                        testFocus(entry.getValue())))
                .toList();
    }

    private String moduleName(String path) {
        String normalizedPath = path.startsWith("/") ? path.substring(1) : path;
        if (normalizedPath.isBlank()) {
            return "root";
        }
        int slashIndex = normalizedPath.indexOf('/');
        String firstSegment = slashIndex >= 0 ? normalizedPath.substring(0, slashIndex) : normalizedPath;
        return firstSegment.isBlank() ? "root" : firstSegment;
    }

    private int writeOperationCount(List<ApiEndpoint> endpoints) {
        return (int) endpoints.stream()
                .filter(endpoint -> List.of("POST", "PUT", "PATCH", "DELETE").contains(endpoint.method()))
                .count();
    }

    private String riskLevel(List<ApiEndpoint> endpoints) {
        boolean hasDeleteOperation = endpoints.stream().anyMatch(endpoint -> "DELETE".equals(endpoint.method()));
        boolean hasPathParameter = endpoints.stream().anyMatch(endpoint -> endpoint.path().contains("{"));
        int writeOperationCount = writeOperationCount(endpoints);

        if (hasDeleteOperation || (writeOperationCount >= 2 && hasPathParameter)) {
            return "HIGH";
        }
        if (writeOperationCount > 0 || hasPathParameter) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private String testFocus(List<ApiEndpoint> endpoints) {
        if (writeOperationCount(endpoints) > 0) {
            return "优先覆盖权限、参数校验和失败回滚。";
        }
        return "优先覆盖分页、筛选条件和空结果。";
    }

    private String summary(List<ApiEndpoint> endpoints, List<ApiModuleSummary> modules) {
        if (endpoints.isEmpty()) {
            return "未识别到接口，请确认 OpenAPI/Swagger JSON 中是否包含 paths。";
        }

        Map<String, Long> methodCounts = new TreeMap<>();
        for (ApiEndpoint endpoint : endpoints) {
            methodCounts.merge(endpoint.method(), 1L, Long::sum);
        }

        String methodSummary = methodCounts.entrySet().stream()
                .map(entry -> entry.getKey() + " " + entry.getValue() + " 个")
                .reduce((left, right) -> left + "，" + right)
                .orElse("无接口");
        return "已识别 " + endpoints.size() + " 个接口（" + methodSummary + "），" + moduleRiskSummary(modules)
                + "建议优先检查写操作权限、参数校验和边界测试。";
    }

    private String moduleRiskSummary(List<ApiModuleSummary> modules) {
        Map<String, Long> riskCounts = modules.stream()
                .collect(Collectors.groupingBy(ApiModuleSummary::riskLevel, TreeMap::new, Collectors.counting()));
        return "模块风险分布：高风险 " + riskCounts.getOrDefault("HIGH", 0L) + " 个，中风险 "
                + riskCounts.getOrDefault("MEDIUM", 0L) + " 个，低风险 " + riskCounts.getOrDefault("LOW", 0L) + " 个。";
    }
}
