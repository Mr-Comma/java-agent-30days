package dev.comma.agent.apidoc;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ReadmeApiDocExampleTest {

    @Test
    void documentsDebugFieldTable() throws IOException {
        String readme = Files.readString(Path.of("README.md"), StandardCharsets.UTF_8);

        assertThat(readme).contains("| 字段 | READY 时含义 | NEEDS_INPUT 时含义 | 前端/Agent 用法 |");
        assertThat(readme).contains("| `workflowStatus` | 已解析到接口，可进入风险审查 | 缺少 `paths` 或未解析到接口 | 作为主路由状态，决定进入审查还是补输入 |");
        assertThat(readme).contains("| `suggestedTool` | 推荐调用 `api-risk-reviewer` | 推荐调用 `openapi-input-validator` | 映射到下一步工具或 Agent 节点 |");
        assertThat(readme).contains("| `blockingReason` | `null`，没有阻塞原因 | 返回缺输入原因 | 展示阻塞提示，避免编造接口 |");
        assertThat(readme).contains("| `reviewPromptPreview` | 生成可执行的审查请求 | 生成补输入请求 | 作为调试预览，不替代结构化变量 |");
    }

    @Test
    void documentsReadyDebugResponseFields() throws IOException {
        String readme = Files.readString(Path.of("README.md"), StandardCharsets.UTF_8);

        assertThat(readme).contains("解析到接口时，可以用下面这个请求验证可审查链路");
        assertThat(readme).contains("\"workflowStatus\": \"READY\"");
        assertThat(readme).contains("\"workflowStage\": \"REVIEW_READY\"");
        assertThat(readme).contains("\"suggestedTool\": \"api-risk-reviewer\"");
        assertThat(readme).contains("\"状态：READY，可以进入 API 风险审查。\"");
        assertThat(readme).contains("\"首个动作：P1 审查 orders 模块，先审查删除接口、权限控制和误删保护。\"");
        assertThat(readme).contains("\"blockingReason\": null");
        assertThat(readme).contains("\"firstReviewModule\": \"orders\"");
        assertThat(readme).contains("请调用 api-risk-reviewer 审查 orders 模块：先审查删除接口、权限控制和误删保护；请输出风险说明、测试建议和下一步行动。");
    }

    @Test
    void documentsMissingInputDebugResponseFields() throws IOException {
        String readme = Files.readString(Path.of("README.md"), StandardCharsets.UTF_8);

        assertThat(readme).contains("缺少 `paths` 或未解析到接口时");
        assertThat(readme).contains("\"workflowStatus\": \"NEEDS_INPUT\"");
        assertThat(readme).contains("\"workflowStage\": \"INPUT_REQUIRED\"");
        assertThat(readme).contains("\"suggestedTool\": \"openapi-input-validator\"");
        assertThat(readme).contains("\"debugHints\": [");
        assertThat(readme).contains("\"状态：NEEDS_INPUT，暂不进入风险审查。\"");
        assertThat(readme).contains("\"firstReviewModule\": null");
        assertThat(readme).contains("请调用 openapi-input-validator 处理 INPUT_REQUIRED 阶段：OpenAPI/Swagger JSON 缺少 paths 或未解析到接口；请先补充有效输入，不要编造接口。");
    }
}
