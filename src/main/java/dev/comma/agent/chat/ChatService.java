package dev.comma.agent.chat;

import java.time.OffsetDateTime;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    public ChatResponse reply(String prompt) {
        String normalizedPrompt = prompt == null || prompt.isBlank() ? "介绍一下 Java Agent" : prompt.trim();
        return new ChatResponse("Day 2 mock agent received: " + normalizedPrompt, OffsetDateTime.now());
    }
}
