package dev.comma.agent.apidoc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ApiDocControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ApiDocParserService parserService = new ApiDocParserService(objectMapper);
    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
                    new ApiDocController(
                            parserService, new ApiDocAnalyzerService(parserService), new ApiDocDebugSchemaService()))
            .build();

    @Test
    void exposesApiDocDebugSchemaForFrontendAndAgentRouting() throws Exception {
        mockMvc.perform(get("/api-docs/debug-schema"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endpoint").value("/api-docs/analyze"))
                .andExpect(jsonPath("$.schemaVersion").value("v1"))
                .andExpect(jsonPath("$.contractOwner").value("api-docs-agent"))
                .andExpect(jsonPath("$.fields[0].name").value("workflowStatus"))
                .andExpect(jsonPath("$.fields[0].jsonType").value("string"))
                .andExpect(jsonPath("$.fields[0].required").value(true))
                .andExpect(jsonPath("$.fields[0].readyMeaning").value("已解析到接口，可进入风险审查"))
                .andExpect(jsonPath("$.fields[0].needsInputMeaning").value("缺少 paths 或未解析到接口"))
                .andExpect(jsonPath("$.fields[0].usage").value("作为主路由状态，决定进入审查还是补输入"))
                .andExpect(jsonPath("$.fields[0].displayOrder").value(10))
                .andExpect(jsonPath("$.fields[0].category").value("routing"))
                .andExpect(jsonPath("$.fields[0].uiLabel").value("工作流状态"))
                .andExpect(jsonPath("$.fields[0].uiDescription").value("判断 OpenAPI 输入是否已具备进入风险审查的条件。"))
                .andExpect(jsonPath("$.fields[0].visibility").value("summary"))
                .andExpect(jsonPath("$.fields[0].renderType").value("badge"))
                .andExpect(jsonPath("$.fields[0].copyable").value(true))
                .andExpect(jsonPath("$.fields[0].interactionHint").value("复制状态用于路由判断"))
                .andExpect(jsonPath("$.fields[0].agentAction").value("route-by-status"))
                .andExpect(jsonPath("$.fields[0].targetNode").value("status-router-node"))
                .andExpect(jsonPath("$.fields[0].nodeInputPath").value("$.workflowStatus"))
                .andExpect(jsonPath("$.fields[0].handoffPayloadKey").value("workflowStatus"))
                .andExpect(jsonPath("$.fields[0].requiredForNode").value(true))
                .andExpect(jsonPath("$.fields[0].fallbackValue").value("NEEDS_INPUT"))
                .andExpect(jsonPath("$.fields[0].validationRule").value("值必须为 READY 或 NEEDS_INPUT；缺失/非法时使用 fallbackValue。"))
                .andExpect(jsonPath("$.fields[0].missingFieldPolicy").value("缺失或非法时应用 fallbackValue 并路由到输入补全。"))
                .andExpect(jsonPath("$.fields[0].policySeverity").value("needs-input"))
                .andExpect(jsonPath("$.fields[0].retryable").value(false))
                .andExpect(jsonPath("$.fields[0].operatorMessage").value("请补充有效 OpenAPI/Swagger JSON 后重新分析。"))
                .andExpect(jsonPath("$.fields[0].failureEscalation").value("openapi-input-owner"))
                .andExpect(jsonPath("$.fields[0].escalationCondition").value("当 workflowStatus 缺失、非法或持续为 NEEDS_INPUT 且无法自动补全时升级。"))
                .andExpect(jsonPath("$.fields[0].failureEscalationMessage").value("已将输入问题升级给 openapi-input-owner：请补充有效 OpenAPI/Swagger JSON 后重新分析。"))
                .andExpect(jsonPath("$.fields[0].escalationPriority").value(10))
                .andExpect(jsonPath("$.fields[0].escalationCategory").value("input"))
                .andExpect(jsonPath("$.fields[0].source").value("ApiDocAnalyzerService.workflowStatus"))
                .andExpect(jsonPath("$.fields[0].readyExampleValue").value("READY"))
                .andExpect(jsonPath("$.fields[0].needsInputExampleValue").value("NEEDS_INPUT"))
                .andExpect(jsonPath("$.fields[3].name").value("blockingReason"))
                .andExpect(jsonPath("$.fields[3].jsonType").value("string|null"))
                .andExpect(jsonPath("$.fields[3].required").value(false))
                .andExpect(jsonPath("$.fields[3].readyMeaning").value("null，没有阻塞原因"))
                .andExpect(jsonPath("$.fields[3].displayOrder").value(40))
                .andExpect(jsonPath("$.fields[3].category").value("routing"))
                .andExpect(jsonPath("$.fields[3].uiLabel").value("阻塞原因"))
                .andExpect(jsonPath("$.fields[3].uiDescription").value("说明当前阻塞原因；READY 时为空。"))
                .andExpect(jsonPath("$.fields[3].visibility").value("summary"))
                .andExpect(jsonPath("$.fields[3].renderType").value("text"))
                .andExpect(jsonPath("$.fields[3].copyable").value(true))
                .andExpect(jsonPath("$.fields[3].interactionHint").value("复制阻塞原因用于补输入说明"))
                .andExpect(jsonPath("$.fields[3].agentAction").value("collect-missing-input"))
                .andExpect(jsonPath("$.fields[3].targetNode").value("input-collector-node"))
                .andExpect(jsonPath("$.fields[3].nodeInputPath").value("$.blockingReason"))
                .andExpect(jsonPath("$.fields[3].handoffPayloadKey").value("blockingReason"))
                .andExpect(jsonPath("$.fields[3].requiredForNode").value(false))
                .andExpect(jsonPath("$.fields[3].fallbackValue").doesNotExist())
                .andExpect(jsonPath("$.fields[3].validationRule").value("READY 时允许为 null；NEEDS_INPUT 时应为非空缺输入说明。"))
                .andExpect(jsonPath("$.fields[3].missingFieldPolicy").value("READY 可缺省为 null；NEEDS_INPUT 缺失时提示重新运行分析。"))
                .andExpect(jsonPath("$.fields[3].policySeverity").value("rerun-analysis"))
                .andExpect(jsonPath("$.fields[3].retryable").value(false))
                .andExpect(jsonPath("$.fields[3].operatorMessage").value("请将阻塞原因展示给用户，并要求重新提交有效接口文档。"))
                .andExpect(jsonPath("$.fields[3].failureEscalation").value("api-docs-operator"))
                .andExpect(jsonPath("$.fields[3].escalationCondition").value("当 NEEDS_INPUT 路径缺少阻塞原因或原因无法指导补输入时升级。"))
                .andExpect(jsonPath("$.fields[3].failureEscalationMessage").value("已将阻塞原因问题升级给 api-docs-operator：请展示原因并要求重新提交接口文档。"))
                .andExpect(jsonPath("$.fields[3].escalationPriority").value(30))
                .andExpect(jsonPath("$.fields[3].escalationCategory").value("operator"))
                .andExpect(jsonPath("$.fields[3].source").value("ApiDocAnalyzerService.blockingReason"))
                .andExpect(jsonPath("$.fields[3].readyExampleValue").doesNotExist())
                .andExpect(jsonPath("$.fields[3].needsInputExampleValue").value("OpenAPI/Swagger JSON 缺少 paths 或未解析到接口。"))
                .andExpect(jsonPath("$.fields[5].readyExampleValue.workflowStage").value("REVIEW_READY"))
                .andExpect(jsonPath("$.fields[5].visibility").value("detail"))
                .andExpect(jsonPath("$.fields[5].renderType").value("json"))
                .andExpect(jsonPath("$.fields[5].interactionHint").value("展开查看结构化 Prompt 变量"))
                .andExpect(jsonPath("$.fields[5].agentAction").value("bind-prompt-variables"))
                .andExpect(jsonPath("$.fields[5].targetNode").value("prompt-template-node"))
                .andExpect(jsonPath("$.fields[5].nodeInputPath").value("$.reviewPromptVariables"))
                .andExpect(jsonPath("$.fields[5].handoffPayloadKey").value("reviewPromptVariables"))
                .andExpect(jsonPath("$.fields[5].requiredForNode").value(true))
                .andExpect(jsonPath("$.fields[5].fallbackValue.workflowStage").value("INPUT_REQUIRED"))
                .andExpect(jsonPath("$.fields[5].fallbackValue.suggestedTool").value("openapi-input-validator"))
                .andExpect(jsonPath("$.fields[5].validationRule").value("应包含 workflowStage、suggestedTool、blockingReason、firstReviewModule、firstReviewAction；缺失时使用 fallbackValue。"))
                .andExpect(jsonPath("$.fields[5].missingFieldPolicy").value("缺失固定键时应用 fallbackValue，避免 PromptTemplate 变量漂移。"))
                .andExpect(jsonPath("$.fields[5].policySeverity").value("fallback"))
                .andExpect(jsonPath("$.fields[5].retryable").value(true))
                .andExpect(jsonPath("$.fields[5].operatorMessage").value("已使用 INPUT_REQUIRED 变量对象，可继续渲染补输入 Prompt。"))
                .andExpect(jsonPath("$.fields[5].failureEscalation").value("none"))
                .andExpect(jsonPath("$.fields[5].fallbackValue.firstReviewModule").doesNotExist())
                .andExpect(jsonPath("$.fields[5].readyExampleValue.firstReviewModule").value("orders"))
                .andExpect(jsonPath("$.fields[5].needsInputExampleValue.workflowStage").value("INPUT_REQUIRED"))
                .andExpect(jsonPath("$.fields[5].needsInputExampleValue.firstReviewModule").doesNotExist())
                .andExpect(jsonPath("$.fields[6].name").value("reviewPromptPreview"))
                .andExpect(jsonPath("$.fields[6].usage").value("作为调试预览，不替代结构化变量"))
                .andExpect(jsonPath("$.fields[6].displayOrder").value(70))
                .andExpect(jsonPath("$.fields[6].category").value("prompt"))
                .andExpect(jsonPath("$.fields[6].uiLabel").value("审查 Prompt 预览"))
                .andExpect(jsonPath("$.fields[6].uiDescription").value("根据结构化变量渲染出的可执行审查请求预览。"))
                .andExpect(jsonPath("$.fields[6].visibility").value("summary"))
                .andExpect(jsonPath("$.fields[6].renderType").value("prompt-preview"))
                .andExpect(jsonPath("$.fields[6].copyable").value(true))
                .andExpect(jsonPath("$.fields[6].interactionHint").value("复制 Prompt 预览给 Agent 节点"))
                .andExpect(jsonPath("$.fields[6].agentAction").value("copy-prompt-preview"))
                .andExpect(jsonPath("$.fields[6].targetNode").value("prompt-preview-node"))
                .andExpect(jsonPath("$.fields[6].nodeInputPath").value("$.reviewPromptPreview"))
                .andExpect(jsonPath("$.fields[6].handoffPayloadKey").value("reviewPromptPreview"))
                .andExpect(jsonPath("$.fields[6].requiredForNode").value(true))
                .andExpect(jsonPath("$.fields[6].fallbackValue").value("请先补充有效 OpenAPI/Swagger JSON，不要编造接口。"))
                .andExpect(jsonPath("$.fields[6].validationRule").value("应为非空审查或补输入请求；缺失/空白时使用 fallbackValue。"))
                .andExpect(jsonPath("$.fields[6].missingFieldPolicy").value("缺失或空白时应用 fallbackValue，要求先补充有效输入。"))
                .andExpect(jsonPath("$.fields[6].policySeverity").value("needs-input"))
                .andExpect(jsonPath("$.fields[6].retryable").value(false))
                .andExpect(jsonPath("$.fields[6].operatorMessage").value("请先收集有效 OpenAPI/Swagger JSON，再生成审查 Prompt。"))
                .andExpect(jsonPath("$.fields[6].failureEscalation").value("prompt-input-owner"))
                .andExpect(jsonPath("$.fields[6].escalationCondition").value("当 Prompt 预览缺失、空白或无法生成非编造审查请求时升级。"))
                .andExpect(jsonPath("$.fields[6].failureEscalationMessage").value("已将 Prompt 预览问题升级给 prompt-input-owner：请先收集有效接口文档。"))
                .andExpect(jsonPath("$.fields[6].escalationPriority").value(40))
                .andExpect(jsonPath("$.fields[6].escalationCategory").value("prompt"))
                .andExpect(jsonPath("$.fields[6].source").value("ApiDocAnalyzerService.reviewPromptPreview"))
                .andExpect(jsonPath("$.fields[6].readyExampleValue")
                        .value("请调用 api-risk-reviewer 审查 orders 模块：先审查删除接口、权限控制和误删保护；请输出风险说明、测试建议和下一步行动。"))
                .andExpect(jsonPath("$.fields[6].needsInputExampleValue")
                        .value("请调用 openapi-input-validator 处理 INPUT_REQUIRED 阶段：OpenAPI/Swagger JSON 缺少 paths 或未解析到接口；请先补充有效输入，不要编造接口。"));
    }

    @Test
    void exposesInputBlockingDebugFieldsFromAnalyzeEndpoint() throws Exception {
        String openApiJson = """
                {
                  "openapi": "3.0.1",
                  "paths": {}
                }
                """;
        String requestBody = objectMapper.writeValueAsString(new ApiDocParseRequest(openApiJson));

        mockMvc.perform(post("/api-docs/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workflowStatus").value("NEEDS_INPUT"))
                .andExpect(jsonPath("$.blockingReason").value("OpenAPI/Swagger JSON 缺少 paths 或未解析到接口。"))
                .andExpect(jsonPath("$.suggestedTool").value("openapi-input-validator"))
                .andExpect(jsonPath("$.reviewPromptVariables.workflowStage").value("INPUT_REQUIRED"))
                .andExpect(jsonPath("$.reviewPromptVariables.suggestedTool").value("openapi-input-validator"))
                .andExpect(jsonPath("$.reviewPromptVariables.blockingReason").value("OpenAPI/Swagger JSON 缺少 paths 或未解析到接口。"))
                .andExpect(jsonPath("$.reviewPromptVariables.firstReviewModule").doesNotExist())
                .andExpect(jsonPath("$.reviewPromptVariables.firstReviewAction").doesNotExist())
                .andExpect(jsonPath("$.reviewPromptPreview")
                        .value("请调用 openapi-input-validator 处理 INPUT_REQUIRED 阶段：OpenAPI/Swagger JSON 缺少 paths 或未解析到接口；请先补充有效输入，不要编造接口。"))
                .andExpect(jsonPath("$.debugHints[0]").value("状态：NEEDS_INPUT，暂不进入风险审查。"))
                .andExpect(jsonPath("$.debugHints[1]").value("工具：调用 openapi-input-validator 校验输入。"))
                .andExpect(jsonPath("$.debugHints[2]").value("原因：OpenAPI/Swagger JSON 缺少 paths 或未解析到接口。"));
    }

    @Test
    void exposesReviewPromptDebugFieldsFromAnalyzeEndpoint() throws Exception {
        String openApiJson = """
                {
                  "openapi": "3.0.1",
                  "paths": {
                    "/orders/{id}": {
                      "delete": {"summary": "Delete order"}
                    }
                  }
                }
                """;
        String requestBody = objectMapper.writeValueAsString(new ApiDocParseRequest(openApiJson));

        mockMvc.perform(post("/api-docs/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workflowStatus").value("READY"))
                .andExpect(jsonPath("$.reviewPromptVariables.suggestedTool").value("api-risk-reviewer"))
                .andExpect(jsonPath("$.reviewPromptVariables.firstReviewModule").value("orders"))
                .andExpect(jsonPath("$.reviewPromptPreview")
                        .value("请调用 api-risk-reviewer 审查 orders 模块：先审查删除接口、权限控制和误删保护；请输出风险说明、测试建议和下一步行动。"))
                .andExpect(jsonPath("$.debugHints[0]").value("状态：READY，可以进入 API 风险审查。"))
                .andExpect(jsonPath("$.debugHints[1]").value("工具：调用 api-risk-reviewer 执行首个审查动作。"))
                .andExpect(jsonPath("$.debugHints[2]").value("首个动作：P1 审查 orders 模块，先审查删除接口、权限控制和误删保护。"));
    }
}
