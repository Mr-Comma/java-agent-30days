package dev.comma.agent.chat;

import java.time.OffsetDateTime;

public record ChatResponse(String message, OffsetDateTime generatedAt, String sessionId, int turnCount, String previousPrompt) {

    public ChatResponse(String message, OffsetDateTime generatedAt) {
        this(message, generatedAt, "default", 1, null);
    }
}
