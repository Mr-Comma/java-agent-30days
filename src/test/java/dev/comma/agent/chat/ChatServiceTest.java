package dev.comma.agent.chat;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ChatServiceTest {

    private final ChatService chatService = new ChatService();

    @Test
    void repliesWithPromptWhenProvided() {
        ChatResponse response = chatService.reply("hello agent");

        assertThat(response.message()).isEqualTo("Day 2 mock agent received: hello agent");
        assertThat(response.generatedAt()).isNotNull();
    }

    @Test
    void usesDefaultPromptWhenBlank() {
        ChatResponse response = chatService.reply(" ");

        assertThat(response.message()).contains("介绍一下 Java Agent");
    }
}
