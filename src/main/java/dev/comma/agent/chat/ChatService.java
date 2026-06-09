package dev.comma.agent.chat;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final Clock clock;

    public ChatService() {
        this(Clock.systemDefaultZone());
    }

    ChatService(Clock clock) {
        this.clock = clock;
    }

    public ChatResponse reply(String prompt) {
        String normalizedPrompt = prompt == null || prompt.isBlank() ? "介绍一下 Java Agent" : prompt.trim();
        OffsetDateTime generatedAt = OffsetDateTime.now(clock);

        if (asksForTime(normalizedPrompt)) {
            String currentTime = generatedAt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            return new ChatResponse("Tool[time] current time: " + currentTime, generatedAt);
        }

        return new ChatResponse("Day 3 mock agent received: " + normalizedPrompt, generatedAt);
    }

    private boolean asksForTime(String prompt) {
        String lowerPrompt = prompt.toLowerCase(Locale.ROOT);
        return lowerPrompt.contains("time") || prompt.contains("时间") || prompt.contains("几点");
    }
}
