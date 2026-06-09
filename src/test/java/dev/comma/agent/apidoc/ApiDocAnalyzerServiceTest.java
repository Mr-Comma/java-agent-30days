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
                    "/orders/{id}": {
                      "delete": {"summary": "Delete order"}
                    }
                  }
                }
                """;

        ApiDocAnalysisResponse response = analyzerService.analyze(openApiJson);

        assertThat(response.endpointCount()).isEqualTo(3);
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
                                "DELETE",
                                "/orders/{id}",
                                "删除接口需要重点确认权限、幂等性和误删保护。",
                                "补充成功删除、重复删除、无权限删除和资源不存在用例。"));
    }
}
