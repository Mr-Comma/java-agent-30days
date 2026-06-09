package dev.comma.agent.chat;

public record ConversationTurn(String sessionId, int turnCount, String previousPrompt) {
}