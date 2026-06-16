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
        List<ApiReviewStep> reviewPlan = reviewPlan(modules);
        String workflowStatus = workflowStatus(parseResponse.endpoints());
        ReviewPromptVariables reviewPromptVariables = reviewPromptVariables(workflowStatus, reviewPlan);
        return new ApiDocAnalysisResponse(
                parseResponse.endpointCount(),
                summary(parseResponse.endpoints(), modules, reviewPlan),
                topPriorityModule(reviewPlan),
                analysisRole(),
                analysisFacts(parseResponse.endpoints(), modules, reviewPlan),
                analysisFactItems(parseResponse.endpoints(), modules, reviewPlan),
                workflowStatus,
                workflowStage(workflowStatus),
                suggestedTool(workflowStatus),
                reviewPromptTemplate(reviewPromptVariables),
                reviewPromptVariables,
                reviewPromptPreview(reviewPromptVariables),
                debugHints(workflowStatus, reviewPlan),
                blockingReason(workflowStatus),
                recommendedNextAction(workflowStatus, reviewPlan),
                taskGoal(reviewPlan),
                taskConstraints(reviewPlan),
                expectedOutput(),
                executionChecklist(reviewPlan),
                analysisTrace(parseResponse.endpoints(), modules, reviewPlan, advices),
                analysisTask(reviewPlan),
                advices,
                modules,
                reviewPlan);
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
                        priority(entry.getValue()),
                        testFocus(entry.getValue())))
                .sorted((left, right) -> {
                    int priorityCompare = Integer.compare(left.priority(), right.priority());
                    if (priorityCompare != 0) {
                        return priorityCompare;
                    }
                    return left.module().compareTo(right.module());
                })
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

    private int priority(List<ApiEndpoint> endpoints) {
        return switch (riskLevel(endpoints)) {
            case "HIGH" -> 1;
            case "MEDIUM" -> 2;
            default -> 3;
        };
    }

    private String testFocus(List<ApiEndpoint> endpoints) {
        if (writeOperationCount(endpoints) > 0) {
            return "优先覆盖权限、参数校验和失败回滚。";
        }
        return "优先覆盖分页、筛选条件和空结果。";
    }

    private List<ApiReviewStep> reviewPlan(List<ApiModuleSummary> modules) {
        return modules.stream()
                .map(module -> new ApiReviewStep(
                        module.module(), module.priority(), reviewAction(module), reviewReason(module)))
                .toList();
    }

    private String topPriorityModule(List<ApiReviewStep> reviewPlan) {
        return reviewPlan.stream()
                .findFirst()
                .map(ApiReviewStep::module)
                .orElse(null);
    }

    private String reviewAction(ApiModuleSummary module) {
        return switch (module.riskLevel()) {
            case "HIGH" -> "先审查删除接口、权限控制和误删保护。";
            case "MEDIUM" -> "再审查写操作参数校验和失败回滚。";
            default -> "最后抽查读接口分页、筛选和空结果。";
        };
    }

    private String reviewReason(ApiModuleSummary module) {
        return switch (module.riskLevel()) {
            case "HIGH" -> "高风险模块排在最前，因为包含删除接口或多个带路径参数的写操作。";
            case "MEDIUM" -> "中风险模块排在其后，因为包含写操作或路径参数。";
            default -> "低风险模块最后抽查，因为当前主要是读接口且未发现路径参数。";
        };
    }

    private String summary(List<ApiEndpoint> endpoints, List<ApiModuleSummary> modules, List<ApiReviewStep> reviewPlan) {
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
                + firstReviewSuggestion(reviewPlan) + "建议优先检查写操作权限、参数校验和边界测试。";
    }

    private String firstReviewSuggestion(List<ApiReviewStep> reviewPlan) {
        return reviewPlan.stream()
                .findFirst()
                .map(step -> "第一步建议审查 " + step.module() + " 模块。")
                .orElse("");
    }

    private String analysisRole() {
        return "你是 Java Agent API 文档审查助手。";
    }

    private String analysisFacts(List<ApiEndpoint> endpoints, List<ApiModuleSummary> modules,
            List<ApiReviewStep> reviewPlan) {
        if (endpoints.isEmpty()) {
            return "请先提供包含 paths 的 OpenAPI/Swagger JSON。";
        }
        return summary(endpoints, modules, reviewPlan) + "首要模块：" + topPriorityModule(reviewPlan) + "。";
    }

    private List<ApiAnalysisFact> analysisFactItems(List<ApiEndpoint> endpoints, List<ApiModuleSummary> modules,
            List<ApiReviewStep> reviewPlan) {
        if (endpoints.isEmpty()) {
            return List.of(new ApiAnalysisFact("input", "未识别到 paths，请提供 OpenAPI/Swagger JSON。"));
        }
        return List.of(
                new ApiAnalysisFact("endpointCount", String.valueOf(endpoints.size())),
                new ApiAnalysisFact("moduleRiskDistribution", moduleRiskSummary(modules)),
                new ApiAnalysisFact("topPriorityModule", topPriorityModule(reviewPlan)),
                new ApiAnalysisFact("firstReviewAction", reviewPlan.get(0).action()));
    }

    private String workflowStatus(List<ApiEndpoint> endpoints) {
        if (endpoints.isEmpty()) {
            return "NEEDS_INPUT";
        }
        return "READY";
    }

    private String workflowStage(String workflowStatus) {
        if ("NEEDS_INPUT".equals(workflowStatus)) {
            return "INPUT_REQUIRED";
        }
        return "REVIEW_READY";
    }

    private String suggestedTool(String workflowStatus) {
        if ("NEEDS_INPUT".equals(workflowStatus)) {
            return "openapi-input-validator";
        }
        return "api-risk-reviewer";
    }

    private String reviewPromptTemplate(ReviewPromptVariables variables) {
        if (variables.blockingReason() != null) {
            return "工作流阶段：" + variables.workflowStage() + "；建议工具：" + variables.suggestedTool() + "；阻塞原因："
                    + trimTrailingSentenceEnd(variables.blockingReason()) + "；" + variables.expectedOutputInstruction();
        }
        if (variables.firstReviewModule() == null) {
            return "工作流阶段：" + variables.workflowStage() + "；建议工具：" + variables.suggestedTool()
                    + "；" + variables.expectedOutputInstruction();
        }
        return "工作流阶段：" + variables.workflowStage() + "；建议工具：" + variables.suggestedTool() + "；首个动作：审查 "
                + variables.firstReviewModule() + " 模块，" + trimTrailingSentenceEnd(variables.firstReviewAction()) + "；"
                + variables.expectedOutputInstruction();
    }

    private String reviewPromptPreview(ReviewPromptVariables variables) {
        if (variables.blockingReason() != null) {
            return "请调用 " + variables.suggestedTool() + " 处理 " + variables.workflowStage() + " 阶段："
                    + trimTrailingSentenceEnd(variables.blockingReason()) + "；" + variables.expectedOutputInstruction();
        }
        if (variables.firstReviewModule() == null) {
            return "请调用 " + variables.suggestedTool() + " 处理 " + variables.workflowStage() + " 阶段；"
                    + variables.expectedOutputInstruction();
        }
        return "请调用 " + variables.suggestedTool() + " 审查 " + variables.firstReviewModule() + " 模块："
                + trimTrailingSentenceEnd(variables.firstReviewAction()) + "；" + variables.expectedOutputInstruction();
    }

    private ReviewPromptVariables reviewPromptVariables(String workflowStatus, List<ApiReviewStep> reviewPlan) {
        if ("NEEDS_INPUT".equals(workflowStatus)) {
            return new ReviewPromptVariables(
                    workflowStage(workflowStatus),
                    suggestedTool(workflowStatus),
                    blockingReason(workflowStatus),
                    null,
                    null,
                    "请先补充有效输入，不要编造接口。");
        }
        return reviewPlan.stream()
                .findFirst()
                .map(step -> new ReviewPromptVariables(
                        workflowStage(workflowStatus),
                        suggestedTool(workflowStatus),
                        null,
                        step.module(),
                        step.action(),
                        "请输出风险说明、测试建议和下一步行动。"))
                .orElse(new ReviewPromptVariables(
                        workflowStage(workflowStatus),
                        suggestedTool(workflowStatus),
                        null,
                        null,
                        null,
                        "请先确认解析结果，再启动 API 风险审查。"));
    }

    private String trimTrailingSentenceEnd(String text) {
        if (text.endsWith("。")) {
            return text.substring(0, text.length() - 1);
        }
        return text;
    }

    private String recommendedNextAction(String workflowStatus, List<ApiReviewStep> reviewPlan) {
        if ("NEEDS_INPUT".equals(workflowStatus)) {
            return "请先补充包含 paths 的 OpenAPI/Swagger JSON，再启动 API 风险审查。";
        }
        return reviewPlan.stream()
                .findFirst()
                .map(step -> "下一步执行 P" + step.priority() + "：审查 " + step.module() + " 模块，" + step.action())
                .orElse("请先确认解析结果，再启动 API 风险审查。");
    }

    private List<String> debugHints(String workflowStatus, List<ApiReviewStep> reviewPlan) {
        if ("NEEDS_INPUT".equals(workflowStatus)) {
            return List.of(
                    "状态：NEEDS_INPUT，暂不进入风险审查。",
                    "工具：调用 openapi-input-validator 校验输入。",
                    "原因：OpenAPI/Swagger JSON 缺少 paths 或未解析到接口。");
        }
        return reviewPlan.stream()
                .findFirst()
                .map(step -> List.of(
                        "状态：READY，可以进入 API 风险审查。",
                        "工具：调用 api-risk-reviewer 执行首个审查动作。",
                        "首个动作：P" + step.priority() + " 审查 " + step.module() + " 模块，" + step.action()))
                .orElse(List.of(
                        "状态：READY，但未生成审查计划。",
                        "工具：调用 api-risk-reviewer 前先确认解析结果。"));
    }

    private String blockingReason(String workflowStatus) {
        if ("NEEDS_INPUT".equals(workflowStatus)) {
            return "OpenAPI/Swagger JSON 缺少 paths 或未解析到接口。";
        }
        return null;
    }

    private String taskGoal(List<ApiReviewStep> reviewPlan) {
        return reviewPlan.stream()
                .findFirst()
                .map(step -> "优先完成 " + step.module() + " 模块的 API 风险审查。")
                .orElse("先确认 OpenAPI/Swagger JSON 中是否包含 paths。");
    }

    private String taskConstraints(List<ApiReviewStep> reviewPlan) {
        if (reviewPlan.isEmpty()) {
            return "不要编造接口；缺少 paths 时只提示补充 API 文档输入。";
        }
        String planContext = reviewPlan.stream()
                .map(step -> step.module() + "(P" + step.priority() + ")：" + step.reason())
                .reduce((left, right) -> left + "；" + right)
                .orElse("暂无审查计划");
        return "必须按审查优先级执行；" + planContext;
    }

    private String expectedOutput() {
        return "输出风险说明、测试建议和下一步行动。";
    }

    private List<String> executionChecklist(List<ApiReviewStep> reviewPlan) {
        if (reviewPlan.isEmpty()) {
            return List.of("确认输入 JSON 是否包含 paths，再开始 API 风险审查。");
        }
        return reviewPlan.stream()
                .map(step -> "P" + step.priority() + " - " + step.module() + "：" + step.action() + step.reason())
                .toList();
    }

    private List<String> analysisTrace(List<ApiEndpoint> endpoints, List<ApiModuleSummary> modules,
            List<ApiReviewStep> reviewPlan, List<ApiEndpointAdvice> advices) {
        return List.of(
                "parse: 识别接口 " + endpoints.size() + " 个。",
                "aggregate: 聚合模块 " + modules.size() + " 个。",
                "prioritize: 生成审查步骤 " + reviewPlan.size() + " 个。",
                "advise: 生成风险提示和测试建议 " + advices.size() + " 条。");
    }

    private String analysisTask(List<ApiReviewStep> reviewPlan) {
        if (reviewPlan.isEmpty()) {
            return "请输出风险说明、测试建议和下一步行动。";
        }
        String planContext = reviewPlan.stream()
                .map(step -> step.module() + "(P" + step.priority() + ")：" + step.action() + step.reason())
                .reduce((left, right) -> left + "；" + right)
                .orElse("暂无审查计划");
        return "审查计划：" + planContext + "请基于以上上下文输出风险说明、测试建议和下一步行动。";
    }

    private String moduleRiskSummary(List<ApiModuleSummary> modules) {
        Map<String, Long> riskCounts = modules.stream()
                .collect(Collectors.groupingBy(ApiModuleSummary::riskLevel, TreeMap::new, Collectors.counting()));
        return "模块风险分布：高风险 " + riskCounts.getOrDefault("HIGH", 0L) + " 个，中风险 "
                + riskCounts.getOrDefault("MEDIUM", 0L) + " 个，低风险 " + riskCounts.getOrDefault("LOW", 0L) + " 个。";
    }
}
