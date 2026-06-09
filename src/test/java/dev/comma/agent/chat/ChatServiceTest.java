package dev.comma.agent.chat;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;

class ChatServiceTest {

    private final ChatService chatService = new ChatService(
            Clock.fixed(Instant.parse("2026-06-09T10:00:00Z"), ZoneOffset.UTC),
            new ToolRegistry(List.of(new TimeTool())));

    @Test
    void repliesWithPromptWhenProvided() {
        ChatResponse response = chatService.reply("hello agent");

        assertThat(response.message()).isEqualTo("Day 3 mock agent received: hello agent");
        assertThat(response.generatedAt()).isNotNull();
    }

    @Test
    void usesDefaultPromptWhenBlank() {
        ChatResponse response = chatService.reply(" ");

        assertThat(response.message()).contains("介绍一下 Java Agent");
    }

    @Test
    void callsTimeToolWhenPromptAsksForTime() {
        ChatResponse response = chatService.reply("现在几点");

        assertThat(response.message()).isEqualTo("Tool[time] current time: 2026-06-09T10:00:00Z");
    }

    @Test
    void delegatesToolSelectionToRegistry() {
        ToolRegistry registry = new ToolRegistry(List.of(new TimeTool()));

        assertThat(registry.executeFirstSupported("what time is it", Instant.parse("2026-06-09T10:00:00Z").atOffset(ZoneOffset.UTC)))
                .contains("Tool[time] current time: 2026-06-09T10:00:00Z");
    }
}
