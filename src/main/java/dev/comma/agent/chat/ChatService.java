package dev.comma.agent.chat;

import java.time.Clock;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final Clock clock;
    private final ChatAgentProperties properties;
    private final ToolRegistry toolRegistry;

    public ChatService(Clock clock, ChatAgentProperties properties, ToolRegistry toolRegistry) {
        this.clock = clock;
        this.properties = properties;
        this.toolRegistry = toolRegistry;
    }

    public ChatResponse reply(String prompt) {
        String normalizedPrompt = prompt == null || prompt.isBlank() ? properties.defaultPrompt() : prompt.trim();
        OffsetDateTime generatedAt = OffsetDateTime.now(clock);

        return toolRegistry.executeFirstSupported(normalizedPrompt, generatedAt)
                .map(toolResult -> new ChatResponse(toolResult, generatedAt))
                .orElseGet(() -> new ChatResponse(properties.roleName() + " received: " + normalizedPrompt, generatedAt));
    }
}
