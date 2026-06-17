package dev.comma.agent.apidoc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ApiDocDebugSchemaServiceTest {

    private final ApiDocDebugSchemaService service = new ApiDocDebugSchemaService();

    @Test
    void returnsStableDebugFieldSchemaForWorkflowRouting() {
        ApiDocDebugSchemaResponse response = service.schema();

        assertThat(response.endpoint()).isEqualTo("/api-docs/analyze");
        assertThat(response.fields())
                .extracting(ApiDocDebugField::name)
                .containsExactly(
                        "workflowStatus",
                        "workflowStage",
                        "suggestedTool",
                        "blockingReason",
                        "debugHints",
                        "reviewPromptVariables",
                        "reviewPromptPreview");
    }

    @Test
    void documentsReadyAndNeedsInputMeaningsForCriticalFields() {
        List<ApiDocDebugField> fields = service.schema().fields();

        assertThat(fields.get(0))
                .isEqualTo(new ApiDocDebugField(
                        "workflowStatus",
                        "已解析到接口，可进入风险审查",
                        "缺少 paths 或未解析到接口",
                        "作为主路由状态，决定进入审查还是补输入"));
        assertThat(fields.get(3))
                .isEqualTo(new ApiDocDebugField(
                        "blockingReason",
                        "null，没有阻塞原因",
                        "返回缺输入原因",
                        "展示阻塞提示，避免编造接口"));
        assertThat(fields.get(6))
                .isEqualTo(new ApiDocDebugField(
                        "reviewPromptPreview",
                        "生成可执行的审查请求",
                        "生成补输入请求",
                        "作为调试预览，不替代结构化变量"));
    }
}
