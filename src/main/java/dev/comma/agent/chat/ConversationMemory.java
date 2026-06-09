package dev.comma.agent.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class ConversationMemory {

    private static final String DEFAULT_SESSION_ID = "default";

    private final Map<String, List<String>> promptsBySession = new ConcurrentHashMap<>();

    public ConversationTurn remember(String sessionId, String prompt) {
        String normalizedSessionId = normalizeSessionId(sessionId);
        List<String> prompts = promptsBySession.computeIfAbsent(normalizedSessionId, ignored -> new ArrayList<>());

        synchronized (prompts) {
            String previousPrompt = prompts.isEmpty() ? null : prompts.get(prompts.size() - 1);
            prompts.add(prompt);
            return new ConversationTurn(normalizedSessionId, prompts.size(), previousPrompt);
        }
    }

    private String normalizeSessionId(String sessionId) {
        return sessionId == null || sessionId.isBlank() ? DEFAULT_SESSION_ID : sessionId.trim();
    }
}