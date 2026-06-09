package dev.comma.agent.chat;

import java.time.Clock;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final Clock clock;
    private final ChatAgentProperties properties;
    private final ToolRegistry toolRegistry;
    private final ConversationMemory conversationMemory;

    public ChatService(Clock clock, ChatAgentProperties properties, ToolRegistry toolRegistry, ConversationMemory conversationMemory) {
        this.clock = clock;
        this.properties = properties;
        this.toolRegistry = toolRegistry;
        this.conversationMemory = conversationMemory;
    }

    public ChatResponse reply(String prompt) {
        return reply(prompt, "default");
    }

    public ChatResponse reply(String prompt, String sessionId) {
        String normalizedPrompt = prompt == null || prompt.isBlank() ? properties.defaultPrompt() : prompt.trim();
        OffsetDateTime generatedAt = OffsetDateTime.now(clock);
        ConversationTurn turn = conversationMemory.remember(sessionId, normalizedPrompt);

        return toolRegistry.executeFirstSupported(normalizedPrompt, generatedAt)
                .map(toolResult -> new ChatResponse(toolResult, generatedAt, turn.sessionId(), turn.turnCount(), turn.previousPrompt()))
                .orElseGet(() -> new ChatResponse(
                        properties.roleName() + " received: " + normalizedPrompt,
                        generatedAt,
                        turn.sessionId(),
                        turn.turnCount(),
                        turn.previousPrompt()));
    }
}
