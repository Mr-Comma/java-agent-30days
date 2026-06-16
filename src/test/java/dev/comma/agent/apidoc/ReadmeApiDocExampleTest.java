package dev.comma.agent.apidoc;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ReadmeApiDocExampleTest {

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
