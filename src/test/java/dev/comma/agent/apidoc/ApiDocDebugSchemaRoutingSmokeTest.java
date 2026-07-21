package dev.comma.agent.apidoc;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ApiDocDebugSchemaRoutingSmokeTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ApiDocDebugSchemaService service = new ApiDocDebugSchemaService();

    @Test
    void serializesTopLevelRoutingAllowedValuesFromRoutingEnums() throws Exception {
        JsonNode schema = objectMapper.readTree(objectMapper.writeValueAsString(service.schema()));

        assertThat(textValues(schema, "workflowStatusAllowedValues"))
                .containsExactlyElementsOf(ApiWorkflowStatus.allowedValues());
        assertThat(textValues(schema, "workflowStageAllowedValues"))
                .containsExactlyElementsOf(ApiWorkflowStage.allowedValues());
        assertThat(textValues(schema, "suggestedToolAllowedValues"))
                .containsExactlyElementsOf(ApiSuggestedTool.allowedValues());
    }

    private List<String> textValues(JsonNode schema, String fieldName) {
        List<String> values = new ArrayList<>();
        for (JsonNode node : schema.path(fieldName)) {
            values.add(node.asText());
        }
        return values;
    }
}
