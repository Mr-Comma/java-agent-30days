package dev.comma.agent.chat;

import java.time.OffsetDateTime;

public record ChatResponse(String message, OffsetDateTime generatedAt) {
}
