package dev.comma.agent.chat;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
class ToolRegistry {

    private final List<AgentTool> tools;

    ToolRegistry(List<AgentTool> tools) {
        this.tools = tools;
    }

    Optional<String> executeFirstSupported(String prompt, OffsetDateTime generatedAt) {
        return tools.stream()
                .filter(tool -> tool.supports(prompt))
                .findFirst()
                .map(tool -> tool.execute(prompt, generatedAt));
    }
}