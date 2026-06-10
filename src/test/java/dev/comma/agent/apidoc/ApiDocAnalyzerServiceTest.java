package dev.comma.agent.apidoc;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class ApiDocAnalyzerServiceTest {

    private final ApiDocAnalyzerService analyzerService = new ApiDocAnalyzerService(
            new ApiDocParserService(new ObjectMapper()));

    @Test
    void generatesRiskHintsAndTestSuggestionsForParsedEndpoints() {
        String openApiJson = """
                {
                  "openapi": "3.0.1",
                  "paths": {
                    "/users": {
                      "get": {"summary": "List users"},
                      "post": {"summary": "Create user"}
                    },
                    "/audit/events": {
                      "get": {"summary": "List audit events"}
                    },
                    "/orders/{id}": {
                      "delete": {"summary": "Delete order"}
                    }
                  }
                }
                """;

        ApiDocAnalysisResponse response = analyzerService.analyze(openApiJson);

        assertThat(response.endpointCount()).isEqualTo(4);
        assertThat(response.summary())
                .isEqualTo("已识别 4 个接口（DELETE 1 个，GET 2 个，POST 1 个），模块风险分布：高风险 1 个，中风险 1 个，低风险 1 个。建议优先检查写操作权限、参数校验和边界测试。");
        assertThat(response.modules())
                .containsExactly(
                        new ApiModuleSummary("orders", 1, 1, "HIGH", 1, "优先覆盖权限、参数校验和失败回滚。"),
                        new ApiModuleSummary("users", 2, 1, "MEDIUM", 2, "优先覆盖权限、参数校验和失败回滚。"),
                        new ApiModuleSummary("audit", 1, 0, "LOW", 3, "优先覆盖分页、筛选条件和空结果。"));
        assertThat(response.advices())
                .containsExactly(
                        new ApiEndpointAdvice(
                                "GET",
                                "/users",
                                "读接口需要重点确认分页、过滤条件和空结果返回。",
                                "补充正常列表、空列表、分页边界和筛选条件用例。"),
                        new ApiEndpointAdvice(
                                "POST",
                                "/users",
                                "写操作接口需要重点确认参数校验、权限控制和失败回滚。",
                                "补充成功提交、必填缺失、非法参数和无权限访问用例。"),
                        new ApiEndpointAdvice(
                                "GET",
                                "/audit/events",
                                "读接口需要重点确认分页、过滤条件和空结果返回。",
                                "补充正常列表、空列表、分页边界和筛选条件用例。"),
                        new ApiEndpointAdvice(
                                "DELETE",
                                "/orders/{id}",
                                "删除接口需要重点确认权限、幂等性和误删保护。",
                                "补充成功删除、重复删除、无权限删除和资源不存在用例。"));
    }

    @Test
    void returnsEmptySummaryWhenNoEndpointsParsed() {
        ApiDocAnalysisResponse response = analyzerService.analyze("{\"openapi\":\"3.0.1\"}");

        assertThat(response.endpointCount()).isZero();
        assertThat(response.summary()).isEqualTo("未识别到接口，请确认 OpenAPI/Swagger JSON 中是否包含 paths。");
        assertThat(response.advices()).isEmpty();
        assertThat(response.modules()).isEmpty();
    }
}
