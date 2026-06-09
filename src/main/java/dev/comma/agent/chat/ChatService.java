package dev.comma.agent.chat;

import java.time.Clock;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final Clock clock;
    private final ToolRegistry toolRegistry;

    public ChatService(Clock clock, ToolRegistry toolRegistry) {
        this.clock = clock;
        this.toolRegistry = toolRegistry;
    }

    public ChatResponse reply(String prompt) {
        String normalizedPrompt = prompt == null || prompt.isBlank() ? "介绍一下 Java Agent" : prompt.trim();
        OffsetDateTime generatedAt = OffsetDateTime.now(clock);

        return toolRegistry.executeFirstSupported(normalizedPrompt, generatedAt)
                .map(toolResult -> new ChatResponse(toolResult, generatedAt))
                .orElseGet(() -> new ChatResponse("Day 3 mock agent received: " + normalizedPrompt, generatedAt));
    }
}
