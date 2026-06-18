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
                .andExpect(jsonPath("$.fields[0].name").value("workflowStatus"))
                .andExpect(jsonPath("$.fields[0].readyMeaning").value("已解析到接口，可进入风险审查"))
                .andExpect(jsonPath("$.fields[0].needsInputMeaning").value("缺少 paths 或未解析到接口"))
                .andExpect(jsonPath("$.fields[0].usage").value("作为主路由状态，决定进入审查还是补输入"))
                .andExpect(jsonPath("$.fields[3].name").value("blockingReason"))
                .andExpect(jsonPath("$.fields[3].readyMeaning").value("null，没有阻塞原因"))
                .andExpect(jsonPath("$.fields[6].name").value("reviewPromptPreview"))
                .andExpect(jsonPath("$.fields[6].usage").value("作为调试预览，不替代结构化变量"));
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
