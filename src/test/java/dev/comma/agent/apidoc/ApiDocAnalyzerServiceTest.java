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
                .isEqualTo("已识别 4 个接口（DELETE 1 个，GET 2 个，POST 1 个），模块风险分布：高风险 1 个，中风险 1 个，低风险 1 个。第一步建议审查 orders 模块。建议优先检查写操作权限、参数校验和边界测试。");
        assertThat(response.topPriorityModule()).isEqualTo("orders");
        assertThat(response.analysisRole()).isEqualTo("你是 Java Agent API 文档审查助手。");
        assertThat(response.analysisFacts())
                .isEqualTo("已识别 4 个接口（DELETE 1 个，GET 2 个，POST 1 个），模块风险分布：高风险 1 个，中风险 1 个，低风险 1 个。第一步建议审查 orders 模块。建议优先检查写操作权限、参数校验和边界测试。首要模块：orders。");
        assertThat(response.analysisFactItems())
                .containsExactly(
                        new ApiAnalysisFact("endpointCount", "4"),
                        new ApiAnalysisFact("moduleRiskDistribution", "模块风险分布：高风险 1 个，中风险 1 个，低风险 1 个。"),
                        new ApiAnalysisFact("topPriorityModule", "orders"),
                        new ApiAnalysisFact("firstReviewAction", "先审查删除接口、权限控制和误删保护。"));
        assertThat(response.workflowStatus()).isEqualTo("READY");
        assertThat(response.blockingReason()).isNull();
        assertThat(response.recommendedNextAction())
                .isEqualTo("下一步执行 P1：审查 orders 模块，先审查删除接口、权限控制和误删保护。");
        assertThat(response.taskGoal()).isEqualTo("优先完成 orders 模块的 API 风险审查。");
        assertThat(response.taskConstraints())
                .isEqualTo("必须按审查优先级执行；orders(P1)：高风险模块排在最前，因为包含删除接口或多个带路径参数的写操作。；users(P2)：中风险模块排在其后，因为包含写操作或路径参数。；audit(P3)：低风险模块最后抽查，因为当前主要是读接口且未发现路径参数。");
        assertThat(response.expectedOutput()).isEqualTo("输出风险说明、测试建议和下一步行动。");
        assertThat(response.executionChecklist())
                .containsExactly(
                        "P1 - orders：先审查删除接口、权限控制和误删保护。高风险模块排在最前，因为包含删除接口或多个带路径参数的写操作。",
                        "P2 - users：再审查写操作参数校验和失败回滚。中风险模块排在其后，因为包含写操作或路径参数。",
                        "P3 - audit：最后抽查读接口分页、筛选和空结果。低风险模块最后抽查，因为当前主要是读接口且未发现路径参数。");
        assertThat(response.analysisTrace())
                .containsExactly(
                        "parse: 识别接口 4 个。",
                        "aggregate: 聚合模块 3 个。",
                        "prioritize: 生成审查步骤 3 个。",
                        "advise: 生成风险提示和测试建议 4 条。");
        assertThat(response.analysisTask())
                .isEqualTo("审查计划：orders(P1)：先审查删除接口、权限控制和误删保护。高风险模块排在最前，因为包含删除接口或多个带路径参数的写操作。；users(P2)：再审查写操作参数校验和失败回滚。中风险模块排在其后，因为包含写操作或路径参数。；audit(P3)：最后抽查读接口分页、筛选和空结果。低风险模块最后抽查，因为当前主要是读接口且未发现路径参数。请基于以上上下文输出风险说明、测试建议和下一步行动。");
        assertThat(response.modules())
                .containsExactly(
                        new ApiModuleSummary("orders", 1, 1, "HIGH", 1, "优先覆盖权限、参数校验和失败回滚。"),
                        new ApiModuleSummary("users", 2, 1, "MEDIUM", 2, "优先覆盖权限、参数校验和失败回滚。"),
                        new ApiModuleSummary("audit", 1, 0, "LOW", 3, "优先覆盖分页、筛选条件和空结果。"));
        assertThat(response.reviewPlan())
                .containsExactly(
                        new ApiReviewStep(
                                "orders",
                                1,
                                "先审查删除接口、权限控制和误删保护。",
                                "高风险模块排在最前，因为包含删除接口或多个带路径参数的写操作。"),
                        new ApiReviewStep(
                                "users",
                                2,
                                "再审查写操作参数校验和失败回滚。",
                                "中风险模块排在其后，因为包含写操作或路径参数。"),
                        new ApiReviewStep(
                                "audit",
                                3,
                                "最后抽查读接口分页、筛选和空结果。",
                                "低风险模块最后抽查，因为当前主要是读接口且未发现路径参数。"));
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
        assertThat(response.reviewPlan()).isEmpty();
        assertThat(response.topPriorityModule()).isNull();
        assertThat(response.analysisRole()).isEqualTo("你是 Java Agent API 文档审查助手。");
        assertThat(response.analysisFacts()).isEqualTo("请先提供包含 paths 的 OpenAPI/Swagger JSON。");
        assertThat(response.analysisFactItems())
                .containsExactly(new ApiAnalysisFact("input", "未识别到 paths，请提供 OpenAPI/Swagger JSON。"));
        assertThat(response.workflowStatus()).isEqualTo("NEEDS_INPUT");
        assertThat(response.blockingReason()).isEqualTo("OpenAPI/Swagger JSON 缺少 paths 或未解析到接口。");
        assertThat(response.recommendedNextAction())
                .isEqualTo("请先补充包含 paths 的 OpenAPI/Swagger JSON，再启动 API 风险审查。");
        assertThat(response.taskGoal()).isEqualTo("先确认 OpenAPI/Swagger JSON 中是否包含 paths。");
        assertThat(response.taskConstraints()).isEqualTo("不要编造接口；缺少 paths 时只提示补充 API 文档输入。");
        assertThat(response.expectedOutput()).isEqualTo("输出风险说明、测试建议和下一步行动。");
        assertThat(response.executionChecklist())
                .containsExactly("确认输入 JSON 是否包含 paths，再开始 API 风险审查。");
        assertThat(response.analysisTrace())
                .containsExactly(
                        "parse: 识别接口 0 个。",
                        "aggregate: 聚合模块 0 个。",
                        "prioritize: 生成审查步骤 0 个。",
                        "advise: 生成风险提示和测试建议 0 条。");
        assertThat(response.analysisTask()).isEqualTo("请输出风险说明、测试建议和下一步行动。");
    }
}
