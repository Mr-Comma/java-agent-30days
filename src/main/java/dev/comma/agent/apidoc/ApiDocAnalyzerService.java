package dev.comma.agent.apidoc;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
        return new ApiDocAnalysisResponse(parseResponse.endpointCount(), summary(parseResponse.endpoints()), advices);
    }

    private String summary(List<ApiEndpoint> endpoints) {
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
        return "已识别 " + endpoints.size() + " 个接口（" + methodSummary + "），建议优先检查写操作权限、参数校验和边界测试。";
    }
}
